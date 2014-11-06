package ua.dp.skillsup.counter;

import com.gman.counter.Counter3;
import com.gman.counter.AtomicCounter;
import com.gman.counter.Counter2;
import com.gman.counter.Counter1;

/**
 * @author Andrey Lomakin <a href="mailto:lomakin.andrey@gmail.com">Andrey Lomakin</a>
 * @since 03/11/14
 */
public class CounterFactory {
	public enum CounterType {
		ATOMIC, COUNTER_1, COUNTER_2, COUNTER_3
	}

	public static Counter build(CounterType type) {
		switch (type) {
			case ATOMIC:
				return new AtomicCounter();
			case COUNTER_1:
				return new Counter1();
			case COUNTER_2:
				return new Counter2();
			case COUNTER_3:
				return new Counter3();
			default:
				throw new IllegalArgumentException();
		}
	}
}