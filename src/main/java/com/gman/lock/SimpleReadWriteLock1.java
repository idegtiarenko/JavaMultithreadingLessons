package com.gman.lock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * There are about 2 reads on each write
 *
 * @author gman
 */
public class SimpleReadWriteLock1 implements ReadWriteLock {


	private final AtomicInteger readers = new AtomicInteger();
	private final AtomicInteger writer = new AtomicInteger();

	//blocking acquire read lock
	@Override
	public void acquireReadLock() {
		while (!writer.compareAndSet(0, 1));
		readers.incrementAndGet();
		writer.set(0);
	}

	@Override
	public void releaseReadLock() {
		readers.decrementAndGet();
	}

	@Override
	public void acquireWriteLock() {
		while (!writer.compareAndSet(0, 1));
		while (readers.get() > 0);
	}

	@Override
	public void releaseWriteLock() {
		writer.set(0);
	}

}