package com.metalbeetle.bg2.item;

import com.metalbeetle.bg2.Position;
import static com.metalbeetle.bg2.item.CompositStrategies.*;
import static com.metalbeetle.bg2.item.ChangeStrategies.*;

public final class Stats {
	public static final float NOWHERE = Float.NaN;

	// Generit part stats
	public static final StringStat NAME  = new StringStat("Name", NONE, NONE_S);
	public static final PosStat    POSITION = new PosStat("Position");
	public static final AndBoolStat ASSEMBLED = new AndBoolStat("Assembled", NONE, SET_B);

	// Bot part stats
	public static final IntStat WEIGHT           = new IntStat    ("Weight"         , ALL    , NONE_I);

	// Energy
	public static final IntStat ENERGY_OUTPUT    = new IntStat    ("Energy Output"  , ACTIVE, NONE_I);
	public static final IntStat ENERGY_USE       = new IntStat    ("Energy Use"     , ACTIVE, NONE_I);
	public static final IntStat ENERGY_RESERVE   = new IntStat    ("Energy Reserve" , ENABLED, ENERGY);
	public static final IntStat ENERGY_CAPACITY  = new IntStat    ("Energy Capacity", ENABLED, NONE_I);

	// Movement
	public static final FloatStat SPEED          = new FloatStat  ("Speed"          , ENABLED, NONE_F);
	public static final FloatStat TURN_SPEED     = new FloatStat  ("Turn Speed"     , ENABLED, NONE_F);
	public static final AndBoolStat TURN_TO_WALK = new AndBoolStat("Turn To Walk"   , ENABLED, NONE_B);
	public static final IntStat   SUPPORT_LIMIT  = new IntStat    ("Support Limit"  , ENABLED, NONE_I);

	// Picking up, arms and cargo
	public static final IntStat    LIFT_LIMIT     = new IntStat    ("Lift Limit"      , ENABLED, NONE_I);
	public static final IntStat    CARGO_LIMIT    = new IntStat    ("Cargo Capacity"  , ENABLED, NONE_I);
	public static final MaxIntStat BUILD_LEVEL    = new MaxIntStat ("Build Tech Level", ENABLED, NONE_I);
	public static final IntStat  BUILD_SPEED      = new IntStat    ("Build Speed"     , ENABLED, NONE_I);

	// Derived stats
	public static final FloatStat SIZE           = new FloatStat("Size"             , SIZE_CS, NONE_F);
	public static final IntStat   CARRIED_CARGO  = new IntStat  ("Carried Cargo"    , CARRIED_C, NONE_I);
	
	private Stats() {}

	static abstract class AbstractStat<T> implements Stat<T> {
		final String name;
		final CompositStrategy compositStrategy;
		final ChangeStrategy<T> changeStrategy;

		public AbstractStat(String name, CompositStrategy compositStrategy,
				ChangeStrategy<T> changeStrategy)
		{
			this.name = name;
			this.compositStrategy = compositStrategy;
			this.changeStrategy = changeStrategy;
		}

		public String getName() { return name; }
		public CompositStrategy compositFrom() { return compositStrategy; }
		public ChangeStrategy<T> changer() { return changeStrategy; }
	}

	public static final class IntStat extends AbstractStat<Integer> {
		public IntStat(String name, CompositStrategy compositStrategy,
				ChangeStrategy<Integer> changeStrategy)
		{
			super(name, compositStrategy, changeStrategy);
		}
		
		public Integer composit(Integer a, Integer b) { return a + b; }
		public Integer defaultValue() { return 0; }
	}

	public static final class MaxIntStat extends AbstractStat<Integer> {
		public MaxIntStat(String name, CompositStrategy compositStrategy,
				ChangeStrategy<Integer> changeStrategy)
		{
			super(name, compositStrategy, changeStrategy);
		}

		public Integer composit(Integer a, Integer b) { return Math.max(a, b); }
		public Integer defaultValue() { return 0; }
	}

	public static final class FloatStat extends AbstractStat<Float> {
		public FloatStat(String name, CompositStrategy compositStrategy,
				ChangeStrategy<Float> changeStrategy)
		{
			super(name, compositStrategy, changeStrategy);
		}

		public Float composit(Float a, Float b) { return a + b; }
		public Float defaultValue() { return 0f; }
	}

	public static final class StringStat extends AbstractStat<String> {
		public StringStat(String name, CompositStrategy compositStrategy,
				ChangeStrategy<String> changeStrategy)
		{
			super(name, compositStrategy, changeStrategy);
		}

		public String composit(String a, String b) {
			return a == null ? b : a + ", " + b;
		}
		public String defaultValue() { return null; }
	}

	public static final class AndBoolStat extends AbstractStat<Boolean> {
		public AndBoolStat(String name, CompositStrategy compositStrategy,
				ChangeStrategy<Boolean> changeStrategy)
		{
			super(name, compositStrategy, changeStrategy);
		}

		public Boolean composit(Boolean a, Boolean b) { return a && b; }
		public Boolean defaultValue() { return false; }
	}

	public static final class PosStat extends AbstractStat<Position> {
		public PosStat(String name) {
			super(name, ANCESTORS, POS);
		}

		/** a is taken as an offset from b. */
		public Position composit(Position a, Position b) {
			return a.within(b);
		}

		public Position defaultValue() { return Position.ORIGIN; }
	}
}
