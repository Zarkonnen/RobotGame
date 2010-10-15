package com.metalbeetle.bg2.item;

public interface ChangeStrategy<T> {
	public void change(Stat<T> k, T delta, Part p);
}
