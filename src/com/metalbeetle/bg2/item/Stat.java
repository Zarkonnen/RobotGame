package com.metalbeetle.bg2.item;

public interface Stat<T> {
	public String getName();
	public CompositStrategy compositFrom();
	public ChangeStrategy changer();
	public T composit(T a, T b);
	public T defaultValue();
}
