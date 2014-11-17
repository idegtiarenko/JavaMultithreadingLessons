package com.gman.lock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * There are almost 1M reads on each write :(
 *
 * @author gman
 */
public class SimpleReadWriteLock2 implements ReadWriteLock {

	private final AtomicLong opsCount = new AtomicLong();

	private final AtomicInteger readers = new AtomicInteger();
	private final AtomicInteger writers = new AtomicInteger();

	@Override
	public void acquireReadLock() {
		readers.incrementAndGet();
		opsCount.incrementAndGet();
		while (writers.get() > 0);
	}

	@Override
	public void releaseReadLock() {
		readers.decrementAndGet();
	}

	@Override
	public void acquireWriteLock() {
		long ops;

		while(true) {
			ops = opsCount.get();

			while (readers.get() > 0);
			while (!writers.compareAndSet(0, 1));

			if (opsCount.compareAndSet(ops, ops+1)) {
				break;
			} else {
				writers.set(0);
			}
		}
	}

	@Override
	public void releaseWriteLock() {
		writers.set(0);
	}
}