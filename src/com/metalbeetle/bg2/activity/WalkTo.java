package com.metalbeetle.bg2.activity;

import com.metalbeetle.bg2.Feedbacker;
import com.metalbeetle.bg2.Position;
import com.metalbeetle.bg2.World;
import com.metalbeetle.bg2.item.Bucket;
import com.metalbeetle.bg2.item.Part;
import static com.metalbeetle.bg2.item.Stats.*;

public class WalkTo implements Activity {
	static final float PRECISION = 1f;

	final Position dest;
	Part legs;
	boolean dead = false;
	Walk walk = null;

	public WalkTo(Position dest, Part legs) {
		this.dest = dest;
		this.legs = legs;
	}

	public WalkTo(Position dest) {
		this.dest = dest;
		legs = null;
	}

	public String getName() {
		return "Walk to " + dest;
	}

	public int getPriority() { return 10; }

	public boolean update(World world, Part p, int delta, Feedbacker f) {
		if (dead) {
			f.say(getName() + " cancelled.");
			if (walk != null) {
				walk.cancel();
				walk.update(world, p, delta, f);
			}
			return true;
		}
		if (legs == null) {
			for (Part pp : p.getBucket(Bucket.ENABLED)) {
				if (pp.get(SUPPORT_LIMIT) >= p.get(WEIGHT) && pp.get(SPEED) > 0) {
					legs = pp;
					break;
				}
			}
			if (legs == null) {
				f.say("Cannot " + getName() + ": no suitable legs found");
				return true;
			}
		}
		Position pos = p.get(POSITION);
		float dist = dest.distanceTo(pos);
		if (dist < PRECISION) {
			f.say("Arrived at " + dest + ".");
			walk.cancel();
			walk.update(world, p, delta, f);
			dead = true;
		} else {
			if (walk == null) {
				walk = new Walk(pos.directionTo(dest), legs);
			} else {
				if (dist < p.get(SPEED) * delta) {
					delta = (int) (dist / p.get(SPEED));
				}
				dead = walk.update(world, p, delta, f);
			}
		}
		return dead;
	}
	
	public void cancel() { this.dead = true; }
}
