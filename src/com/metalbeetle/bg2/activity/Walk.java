package com.metalbeetle.bg2.activity;

import com.metalbeetle.bg2.Direction;
import com.metalbeetle.bg2.Feedbacker;
import com.metalbeetle.bg2.Position;
import com.metalbeetle.bg2.World;
import com.metalbeetle.bg2.item.Bucket;
import com.metalbeetle.bg2.item.Part;
import static com.metalbeetle.bg2.item.Stats.*;

public class Walk implements Activity {
	static final float HEADING_PRECISION = 1f;
	
	boolean dead = false;
	final Direction walkDirection;
	final Part legs;

	public Walk(Direction direction, Part legs) {
		this.walkDirection = direction;
		this.legs = legs;
	}

	public String getName() {
		return "Walking in direction " + walkDirection;
	}

	public boolean update(World world, Part p, int delta, Feedbacker f) {
		if (dead) {
			p.move(legs, Bucket.ENABLED);
			f.say(getName() + " cancelled.");
			return true;
		}

		int energyUse = legs.get(ENERGY_USE) * delta;
		if (energyUse > 0 && energyUse > p.get(ENERGY_RESERVE)) {
			p.move(legs, Bucket.ENABLED);
			f.say("Not enough energy to continue walking in direction " + walkDirection + ".");
			return true;
		}

		if (legs.get(SUPPORT_LIMIT) < p.get(WEIGHT)) {
			f.say(p.get(WEIGHT) + " weight");
			f.say(legs.get(SUPPORT_LIMIT) + " support");
			p.move(legs, Bucket.ENABLED);
			f.say("Walking in direction " + walkDirection + " aborted: Cannot carry weight.");
			return true;
		}

		p.move(legs, Bucket.ACTIVE);
		p.change(ENERGY_RESERVE, -energyUse);
		Position currentPosition = p.get(POSITION);
		if (legs.get(TURN_TO_WALK)) {
			Direction currentDirection = currentPosition.direction;
			if (Math.abs(currentDirection.getRotationDirectionInDegrees(walkDirection, 360)) >
					HEADING_PRECISION)
			{
				float turnSpeed = legs.get(TURN_SPEED) * delta;
				float dHeading =
						currentDirection.getRotationDirectionInDegrees(walkDirection, turnSpeed);
				delta -= delta * (dHeading / turnSpeed);
				p.change(POSITION, new Position(0f, 0f, Direction.fromDegrees(dHeading)));
			}
		}

		p.change(POSITION, new Position(
				walkDirection.dy(p.get(SPEED) * delta),
				walkDirection.dx(p.get(SPEED) * delta),
				Direction.EAST));
		
		return false;
	}

	public void cancel() { dead = true; }

	public int getPriority() { return 10; }
}
