package com.metalbeetle.bg2.item;

import com.metalbeetle.bg2.activity.Activity;
import java.util.Comparator;

public class PriorityComparator implements Comparator<Activity> {
	public int compare(Activity a1, Activity a2) {
		return a1.getPriority() - a2.getPriority();
	}
}
