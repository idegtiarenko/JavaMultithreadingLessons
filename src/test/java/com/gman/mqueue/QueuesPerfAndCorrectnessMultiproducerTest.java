package com.gman.mqueue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author Andrey Lomakin <a href="mailto:lomakin.andrey@gmail.com">Andrey Lomakin</a>, ydegtyarenko
 * @since 10/26/14
 */
public class QueuesPerfAndCorrectnessMultiproducerTest {

	private static final int PRODUCERS = 2;
	private static final int TIMEOUT_IN_SECONDS = 45;

	private static final int WARMUP = 500000;
	private static final int REPETITIONS = 10000000;
	private final ExecutorService executorService = Executors.newFixedThreadPool(PRODUCERS);

	@Test(timeout = TIMEOUT_IN_SECONDS * 1000)
	public void testQueue10() throws Exception {
		Queue<Integer> queue = new Queue10<>();
		perfTest(queue);
	}

	private void perfTest(Queue<Integer> queue) throws Exception {
		performanceRun(true, queue);
		final long duration = performanceRun(false, queue);


		final long ops = (REPETITIONS * 1000L * 1000L * 1000L) / duration;
		System.out.format("ops/sec=%,d - %s\n", ops, queue.getClass().getSimpleName());
	}


	private long performanceRun(boolean isWarmUp, final Queue<Integer> queue) throws Exception {
		List<Future<Void>> producers = new ArrayList<>();
		for (int p = 0; p < PRODUCERS; p++) {
			producers.add(executorService.submit(new Producer(queue, isWarmUp)));
		}
		int i = isWarmUp ? WARMUP : REPETITIONS;
		i *= PRODUCERS;
		final long start = isWarmUp ? 0 : System.nanoTime();

		do {
			Integer val;
			while ((val = queue.poll()) == null);
		} while (0 != --i);

		for (int p = 0; p < PRODUCERS; p++) {
			producers.get(p).get();
		}

		final long end = isWarmUp ? 0 : System.nanoTime();

		return end - start;
	}

	public final class Producer implements Callable<Void> {
		private final Queue<Integer> queue;
		private final boolean isWarmUp;

		public Producer(Queue<Integer> queue, boolean isWarmUp) {
			this.queue = queue;
			this.isWarmUp = isWarmUp;
		}

		@Override
		public Void call() throws Exception {
			int i = isWarmUp ? WARMUP : REPETITIONS;
			do {
				while (!queue.offer(i)) ;
			} while (0 != --i);

			return null;
		}
	}
}