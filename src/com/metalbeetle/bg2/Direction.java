package com.metalbeetle.bg2;

public final class Direction {
	private static final float PI = (float) Math.PI;

	public static final Direction NORTH = new Direction(-1, 0);
	public static final Direction SOUTH = new Direction(1, 0);
	public static final Direction EAST  = new Direction(0, 1);
	public static final Direction WEST  = new Direction(0, -1);

	private final float dy;
	private final float dx;
	private final float radians;

	private Direction(float dy, float dx) {
		this.dy = dy / (Math.abs(dx) + Math.abs(dy));
		this.dx = dx / (Math.abs(dx) + Math.abs(dy));
		radians = (float) Math.atan2(dy, dx);
	}

	private Direction(float angle) {
		while (angle < 0     ) { angle += 2 * PI; }
		while (angle > 2 * PI) { angle -= 2 * PI; }
		this.radians = angle;
		this.dy = (float) -Math.sin(angle);
		this.dx = (float) Math.cos(angle);
	}

	public float dy() { return dy; }
	public float dx() { return dx; }
	public float dy(float forDistance) { return dy * forDistance; }
	public float dx(float forDistance) { return dx * forDistance; }
	public float radians() { return radians; }
	public float degrees() { return radians * 180 / PI; }

	public Direction plus(Direction d) { return new Direction(radians + d.radians); }
	public Direction plusRadians(float radians) { return new Direction(this.radians + radians); }
	public Direction plusDegrees(float degrees) { return new Direction(this.radians + degrees * PI / 180); }

	public Direction minus(Direction d) { return new Direction(radians - d.radians); }
	public Direction minusRadians(float radians) { return new Direction(this.radians - radians); }
	public Direction minusDegrees(float degrees) { return new Direction(this.radians - degrees * PI / 180); }

	public static Direction fromOffset(float dy, float dx) {
		return new Direction(dy, dx);
	}

	public static Direction fromRadians(float angle) {
		return new Direction(angle);
	}

	public static Direction fromDegrees(float degrees) {
		return new Direction(degrees * PI / 180);
	}

	public float getRotationDirectionInDegrees(Direction to, float delta) {
		return getRotationDirectionInRadians(to, delta * PI / 180) * 180 / PI;
	}

	public float getRotationDirectionInRadians(Direction to, float delta) {
		double fromToTo = to.radians - radians;
		while (fromToTo < 0) {
			fromToTo += Math.PI * 2;
		}
		fromToTo = fromToTo % (Math.PI * 2);

		if (fromToTo < Math.PI) {
			if (fromToTo < delta) {
				return (float) fromToTo;
			} else {
				return delta;
			}
		} else {
			if (Math.PI * 2 - fromToTo < delta) {
				return (float) (fromToTo - Math.PI * 2);
			} else {
				return -delta;
			}
		}
	}

	@Override
	public String toString() {
		return degrees() + "°";
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Direction)) { return false; }
		return ((Direction) o).radians == radians;
	}

	@Override
	public int hashCode() { return 9 + 7 * Float.floatToIntBits(this.radians); }
}
