package com.metalbeetle.bg2;

public final class Position {
	public static final Position ORIGIN = new Position(0, 0, Direction.EAST);

	public final float y;
	public final float x;
	public final Direction direction;

	public Position(float y, float x, Direction direction) {
		this.y = y;
		this.x = x;
		this.direction = direction;
	}

	public Position within(Position parent) {
		return new Position(
				parent.y + parent.direction.dx(y) - parent.direction.dy(x),
				parent.x + parent.direction.dx(x) - parent.direction.dy(y),
				direction.plus(parent.direction)
		);
	}

	public Position plus(Position delta) {
		return new Position(
				y + delta.y,
				x + delta.x,
				direction.plus(delta.direction)
		);
	}

	public Position minus(Position delta) {
		return new Position(
				y - delta.y,
				x - delta.x,
				direction.minus(delta.direction)
		);
	}

	public Direction directionTo(Position dest) {
		return Direction.fromOffset(dest.y - y, dest.x - x);
	}

	public float distanceToSq(Position p2) {
		return ((y - p2.y) * (y - p2.y) + (x - p2.x) * (x - p2.x));
	}

	public float distanceTo(Position p2) {
		return (float) Math.sqrt(distanceToSq(p2));
	}

	@Override
	public String toString() {
		return y + " / " + x + ", " + direction;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Position)) { return false; }
		Position p2 = (Position) o;
		return p2.y == y && p2.x == x && p2.direction.equals(direction);
	}

	@Override
	public int hashCode() {
		return  19 +
				Float.floatToIntBits(y) * 31 +
				Float.floatToIntBits(x) * 49 +
				direction.hashCode();
	}
}
