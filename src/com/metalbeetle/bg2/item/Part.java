package com.metalbeetle.bg2.item;

import com.metalbeetle.bg2.Feedbacker;
import com.metalbeetle.bg2.World;
import com.metalbeetle.bg2.activity.Activity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import static com.metalbeetle.bg2.Collections.*;

public final class Part  {
	final Map<Bucket, HashSet<Part>> buckets;
	final HashMap<Stat, Object> kv = new HashMap<Stat, Object>();
	final HashSet<Activity> activities = new HashSet<Activity>();
	Part parent = null;
	boolean dead = false;

	public Part() {
		buckets = m(
				p(Bucket.ACTIVE,   new HashSet<Part>()),
				p(Bucket.CARRIED,  new HashSet<Part>()),
				p(Bucket.ENABLED,  new HashSet<Part>()),
				p(Bucket.DISABLED, new HashSet<Part>()));
	}

	public HashSet<Part> getBucket(Bucket b) {
		return new HashSet<Part>(buckets.get(b));
	}

	public List<Activity> getActivities() {
		return new ArrayList<Activity>(activities);
	}

	public void add(Part p, Bucket b) {
		p.setParent(this);
		buckets.get(b).add(p);
	}

	public void remove(Part p) {
		for (HashSet<Part> b : buckets.values()) { b.remove(p); }
		if (p.getParent() == this) { p.setParent(null); }
	}

	public void move(Part p, Bucket dest) {
		if (find(p) == null) { return; }
		remove(p);
		add(p, dest);
	}

	public Bucket find(Part p) {
		for (Bucket b : Bucket.values()) {
			if (buckets.get(b).contains(p)) { return b; }
		}
		return null;
	}

	public Part getParent() { return parent; }
	public void setParent(Part parent) { this.parent = parent; }
	public boolean isDead() { return dead; }
	public void kill() { dead = true; }

	public <T> void set(Stat<T> k, T v) {
		if (v == null) {
			kv.remove(k);
		} else {
			kv.put(k, v);
		}
	}

	public <T> void change(Stat<T> k, T delta) {
		k.changer().change(k, delta, this);
	}

	public <T> T get(Stat<T> k) {
		return k.compositFrom().composit(k, this);
	}

	public void addActivity(Activity a) {
		activities.add(a);
	}

	public boolean update(World world, int delta, Feedbacker f) {
		for (HashSet<Part> bucket : buckets.values()) {
			for (Iterator<Part> it = bucket.iterator(); it.hasNext();) {
				if (it.next().update(world, delta, f)) { it.remove(); }
			}
		}
		ArrayList<Activity> as = new ArrayList<Activity>(activities);
		Collections.sort(as, new PriorityComparator());
		for (Activity a : as) {
			if (a.update(world, this, delta, f)) {
				activities.remove(a);
			}
		}
		return isDead();
	}

	public String fullToString() { return fullToString(""); }

	public String fullToString(String indent) {
		String s = "";
		for (Map.Entry<Stat, Object> kve : kv.entrySet()) {
			s += indent + kve.getKey().getName() + ": " + kve.getValue() + "\n";
		}
		if (activities.size() > 0) {
			s += indent + "ACTIVITIES\n";
			for (Activity a : activities) {
				s += indent + "  " + a.getName() + "\n";
			}
		}
		for (Map.Entry<Bucket, HashSet<Part>> be : buckets.entrySet()) {
			if (be.getValue().size() == 0) { continue; }
			s += indent + be.getKey().name().toUpperCase() + "\n";
			for (Part p : be.getValue()) {
				s += indent + " {\n";
				s += p.fullToString(indent + "  ");
				s += indent + " }\n";
			}
		}
		return s;
	}
}
