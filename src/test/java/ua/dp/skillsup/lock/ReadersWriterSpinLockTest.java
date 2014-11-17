package ua.dp.skillsup.lock;

import com.gman.lock.ReadWriteLock;
import com.gman.lock.SimpleReadWriteLock3;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ReadersWriterSpinLockTest {
	private final CountDownLatch latch = new CountDownLatch(1);

	private final AtomicLong readers = new AtomicLong();
	private final AtomicLong writers = new AtomicLong();

	private final AtomicLong readersCounter = new AtomicLong();
	private final AtomicLong writersCounter = new AtomicLong();

	private final ReadWriteLock spinLock = new SimpleReadWriteLock3();

	private volatile boolean stop = false;

	private final ExecutorService executorService = Executors.newCachedThreadPool();

	private volatile long c = 47;

	@Test
	public void testCompetingAccess() throws Exception {
		List<Future> futures = new ArrayList<Future>();
		int threads = 8;

		for (int i = 0; i < threads; i++)
			futures.add(executorService.submit(new Writer()));

		for (int i = 0; i < threads; i++)
			futures.add(executorService.submit(new Reader()));

		latch.countDown();
		Thread.sleep(60 * 1000);

		stop = true;

		for (Future future : futures)
			future.get();

		System.out.println("Writes : " + writers.get());
		System.out.println("Reads : " + readers.get());
	}

	private final class Reader implements Callable<Void> {
		@Override
		public Void call() throws Exception {
			latch.await();

			try {
				while (!stop) {
					spinLock.acquireReadLock();
					try {
//						spinLock.acquireReadLock();
//						try {
							readersCounter.incrementAndGet();
							readers.incrementAndGet();
							consumeCPU(100);
							readersCounter.decrementAndGet();
//						} finally {
//							spinLock.releaseReadLock();
//						}
					} finally {
						spinLock.releaseReadLock();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			return null;
		}
	}

	private final class Writer implements Callable<Void> {
		@Override
		public Void call() throws Exception {
			latch.await();

			try {
				while (!stop) {
					spinLock.acquireWriteLock();
					try {
//						spinLock.acquireWriteLock();
//						try {
//							spinLock.acquireReadLock();
//							try {
								writers.incrementAndGet();
								writersCounter.incrementAndGet();

								Assert.assertEquals(0, readersCounter.get());

								long rCounter = readersCounter.get();
								long wCounter = writersCounter.get();

								Assert.assertEquals(0, rCounter);
								Assert.assertEquals(1, wCounter);

								consumeCPU(1000);

								Assert.assertEquals(readersCounter.get(), rCounter);
								Assert.assertEquals(writersCounter.get(), wCounter);

								writersCounter.decrementAndGet();
//							} finally {
//								spinLock.releaseReadLock();
//							}
//						} finally {
//							spinLock.releaseWriteLock();
//						}
					} finally {
						spinLock.releaseWriteLock();
					}

					consumeCPU(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			return null;
		}
	}

	private void consumeCPU(int cycles) {
		long c1 = c;
		for (int i = 0; i < cycles; i++) {
			c1 += c1 * 31 + i * 51;
		}
		c = c1;
	}
}