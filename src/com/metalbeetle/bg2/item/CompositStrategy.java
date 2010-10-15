package com.metalbeetle.bg2.item;

public interface CompositStrategy {
	public <T> T composit(Stat<T> k, Part p);
}
