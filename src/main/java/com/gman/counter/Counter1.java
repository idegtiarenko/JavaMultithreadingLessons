package com.gman.counter;

import ua.dp.skillsup.counter.Counter;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Works fine in thread pools
 * Consumes a lot of memory in case threads are created and destroyed each time
 */
public class Counter1 implements Counter {

	private final Collection<CounterHolder> total = new CopyOnWriteArrayList<CounterHolder>();
	private final ThreadLocal<CounterHolder> threadLocalCounter = new ThreadLocal<CounterHolder>() {
		@Override
		protected CounterHolder initialValue() {
			CounterHolder holder = new CounterHolder();
			total.add(holder);
			return holder;
		}
	};

	@Override
	public void inc() {
		//TODO partial publication on 32bit
		//TODO late publication???
		threadLocalCounter.get().count++;
	}

	@Override
	public long get() {
		long totalCount = 0;
		for (CounterHolder counterHolder : total) {
			totalCount += counterHolder.count;
		}
		return totalCount;
	}

	private static final class CounterHolder {

		private long count = 0;
	}
}
