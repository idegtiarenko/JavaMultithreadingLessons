package com.gman.counter;

import ua.dp.skillsup.counter.Counter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Works like ConcurrentHashMap
 * Works fine in case there are less then PARALLELS threads using it
 */
public class Counter2 implements Counter {

	private static final int PARALLELS = 32;
	private static final int MASK = PARALLELS - 1;

	private final AtomicLong[] counters = new AtomicLong[PARALLELS];

	public Counter2() {
		for (int i = 0; i < PARALLELS; i++) {
			counters[i] = new AtomicLong();
		}
	}

	@Override
	public void inc() {
		final int hashCode = Thread.currentThread().hashCode();
		counters[hashCode & MASK].incrementAndGet();
	}

	@Override
	public long get() {
		long count = 0;
		for (AtomicLong counter : counters) {
			count += counter.get();
		}
		return count;
	}
}
