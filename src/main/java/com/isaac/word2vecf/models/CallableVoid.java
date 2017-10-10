package com.isaac.word2vecf.models;

import java.util.concurrent.Callable;

/** Utility base implementation of Callable with a Void return type. */
public abstract class CallableVoid implements Callable<Void> {

	public final Void call() throws Exception {
		run();
		return null;
	}

	/** Do the actual work here instead of using previous one */
	protected abstract void run() throws Exception;
}
