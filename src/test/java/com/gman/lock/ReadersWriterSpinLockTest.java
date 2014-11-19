package com.gman.lock;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ReadersWriterSpinLockTest {

	@Test
	public void testJdkReadWriteLockAdapter() throws Exception {
		runPerformanceTest(new JdkReadWriteLockAdapter());
	}

	@Test
	public void testSimpleReadWriteLock1() throws Exception {
		runPerformanceTest(new SimpleReadWriteLock1());
	}

	@Test
	public void testSimpleReadWriteLock2() throws Exception {
		runPerformanceTest(new SimpleReadWriteLock2());
	}

	@Test
	public void testSimpleReadWriteLock3() throws Exception {
		runPerformanceTest(new SimpleReadWriteLock3());
	}

	@Test
	public void testSimpleReadWriteLock4() throws Exception {
		runPerformanceTest(new SimpleReadWriteLock4());
	}

	@Test
	public void testSimpleReadWriteLock5() throws Exception {
		runPerformanceTest(new SimpleReadWriteLock5());
	}

	@Test
	public void testSimpleReadWriteLock6() throws Exception {
		runPerformanceTest(new SimpleReadWriteLock6());
	}

	private void runPerformanceTest(ReadWriteLock lock) throws Exception {

		TestState state = new TestState(lock);
		ExecutorService executorService = Executors.newCachedThreadPool();
		List<Future> futures = new ArrayList<>();
		int threads = 8;

		for (int i = 0; i < threads; i++) {
			futures.add(executorService.submit(new Writer(state)));
		}

		for (int i = 0; i < threads; i++) {
			futures.add(executorService.submit(new Reader(state)));
		}

		state.latch.countDown();
		Thread.sleep(60 * 1000);

		state.stop = true;

		for (Future future : futures) {
			future.get();
		}

		executorService.shutdown();

		System.out.println("Class: " + lock.getClass().getName());
		System.out.println("Writes : " + state.writers.get());
		System.out.println("Reads : " + state.readers.get());
	}

	private static final class TestState {
		private final CountDownLatch latch = new CountDownLatch(1);

		private final AtomicLong readers = new AtomicLong();
		private final AtomicLong writers = new AtomicLong();

		private final AtomicLong readersCounter = new AtomicLong();
		private final AtomicLong writersCounter = new AtomicLong();

		private volatile boolean stop = false;

		private final ReadWriteLock lock;

		private TestState(ReadWriteLock lock) {
			this.lock = lock;
		}
	}

	private static final class Reader implements Callable<Void> {

		private final TestState state;

		private Reader(TestState state) {
			this.state = state;
		}

		@Override
		public Void call() throws Exception {
			state.latch.await();

			try {
				while (!state.stop) {
					state.lock.acquireReadLock();
					try {
						state.readersCounter.incrementAndGet();
						state.readers.incrementAndGet();
						consumeCPU(100);
						state.readersCounter.decrementAndGet();
					} finally {
						state.lock.releaseReadLock();
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

		private final TestState state;

		private Writer(TestState state) {
			this.state = state;
		}

		@Override
		public Void call() throws Exception {
			state.latch.await();

			try {
				while (!state.stop) {
					state.lock.acquireWriteLock();
					try {
						state.writers.incrementAndGet();
						state.writersCounter.incrementAndGet();

						Assert.assertEquals(0, state.readersCounter.get());

						long rCounter = state.readersCounter.get();
						long wCounter = state.writersCounter.get();

						Assert.assertEquals(0, rCounter);
						Assert.assertEquals(1, wCounter);

						consumeCPU(1000);

						Assert.assertEquals(rCounter, state.readersCounter.get());
						Assert.assertEquals(wCounter, state.writersCounter.get());

						state.writersCounter.decrementAndGet();
					} finally {
						state.lock.releaseWriteLock();
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

	private static volatile long c = 47;

	private static void consumeCPU(int cycles) {
		long c1 = c;
		for (int i = 0; i < cycles; i++) {
			c1 += c1 * 31 + i * 51;
		}
		c = c1;
	}
}