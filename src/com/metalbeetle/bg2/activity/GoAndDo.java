package com.metalbeetle.bg2.activity;

import com.metalbeetle.bg2.Direction;
import com.metalbeetle.bg2.Feedbacker;
import com.metalbeetle.bg2.Position;
import com.metalbeetle.bg2.World;
import com.metalbeetle.bg2.item.Part;
import static com.metalbeetle.bg2.item.Stats.*;

public class GoAndDo implements Activity {
	boolean dead = false;
	WalkTo walkTo;
	LocatedActivity activity;
	static enum Phase { NOT_STARTED, WALKING, ACTIVITY; }
	Phase phase = Phase.NOT_STARTED;

	public GoAndDo(LocatedActivity activity) {
		this.activity = activity;
	}

	public String getName() {
		return "Walk to " + activity.getPosition() + " and " + activity.getName();
	}

	public int getPriority() {
		return activity.getPriority();
	}

	public boolean update(World world, Part p, int delta, Feedbacker f) {
		switch (phase) {
			case NOT_STARTED: {
				if (dead) { return true; }
				String err = activity.impossibleReason(p);
				if (err != null && !err.equals(LocatedActivity.TOO_FAR_AWAY)) {
					f.say("Cannot " + getName() + ": " + err + ".");
					return true;
				}
				Position myPos = p.get(POSITION);
				Position tPos = activity.getPosition();
				float dist = myPos.distanceTo(tPos);
				if (dist > Const.TOUCH_DIST - 1) {
					Direction dir = myPos.directionTo(tPos);
					walkTo = new WalkTo(new Position(
							tPos.y - dir.dy(Const.TOUCH_DIST / 2),
							tPos.x - dir.dx(Const.TOUCH_DIST / 2),
							Direction.EAST));
					phase = Phase.WALKING;
				} else {
					phase = Phase.ACTIVITY;
				}
				break;
			}
			case WALKING: {
				if (dead) {
					walkTo.cancel();
					walkTo.update(world, p, delta, f);
					return true;
				}
				if (walkTo.update(world, p, delta, f)) {
					phase = Phase.ACTIVITY;
				}
				break;
			}
			case ACTIVITY: {
				if (dead) {
					activity.cancel();
					activity.update(world, p, delta, f);
					return true;
				}
				if (activity.update(world, p, delta, f)) {
					return true;
				}
				break;
			}
		}

		return false;
	}

	public void cancel() {
		dead = true;
	}
}
