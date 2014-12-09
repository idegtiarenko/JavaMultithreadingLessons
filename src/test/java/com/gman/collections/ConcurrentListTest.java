package com.gman.collections;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * @author gman
 */
@Ignore("Adding may be reordered")
public class ConcurrentListTest {

	@Test
	public void testCorrectness() {

		ConcurrentList<Integer> testing = new ConcurrentList<>();

		AtomicInteger inserting = new AtomicInteger();
		CountDownLatch latch = new CountDownLatch(1);
		ExecutorService executors = Executors.newFixedThreadPool(6);

		List<Future<?>> futures = Arrays.asList(
				executors.submit(new Adder(latch, testing, inserting)),
				executors.submit(new Adder(latch, testing, inserting)),
				executors.submit(new Adder(latch, testing, inserting)),
				executors.submit(new RandomReader(latch, testing)),
				executors.submit(new RandomReader(latch, testing)),
				executors.submit(new RandomReader(latch, testing))
		);

		latch.countDown();

		for (Future<?> future : futures) {
			try {
				future.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		executors.shutdown();
	}

	private static final class Adder implements Runnable {

		private final CountDownLatch latch;
		private final List<Integer> testing;
		private final AtomicInteger inserting;

		private Adder(CountDownLatch latch, List<Integer> testing, AtomicInteger inserting) {
			this.latch = latch;
			this.testing = testing;
			this.inserting = inserting;
		}

		@Override
		public void run() {
			try {
				latch.await();
				for (int i = 0; i < 10000; i++) {
					testing.add(inserting.incrementAndGet());
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private static final class RandomReader implements Runnable {

		private final CountDownLatch latch;
		private final List<Integer> testing;
		private final Random random = new Random();

		private RandomReader(CountDownLatch latch, List<Integer> testing) {
			this.latch = latch;
			this.testing = testing;
		}

		@Override
		public void run() {
			try {
				latch.await();
				for (int i = 0; i < 10000; i++) {
					if (testing.size() == 0) {
						continue;
					}
					int random = this.random.nextInt(testing.size());
					assertEquals(random, testing.get(random).intValue());
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
