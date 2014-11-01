package com.gman.queue;

import java.util.AbstractQueue;
import java.util.Iterator;

/**
 * Single producer
 * Single consumer
 * Elements in queue are padded to prevent false sharing
 *
 * @author ydegtyarenko
 * @since 10/30/14
 */
public class Queue3<T> extends AbstractQueue<T> {

	public static final int SIZE = 2 << 20;
	public static final int CONTENDED_STEP = 32;

	private final T[] data = (T[]) new Object[SIZE];
	private volatile int incomeIndex = 0;
	private volatile int outcomeIndex = 0;

	@Override
	public boolean offer(T t) {
		int possibleNext = calculateNextIndex(incomeIndex);
		if (possibleNext == outcomeIndex) {
			return false;
		} else {
			data[incomeIndex] = t;
			incomeIndex = possibleNext;
			return true;
		}
	}

	@Override
	public T poll() {
		if (outcomeIndex == incomeIndex) {
			return null;
		} else {
			T t = data[outcomeIndex];
			outcomeIndex = calculateNextIndex(outcomeIndex);
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
