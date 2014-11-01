package com.gman.queue;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Single producer
 * Single consumer
 * Elements in queue are padded to prevent false sharing
 * Volatiles has been replaced with AtomicLong (simplify overflow checks)
 * Atomics are padded to prevent false sharing
 *
 * @author ydegtyarenko
 * @since 10/30/14
 */
public class Queue6<T> extends AbstractQueue<T> {

	public static final int SIZE = 2 << 12;
	public static final int CONTENDED_STEP = 32;

	private final T[] data = (T[]) new Object[SIZE];

	private final AtomicLong incomeIndex = new AtomicLongWithPadding();
	private final AtomicLong outcomeIndex = new AtomicLongWithPadding();

	@Override
	public boolean offer(T t) {
		long currentIncome = incomeIndex.get();
		if (currentIncome - outcomeIndex.get() >= SIZE) {
			return false;
		}
		data[calculateIndex(currentIncome)] = t;
		incomeIndex.lazySet(currentIncome + CONTENDED_STEP);
		return true;
	}

	@Override
	public T poll() {
		long currentOutcome = outcomeIndex.get();
		if (currentOutcome >= incomeIndex.get()) {
			return null;
		}
		T t = data[calculateIndex(currentOutcome)];
		outcomeIndex.lazySet(currentOutcome + CONTENDED_STEP);
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
		return (int)(currentIndex % SIZE);
	}

    private static final class AtomicLongWithPadding extends AtomicLong {
        public final long[] padding = new long[32];
    }
}
