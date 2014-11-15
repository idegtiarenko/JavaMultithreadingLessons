package com.gman.lock;

public interface ReadWriteLock {

	void acquireReadLock();
	void releaseReadLock();
	void acquireWriteLock();
	void releaseWriteLock();
}
