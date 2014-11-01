package com.gman.queue;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Single producer
 * Single consumer
 * Elements in queue are padded to prevent false sharing
 * Volatiles has been replaced with AtomicInteger (one of the updates is lazy)
 *
 * @author ydegtyarenko
 * @since 10/30/14
 */
public class Queue4<T> extends AbstractQueue<T> {

	public static final int SIZE = 2 << 20;
	public static final int CONTENDED_STEP = 32;

	private final T[] data = (T[]) new Object[SIZE];
	private final AtomicInteger incomeIndex = new AtomicInteger();
	private final AtomicInteger outcomeIndex = new AtomicInteger();

	@Override
	public boolean offer(T t) {
		int currentIncome = incomeIndex.get();
		int possibleNext = calculateNextIndex(currentIncome);
		if (possibleNext == outcomeIndex.get()) {
			return false;
		} else {
			data[currentIncome] = t;
			incomeIndex.lazySet(possibleNext);
			return true;
		}
	}

	@Override
	public T poll() {
		int currentIndex = outcomeIndex.get();
		if (currentIndex == incomeIndex.get()) {
			return null;
		} else {
			T t = data[currentIndex];
			outcomeIndex.set(calculateNextIndex(currentIndex));
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

	private int calculateNextIndex(int currentIndex) {
		return (currentIndex + CONTENDED_STEP) % SIZE;
	}
}
