package com.gman.counter;

import ua.dp.skillsup.counter.Counter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Uses custom hashCode to be better distributed
 * Uses custom simple hashMap that increase its capacity in case of hi load
 */
public class Counter4 implements Counter {

	private static final int TRIES = 3;

	private volatile AtomicLong[] counters = new AtomicLong[8];
	private final AtomicBoolean isAnyOneUpdatingArray = new AtomicBoolean(false);

	private ThreadLocal<Integer> veryGoodHashCode = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return nextHashCode();//Object#hashCode() does not give guarantee to be well distributed
		}
	};

	public Counter4() {
		for (int i = 0; i < counters.length; i++) {
			counters[i] = new AtomicLong();
		}
	}

	@Override
	public void inc() {

		AtomicLong[] currentCounters = counters;

		AtomicLong counter = currentCounters[veryGoodHashCode.get() & currentCounters.length - 1];
		for (int i = 0; i < TRIES; i++) {
			long expected = counter.get();
			if (counter.compareAndSet(expected, expected+1)) {
				return;
			}
		}

		if (isAnyOneUpdatingArray.compareAndSet(false, true)) {
			//really need to be volatile only here.
			//if you will be able to put memory barer here then counters may not be volatile

			AtomicLong[] newCounters = new AtomicLong[currentCounters.length * 2];
			System.arraycopy(currentCounters, 0, newCounters, 0, currentCounters.length);
			for (int i = currentCounters.length; i < newCounters.length; i++) {
				newCounters[i] = new AtomicLong();
			}
			counters = newCounters;

			isAnyOneUpdatingArray.set(false);
		}

		inc();
	}

	public void dec() {

		AtomicLong[] currentCounters = counters;

		AtomicLong counter = currentCounters[veryGoodHashCode.get() & currentCounters.length - 1];
		for (int i = 0; i < TRIES; i++) {
			long expected = counter.get();
			if (counter.compareAndSet(expected, expected-1)) {
				return;
			}
		}

		if (isAnyOneUpdatingArray.compareAndSet(false, true)) {
			//really need to be volatile only here.
			//if you will be able to put memory barer here then counters may not be volatile

			AtomicLong[] newCounters = new AtomicLong[currentCounters.length * 2];
			System.arraycopy(currentCounters, 0, newCounters, 0, currentCounters.length);
			for (int i = currentCounters.length; i < newCounters.length; i++) {
				newCounters[i] = new AtomicLong();
			}
			counters = newCounters;

			isAnyOneUpdatingArray.set(false);
		}

		dec();
	}



	@Override
	public long get() {
		AtomicLong[] currentCounters = counters;
		long count = 0;
		for (AtomicLong counter : currentCounters) {
			count += counter.get();
		}
		return count;
	}

	private static AtomicInteger nextHashCode = new AtomicInteger();
	private static final int HASH_INCREMENT = 0x61c88647;
	private static int nextHashCode() {
		return nextHashCode.getAndAdd(HASH_INCREMENT);
	}
}
