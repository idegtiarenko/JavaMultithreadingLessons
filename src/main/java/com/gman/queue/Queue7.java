package com.gman.queue;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Single producer
 * Single consumer
 * Elements in queue are NOT padded to prevent false sharing
 * Volatiles has been replaced with AtomicLong (simplify overflow checks)
 * Atomics are NOT padded to prevent false sharing
 * Using caching variables to prevent volatile read
 *
 * @author ydegtyarenko
 * @since 10/30/14
 */
public class Queue7<T> extends AbstractQueue<T> {

	public static final int SIZE = 2 << 20;

	private final T[] data = (T[]) new Object[SIZE];

	private final AtomicLong incomeIndex = new AtomicLong();
	private final AtomicLong outcomeIndex = new AtomicLong();

    private long lastKnownIncome = 0;
    private long lastKnownOutcome = 0;

	@Override
	public boolean offer(T t) {
        long currentIncome = incomeIndex.get();
        if (currentIncome - lastKnownOutcome >= SIZE) {
            lastKnownOutcome = outcomeIndex.get();
            if (currentIncome - lastKnownOutcome >= SIZE) {
                return false;
            }
        }
        data[calculateIndex(currentIncome)] = t;
        incomeIndex.lazySet(currentIncome + 1);
        return true;
	}

	@Override
	public T poll() {
        long currentOutcome = outcomeIndex.get();
        if (currentOutcome >= lastKnownIncome) {
            lastKnownIncome = incomeIndex.get();
            if (currentOutcome >= lastKnownIncome) {
                return null;
            }
        }
        T t = data[calculateIndex(currentOutcome)];
        outcomeIndex.lazySet(currentOutcome + 1);
        return t;
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
		return (int) currentIndex % SIZE;
	}
}
