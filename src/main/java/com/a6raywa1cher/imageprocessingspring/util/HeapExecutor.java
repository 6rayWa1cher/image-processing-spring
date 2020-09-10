package com.a6raywa1cher.imageprocessingspring.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class HeapExecutor implements Executor {
	private final Executor executor = Executors.newSingleThreadExecutor();
	private final ReentrantLock lock = new ReentrantLock();
	private volatile Runnable head;
	private volatile boolean running;

	@Override
	public void execute(Runnable command) {
		lock.lock();
		try {
			head = command;
			if (!running) {
				running = true;
				executor.execute(() -> {
					while (true) {
						Runnable runnable;
						lock.lock();
						try {
							runnable = head;
							if (runnable == null) {
								running = false;
								break;
							}
						} finally {
							lock.unlock();
						}
						runnable.run();
					}
				});
			}
		} finally {
			lock.unlock();
		}
	}
}
