package com.gman.adder;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author ydegtyarenko
 * @since 10/30/14
 */
public class SimpleThreadLocalAdder {

	private final Collection<CounterHolder> total = new CopyOnWriteArrayList<>();
	private final ThreadLocal<CounterHolder> threadLocalCounter = new ThreadLocal<CounterHolder>() {
		@Override
		protected CounterHolder initialValue() {
			CounterHolder holder = new CounterHolder();
			total.add(holder);
			return holder;
		}
	};

	public void inc() {
		//TODO partial publication on 32bit
		//TODO late publication???
		threadLocalCounter.get().count++;
	}

	public long count() {
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
