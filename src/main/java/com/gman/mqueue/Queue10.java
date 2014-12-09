package com.gman.mqueue;

import sun.misc.Contended;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.LockSupport;

/**
 * Multi producer!!!
 * Single consumer
 * Elements in queue are NOT padded to prevent false sharing
 * Volatiles has been replaced with AtomicLong (simplify overflow checks)
 * Atomics are NOT padded to prevent false sharing
 * Using caching variables to prevent volatile read
 * & is much faster then % on Intel i7 processors
 *
 * @author ydegtyarenko
 * @since 10/30/14
 */
public class Queue10<T> extends AbstractQueue<T> {

	public static final int SIZE = 2 << 20;
	public static final int MASK = SIZE - 1;
	public static final int RETRIES = 10;

	private final AtomicReferenceArray<T> data = new AtomicReferenceArray<T>(SIZE);

	private final AtomicLong incomeIndex = new AtomicLong();
	private final AtomicLong outcomeIndex = new AtomicLong();

	@Contended
	private long lastKnownIncome = 0;
	@Contended
	private volatile long lastKnownOutcome = 0;


	@Override
	public boolean offer(T t) {
		for (int i = 1; true; i++) {
			long currentIncome = incomeIndex.get();
			if (currentIncome - lastKnownOutcome >= SIZE) {
				lastKnownOutcome = outcomeIndex.get();
				if (currentIncome - lastKnownOutcome >= SIZE) {
					return false;
				}
			}
			if (incomeIndex.compareAndSet(currentIncome, currentIncome + 1)) {
				data.set(calculateIndex(currentIncome), t);
				return true;
			} else if (i % RETRIES == 0) {
				LockSupport.parkNanos(this, 1);
			}
		}
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
		int realIndex = calculateIndex(currentOutcome);
		T t = data.get(realIndex);
		if (t == null) {
			return null;
		}

		data.set(realIndex, null);
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
		return (int)(currentIndex & MASK);
	}
}
