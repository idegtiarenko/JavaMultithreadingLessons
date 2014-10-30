package com.gman.queue;

import java.util.AbstractQueue;
import java.util.Iterator;

/**
 * @author ydegtyarenko
 * @since 10/30/14
 */
public class SingleProducerSingleConsumerQueue<T> extends AbstractQueue<T> {

	public static final int SIZE = 2 << 20;

	private final T[] data = (T[]) new Object[SIZE];
	private volatile int incomeIndex = 0;
	private int outcomeIndex = 0;

	@Override
	public boolean offer(T t) {
		data[incomeIndex] = t;
		incomeIndex = calculateNextIndex(incomeIndex);
		return true;
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
		return (currentIndex + 1) % SIZE;
	}
}
