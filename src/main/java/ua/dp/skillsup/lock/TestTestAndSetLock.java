package ua.dp.skillsup.lock;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Andrey Lomakin <a href="mailto:lomakin.andrey@gmail.com">Andrey Lomakin</a>
 * @since 10/5/14
 */
public class TestTestAndSetLock implements Lock {

	private AtomicBoolean state = new AtomicBoolean(false);

	@Override
	public void lock() {
		while (true) {
			while (state.get()) {};
			if (!state.getAndSet(true))
				return;
		}
	}

	@Override
	public void unlock() {
		state.set(false);
	}
}