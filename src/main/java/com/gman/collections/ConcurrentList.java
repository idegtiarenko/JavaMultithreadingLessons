package com.gman.collections;

import java.util.AbstractList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * @author gman
 */
public class ConcurrentList<T> extends AbstractList<T> {

	/**
	 * []
	 * [][]
	 * [][][][]
	 * [][][][][][][][]
	 * ...
	 */
	private static final int MAX_MINOR_INDEX = 31;
	private final AtomicReferenceArray<AtomicReferenceArray<T>> data = new AtomicReferenceArray<>(MAX_MINOR_INDEX);
	private final AtomicInteger indexGenerator = new AtomicInteger();
	private final AtomicInteger size = new AtomicInteger();

	@Override
	public boolean add(T t) {
		int index = indexGenerator.incrementAndGet();
		int major = calculateMajorIndex(index);
		int minor = calculateMinorIndex(index);
		while (
				data.get(major) == null &&
				!data.compareAndSet(major, null, new AtomicReferenceArray<T>(sizeOfMinorArray(index)))
		);
		data.get(major).set(minor, t);
		size.incrementAndGet();
		return true;
	}

	@Override
	public T set(int index, T element) {
		if (index < 0 || index > size.get()) {
			throw new IndexOutOfBoundsException();
		}
		int major = calculateMajorIndex(index+1);
		int minor = calculateMinorIndex(index+1);
		T t = data.get(major).get(minor);
		data.get(major).set(minor, element);
		return t;
	}

	@Override
	public T get(int index) {
		if (index < 0 || index > size.get()) {
			throw new IndexOutOfBoundsException();
		}
		int major = calculateMajorIndex(index+1);
		int minor = calculateMinorIndex(index+1);
		return data.get(major).get(minor);
	}

	@Override
	public int size() {
		return size.get();
	}

	private static int calculateMajorIndex(int index) {
		return MAX_MINOR_INDEX - Integer.numberOfLeadingZeros(index);
	}

	private static int calculateMinorIndex(int index) {
		return index - Integer.highestOneBit(index);
	}

	private static int sizeOfMinorArray(int index) {
		return Integer.highestOneBit(index);
	}
}
