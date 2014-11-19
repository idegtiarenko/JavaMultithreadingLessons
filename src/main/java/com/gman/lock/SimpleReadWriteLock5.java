package com.gman.lock;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 *
 * @author gman
 */
public class SimpleReadWriteLock5 implements ReadWriteLock {

	private final Queue<Thread> sleeping = new ConcurrentLinkedQueue<>();

	private final AtomicInteger readers = new AtomicInteger();
	private final AtomicInteger writers = new AtomicInteger();

	@Override
	public void acquireReadLock() {
		Thread current = Thread.currentThread();
		while (true) {
			readers.incrementAndGet();
			if (writers.get() > 0) {
				if (sleeping.offer(current)) {
					readers.decrementAndGet();
					if (writers.get() > 0) {
						LockSupport.park(this);
					}
				} else {
					readers.decrementAndGet();
				}
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
		while (!sleeping.isEmpty()) {
			LockSupport.unpark(sleeping.poll());
		}
	}
}