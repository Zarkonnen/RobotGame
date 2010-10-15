package com.metalbeetle.bg2.activity;

import com.metalbeetle.bg2.Feedbacker;
import com.metalbeetle.bg2.World;
import com.metalbeetle.bg2.item.Part;

public interface Activity {
	public String getName();
	public int getPriority();
	public boolean update(World world, Part p, int delta, Feedbacker f);
	public void cancel();
}
