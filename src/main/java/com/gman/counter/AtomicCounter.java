package com.gman.counter;

import ua.dp.skillsup.counter.Counter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Andrey Lomakin <a href="mailto:lomakin.andrey@gmail.com">Andrey Lomakin</a>
 * @since 03/11/14
 */
public class AtomicCounter implements Counter {

	private final AtomicLong counter = new AtomicLong();

	@Override
	public void inc() {
		counter.incrementAndGet();
	}

	@Override
	public long get() {
		return counter.get();
	}
}