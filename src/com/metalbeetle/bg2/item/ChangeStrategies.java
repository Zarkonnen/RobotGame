package com.metalbeetle.bg2.item;

import com.metalbeetle.bg2.Direction;
import com.metalbeetle.bg2.Position;
import static com.metalbeetle.bg2.item.Stats.*;

final class ChangeStrategies {
	private ChangeStrategies() {}

	static final ChangeStrategy<Integer> ENERGY = new EnergyStrategy();
	static final ChangeStrategy<Integer> NONE_I = new NoChangeStrategy<Integer>();
	static final ChangeStrategy<String>  NONE_S = new NoChangeStrategy<String>();
	static final ChangeStrategy<Float>   NONE_F = new NoChangeStrategy<Float>();
	static final ChangeStrategy<Boolean> NONE_B = new NoChangeStrategy<Boolean>();
	static final ChangeStrategy<Float>   ADD_F  = new AddFloatStrategy();
	static final ChangeStrategy<Boolean> SET_B  = new SetStrategy<Boolean>();
	static final ChangeStrategy<Position> POS   = new PositionStrategy();

	static class EnergyStrategy implements ChangeStrategy<Integer> {
		public void change(Stat<Integer> k, Integer delta, Part p) {
			assert k == ENERGY_RESERVE;
			change2(delta, p);
		}

		int change2(Integer delta, Part p) {
			if (delta < 0) {
				if (p.kv.containsKey(ENERGY_RESERVE)) {
					int energyHere = (Integer) p.kv.get(ENERGY_RESERVE);
					int energyRemoved = Math.min(energyHere, -delta);
					p.kv.put(ENERGY_RESERVE, energyHere - energyRemoved);
					delta += energyRemoved; // Makes delta *less negative*.
				}
			} else {
				if (p.kv.containsKey(ENERGY_CAPACITY)) {
					int energyHere =
							p.kv.containsKey(ENERGY_RESERVE)
							? (Integer) p.kv.get(ENERGY_RESERVE)
							: 0;
					int capacityHere = (Integer) p.kv.get(ENERGY_CAPACITY);
					int energyAdded = Math.min(capacityHere - energyHere, delta);
					p.kv.put(ENERGY_RESERVE, energyHere + energyAdded);
					delta -= energyAdded;
				}
			}

			for (Part sp : p.buckets.get(Bucket.ENABLED)) {
				if (delta == 0) { break; }
				delta = change2(delta, sp);
			}
			return delta;
		}
	}

	static class PositionStrategy implements ChangeStrategy<Position> {
		public void change(Stat<Position> k, Position delta, Part p) {
			Position val = p.kv.containsKey(k) ? (Position) p.kv.get(k) : k.defaultValue();
			p.kv.put(k, val.plus(delta));
		}
	}

	static class NoChangeStrategy<T> implements ChangeStrategy<T> {
		public void change(Stat<T> k, T delta, Part p) {
			// Do nuffinghk.
		}
	}

	static class SetStrategy<T> implements ChangeStrategy<T> {
		public void change(Stat<T> k, T delta, Part p) {
			p.set(k, delta);
		}
	}

	static class AddFloatStrategy implements ChangeStrategy<Float> {
		public void change(Stat<Float> k, Float delta, Part p) {
			float val = p.kv.containsKey(k) ? (Float) p.kv.get(k) : k.defaultValue();
			p.kv.put(k, val + delta);
		}
	}

	static class DirectionStrategy implements ChangeStrategy<Direction> {
		public void change(Stat<Direction> k, Direction delta, Part p) {
			Direction val = p.kv.containsKey(k) ? (Direction) p.kv.get(k) : k.defaultValue();
			p.kv.put(k, val.plus(delta));
		}
	}
}
