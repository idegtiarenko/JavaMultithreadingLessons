package com.gman.lock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author gman
 */
public class SimpleReadWriteLock2 implements ReadWriteLock {

	private final AtomicLong opsCount = new AtomicLong();

	private final AtomicInteger readers = new AtomicInteger();
	private final AtomicInteger writer = new AtomicInteger();

	@Override
	public void acquireReadLock() {
		opsCount.incrementAndGet();
		readers.incrementAndGet();
		while (writer.get() > 0);
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
			while (writer.compareAndSet(0, 1));

			if (opsCount.compareAndSet(ops, ops+1)) {
				break;
			} else {
				writer.set(0);
			}
		}
	}

	@Override
	public void releaseWriteLock() {
		writer.set(0);
	}
}