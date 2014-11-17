package com.gman.lock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author gman
 */
public class SimpleReadWriteLock3 implements ReadWriteLock {

	private final AtomicInteger readers = new AtomicInteger();
	private final AtomicInteger writers = new AtomicInteger();

	@Override
	public void acquireReadLock() {
		while (true) {
			readers.incrementAndGet();
			if (writers.get() > 0) {
				readers.decrementAndGet();
			} else {
				break;
			}
		}
	}

	@Override
	public void releaseReadLock() {
		readers.decrementAndGet();
	}

	@Override
	public void acquireWriteLock() {
		while (!writers.compareAndSet(0, 1));
		while (readers.get() > 0);
	}

	@Override
	public void releaseWriteLock() {
		writers.set(0);
	}
}