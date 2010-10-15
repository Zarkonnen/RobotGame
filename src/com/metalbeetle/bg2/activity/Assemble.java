package com.metalbeetle.bg2.activity;

import com.metalbeetle.bg2.Feedbacker;
import com.metalbeetle.bg2.Position;
import com.metalbeetle.bg2.World;
import com.metalbeetle.bg2.item.Bucket;
import com.metalbeetle.bg2.item.Part;
import java.util.ArrayList;
import java.util.Random;
import static com.metalbeetle.bg2.item.Stats.*;

public class Assemble implements LocatedActivity {
	final Part carriedPart;
	final Part targetPart;

	int progress = 0;
	boolean started = false;

	ArrayList<Part> assemblers = new ArrayList<Part>();

	public Assemble(Part carriedPart, Part targetPart) {
		this.carriedPart = carriedPart;
		this.targetPart = targetPart;
	}

	boolean dead = false;

	public String impossibleReason(Part actor) {
		int techLvl = actor.get(BUILD_LEVEL);
		if (techLvl < 1) {
			return "Cannot " + getName() + ": no assembly capability";
		}
		int itemWeight = carriedPart.get(WEIGHT);
		int liftCap = actor.get(LIFT_LIMIT);
		if (itemWeight > liftCap) {
			return carriedPart.get(NAME) + " too heavy";
		}
		float distanceSq = actor.get(POSITION).distanceToSq(targetPart.get(POSITION));
		if (distanceSq > Const.TOUCH_DIST * Const.TOUCH_DIST) {
			System.out.println(distanceSq);
			System.out.println(Const.TOUCH_DIST * Const.TOUCH_DIST);
			return TOO_FAR_AWAY;
		}
		return null;
	}

	public Position getPosition() {
		return targetPart.get(POSITION);
	}

	public String getName() {
		return "Attach " + carriedPart.get(NAME) + " to " + targetPart.get(NAME);
	}

	public int getPriority() {
		return 12;
	}

	public boolean update(World world, Part p, int delta, Feedbacker f) {
		for (Part ass : assemblers) {
			p.move(ass, Bucket.ENABLED);
		}
		if (dead) {
			return true;
		}
		String err = impossibleReason(p);
		if (err != null) {
			f.say("Cannot " + getName() + ": " + err + ".");
			return true;
		}

		int todo = Math.min(carriedPart.get(WEIGHT), targetPart.get(WEIGHT)) * Const.BUILD_PER_WEIGHT;
		int left = todo - progress;

		int energyUse = 0;
		int done = 0;
		for (Part pp : p.getBucket(Bucket.ENABLED)) {
			if (pp.get(BUILD_LEVEL) > 0) {
				int meDone = Math.min(left - done, pp.get(BUILD_SPEED) * delta);
				done += meDone;
				int energyUsed = meDone / pp.get(BUILD_SPEED);
				energyUse += energyUsed;
				assemblers.add(pp);
				p.move(pp, Bucket.ACTIVE);
				if (done == left) {
					break;
				}
			}
		}
		if (energyUse > p.get(ENERGY_RESERVE)) {
			f.say("Cannot " + getName() + ": Insufficient energy reserves.");
			return true;
		}
		if (assemblers.size() == 0) {
			f.say("Cannot " + getName() + ": No assembler equipment found.");
			return true;
		}
		p.change(ENERGY_RESERVE, -energyUse);
		progress += done;
		if (progress == todo) {
			if (carriedPart.get(ASSEMBLED)) {
				if (targetPart.get(ASSEMBLED)) {
					// Merge!
					for (Bucket b : Bucket.values()) {
						for (Part pp : carriedPart.getBucket(b)) {
							targetPart.add(pp, b);
						}
					}
					p.remove(carriedPart);
				} else {
					// Add targetPart to carriedPart
					carriedPart.add(targetPart, Bucket.ENABLED);
					carriedPart.set(POSITION, targetPart.get(POSITION));
					targetPart.set(POSITION, null);
					p.remove(carriedPart);
				}
			} else {
				if (targetPart.get(ASSEMBLED)) {
					// Add carriedPart to targetPart
					targetPart.add(carriedPart, Bucket.ENABLED);
					p.remove(carriedPart);
				} else {
					// Create new assembly.
					Part newA = new Part();
					newA.set(NAME, "Robot #" + new Random().nextInt(100));
					newA.set(ASSEMBLED, true);
					newA.set(POSITION, targetPart.get(POSITION));
					targetPart.set(POSITION, null);
					newA.add(carriedPart, Bucket.ENABLED);
					newA.add(targetPart, Bucket.ENABLED);
					p.remove(carriedPart);
					world.addPart(newA);
				}
			}
			f.say(carriedPart.get(NAME) + " attached to " + targetPart.get(NAME) + ".");
			return true;
		}
		return false; // Still welding...
	}

	public void cancel() {
		dead = true;
	}
}
