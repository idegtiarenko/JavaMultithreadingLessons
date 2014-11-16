package com.gman.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JdkReadWriteLockAdapter implements ReadWriteLock {

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	public void acquireReadLock() {
		lock.readLock().lock();
	}

	@Override
	public void releaseReadLock() {
		lock.readLock().unlock();
	}

	@Override
	public void acquireWriteLock() {
		lock.writeLock().lock();
	}

	@Override
	public void releaseWriteLock() {
		lock.writeLock().unlock();
	}
}
