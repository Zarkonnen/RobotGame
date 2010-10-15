package com.metalbeetle.bg2.activity;

import com.metalbeetle.bg2.Feedbacker;
import com.metalbeetle.bg2.Position;
import com.metalbeetle.bg2.World;
import com.metalbeetle.bg2.item.Bucket;
import com.metalbeetle.bg2.item.Part;
import java.util.ArrayList;
import static com.metalbeetle.bg2.item.Stats.*;

public class PickUp implements LocatedActivity {
	final Part target;
	boolean dead = false;
	boolean done = false;
	ArrayList<Part> arms = new ArrayList<Part>();

	public PickUp(Part target) {
		this.target = target;
	}

	public String getName() {
		return "Pick up " + target.get(NAME);
	}

	public int getPriority() {
		return 9;
	}

	public Position getPosition() {
		return target.get(POSITION);
	}

	public String impossibleReason(Part actor) {
		int targetWeight = target.get(WEIGHT);
		int spareCargo = actor.get(CARGO_LIMIT) - actor.get(CARRIED_CARGO);
		int liftCap = actor.get(LIFT_LIMIT);
		if (targetWeight > liftCap) {
			return "item too heavy";
		}
		if (targetWeight > spareCargo) {
			return "not enough spare cargo capacity";
		}
		float distanceSq = actor.get(POSITION).distanceToSq(target.get(POSITION));
		if (distanceSq > Const.TOUCH_DIST * Const.TOUCH_DIST) {
			return TOO_FAR_AWAY;
		}
		return null;
	}

	public boolean update(World world, Part p, int delta, Feedbacker f) {
		if (done || dead) {
			for (Part a : arms) {
				p.move(a, Bucket.ENABLED);
			}
			return true;
		}
		String err = impossibleReason(p);
		if (err != null) {
			f.say("Cannot " + getName() + ": " + err + ".");
			return true;
		}
		int weight = target.get(WEIGHT);
		// Gather arms:
		int liftCap = 0;
		int energyUse = 0;
		for (Part pp : p.getBucket(Bucket.ENABLED)) {
			if (liftCap >= weight) { break; }
			int ll = pp.get(LIFT_LIMIT);
			if (ll > 0) {
				liftCap += ll;
				energyUse += pp.get(ENERGY_USE);
				arms.add(pp);
			}
		}
		if (energyUse > p.get(ENERGY_RESERVE)) {
			f.say("Cannot " + getName() + ": not enough energy.");
			return true;
		}
		for (Part a : arms) {
			p.move(a, Bucket.ACTIVE);
		}
		p.change(ENERGY_RESERVE, -energyUse);
		p.add(target, Bucket.CARRIED);
		target.set(POSITION, null);
		world.removePart(target);
		done = true;
		return false;
	}

	public void cancel() {
		dead = true;
	}
}
