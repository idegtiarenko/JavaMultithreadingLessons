package ua.dp.skillsup;

import com.gman.queue.*;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author Andrey Lomakin <a href="mailto:lomakin.andrey@gmail.com">Andrey Lomakin</a>
 * @since 10/26/14
 */
public class QueuesPerfTest {

    private static final int TIMEOUT_IN_SECONDS = 45;

	private static final int WARMUP = 500000;
	private static final int REPETITIONS = 100000000;
	private static final Integer TEST_VALUE = 42;
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
		performanceRun(0, queue);
		final long duration = performanceRun(1, queue);


		final long ops = (REPETITIONS * 1000L * 1000L * 1000L) / duration;
		System.out.format("ops/sec=%,d - %s\n", ops, queue.getClass().getSimpleName());
	}


	private long performanceRun(int runNumber, final Queue<Integer> queue) throws Exception {
		if (runNumber == 0) {
			Future<Void> producer = executorService.submit(new Producer(queue, runNumber));

			int i = WARMUP;
			do {
				while (null == (queue.poll())) {
				}
			} while (0 != --i);
			producer.get();

			return 0;
		} else {
			Future<Void> producer = executorService.submit(new Producer(queue, runNumber));
			final long start = System.nanoTime();
			int i = REPETITIONS;

			do {
				while (null == (queue.poll())) {
				}
			} while (0 != --i);
			producer.get();

			final long duration = System.nanoTime() - start;


			return duration;
		}
	}


	public final class Producer implements Callable<Void> {
		private final Queue<Integer> queue;
		private final int runNumber;

		public Producer(Queue<Integer> queue, int runNumber) {
			this.queue = queue;
			this.runNumber = runNumber;
		}

		@Override
		public Void call() throws Exception {
			int i;
			if (runNumber == 0)
				i = WARMUP;
			else
				i = REPETITIONS;

			do {
				while (!queue.offer(TEST_VALUE)) {
				}
			} while (0 != --i);

			return null;
		}
	}
}