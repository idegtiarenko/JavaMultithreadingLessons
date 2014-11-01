package com.gman.queue;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Single producer
 * Single consumer
 * Elements in queue are padded to prevent false sharing
 * Volatiles has been replaced with AtomicLong (simplify overflow checks)
 *
 * @author ydegtyarenko
 * @since 10/30/14
 */
public class Queue5<T> extends AbstractQueue<T> {

	public static final int SIZE = 2 << 20;
	public static final int CONTENDED_STEP = 32;

	private final T[] data = (T[]) new Object[SIZE];

	private final AtomicLong incomeIndex = new AtomicLong();
	private final AtomicLong outcomeIndex = new AtomicLong();

	@Override
	public boolean offer(T t) {
		long currentIncome = incomeIndex.get();
		if (currentIncome - outcomeIndex.get() >= SIZE) {
			return false;
		} else {
			data[calculateIndex(currentIncome)] = t;
			incomeIndex.lazySet(currentIncome + CONTENDED_STEP);
			return true;
		}
	}

	@Override
	public T poll() {
		long currentOutcome = outcomeIndex.get();
		if (currentOutcome >= incomeIndex.get()) {
			return null;
		} else {
			T t = data[calculateIndex(currentOutcome)];
			outcomeIndex.lazySet(currentOutcome + CONTENDED_STEP);
			return t;
		}
	}

	@Override
	public T peek() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	private int calculateIndex(long currentIndex) {
		return (int)(currentIndex % SIZE);
	}
}
