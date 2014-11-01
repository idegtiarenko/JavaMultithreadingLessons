package com.gman.queue;

import org.junit.Assert;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author Andrey Lomakin <a href="mailto:lomakin.andrey@gmail.com">Andrey Lomakin</a>, ydegtyarenko
 * @since 10/26/14
 */
public class QueuesPerfAndCorrectnessTest {

    private static final int TIMEOUT_IN_SECONDS = 45;

	private static final int WARMUP = 500000;
	private static final int REPETITIONS = 100000000;
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();


	@Test(timeout = TIMEOUT_IN_SECONDS *1000)
	public void testArrayBlockingQueue() throws Exception {
		Queue<Integer> queue = new ArrayBlockingQueue<Integer>(1000);
		perfTest(queue);
	}

	@Test(timeout = TIMEOUT_IN_SECONDS *1000)
	public void testConcurrentLinkedQueue() throws Exception {
		Queue<Integer> queue = new ConcurrentLinkedQueue<Integer>();
		perfTest(queue);
	}

	@Test(timeout = TIMEOUT_IN_SECONDS *1000)
	public void testQueue1() throws Exception {
		Queue<Integer> queue = new Queue1<>();
		perfTest(queue);
	}

    @Test(timeout = TIMEOUT_IN_SECONDS *1000)
    public void testQueue2() throws Exception {
        Queue<Integer> queue = new Queue2<>();
        perfTest(queue);
    }

	@Test(timeout = TIMEOUT_IN_SECONDS *1000)
	public void testQueue3() throws Exception {
		Queue<Integer> queue = new Queue3<>();
		perfTest(queue);
	}

	@Test(timeout = TIMEOUT_IN_SECONDS *1000)
	public void testQueue4() throws Exception {
		Queue<Integer> queue = new Queue4<>();
		perfTest(queue);
	}

	@Test(timeout = TIMEOUT_IN_SECONDS *1000)
	public void testQueue5() throws Exception {
		Queue<Integer> queue = new Queue5<>();
		perfTest(queue);
	}

	@Test(timeout = TIMEOUT_IN_SECONDS *1000)
	public void testQueue6() throws Exception {
		Queue<Integer> queue = new Queue6<>();
		perfTest(queue);
	}

    @Test(timeout = TIMEOUT_IN_SECONDS *1000)
    public void testQueue7() throws Exception {
        Queue<Integer> queue = new Queue7<>();
        perfTest(queue);
    }

    @Test(timeout = TIMEOUT_IN_SECONDS *1000)
    public void testQueue8() throws Exception {
        Queue<Integer> queue = new Queue8<>();
        perfTest(queue);
    }

	private void perfTest(Queue<Integer> queue) throws Exception {
		performanceRun(true, queue);
		final long duration = performanceRun(false, queue);


		final long ops = (REPETITIONS * 1000L * 1000L * 1000L) / duration;
		System.out.format("ops/sec=%,d - %s\n", ops, queue.getClass().getSimpleName());
	}


	private long performanceRun(boolean isWarmUp, final Queue<Integer> queue) throws Exception {
        Future<Void> producer = executorService.submit(new Producer(queue, isWarmUp));
        int i = isWarmUp ? WARMUP : REPETITIONS;
        final long start = isWarmUp ? 0 : System.nanoTime();

        do {
            Integer val;
            while ((val = queue.poll()) == null);
            Assert.assertEquals(val.intValue(), i);
        } while (0 != --i);

        producer.get();

        final long end = isWarmUp ? 0 : System.nanoTime();

        return end-start;
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
				while (!queue.offer(i));
			} while (0 != --i);

			return null;
		}
	}
}