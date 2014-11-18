package com.gman.lock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author gman
 */
public class SimpleReadWriteLock4 implements ReadWriteLock {

	private static final int WRITING = 0x80000000;

	private final AtomicInteger counter = new AtomicInteger();

	@Override
	public void acquireReadLock() {
		while (true) {
			int current = counter.get();
			if (current != WRITING && counter.compareAndSet(current, current + 1)) {
				break;
			}
		}
	}

	@Override
	public void releaseReadLock() {
		counter.decrementAndGet();
	}

	@Override
	public void acquireWriteLock() {
		while (!counter.compareAndSet(0, WRITING));
	}

	@Override
	public void releaseWriteLock() {
		counter.set(0);
	}
}