package com.gman.lock;

import com.gman.counter.Counter4;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 *
 * @author gman
 */
public class SimpleReadWriteLock6 implements ReadWriteLock {

	private final Queue<Thread> sleeping = new ConcurrentLinkedQueue<>();

	private final Counter4 readers = new Counter4();
	private final AtomicInteger writers = new AtomicInteger();

	@Override
	public void acquireReadLock() {
		Thread current = Thread.currentThread();
		while (true) {
			readers.inc();
			if (writers.get() > 0) {
				if (sleeping.offer(current)) {
					readers.dec();
					if (writers.get() > 0) {
						LockSupport.park(this);
					}
				} else {
					readers.dec();
				}
			} else {
				break;
			}
		}
	}

	@Override
	public void releaseReadLock() {
		readers.dec();
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