package com.gman.queue;

import com.gman.counter.AtomicCounter;
import com.gman.counter.Counter1;
import com.gman.counter.Counter2;
import com.gman.counter.Counter3;
import org.junit.Test;
import ua.dp.skillsup.counter.Counter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gman
 */
public class CounterPerfTest {

	private static final int THREADS_COUNT = 1000;
	private static final int REPETITIONS = 100000;

	@Test
	public void testAtomicCounter() throws InterruptedException {
		runPerfTest(new AtomicCounter());
	}
	@Test
	public void testCounter1() throws InterruptedException {
		runPerfTest(new Counter1());
	}
	@Test
	public void testCounter2() throws InterruptedException {
		runPerfTest(new Counter2());
	}
	@Test
	public void testCounter3() throws InterruptedException {
		runPerfTest(new Counter3());
	}

	private void runPerfTest(final Counter counter) throws InterruptedException {

		final long start = System.nanoTime();

		final List<Thread> threads = new ArrayList<>(THREADS_COUNT);
		for (int i = 0; i < THREADS_COUNT; i++) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int j = 0; j < REPETITIONS; j++) {
						counter.inc();
					}
				}
			});
			thread.start();
			threads.add(thread);
		}
		for (int i = 0; i < THREADS_COUNT; i++) {
			threads.get(i).join();
		}

		assert THREADS_COUNT * REPETITIONS == counter.get();

		final long end = System.nanoTime();
		final long ops = (THREADS_COUNT * REPETITIONS * 1000L * 1000L * 1000L) / (end - start);


		System.out.format("ops/sec=%,d - %s\n", ops, counter.getClass().getSimpleName());
	}
}
