package com.metalbeetle.bg2.item;

import java.util.List;
import static com.metalbeetle.bg2.Collections.*;

final class CompositStrategies {
	static final CompositStrategy ALL       = new FromBuckets(l(Bucket.ACTIVE, Bucket.CARRIED, Bucket.ENABLED, Bucket.DISABLED));
	static final CompositStrategy INSTALLED = new FromBuckets(l(Bucket.ACTIVE, Bucket.ENABLED, Bucket.DISABLED));
	static final CompositStrategy CARRIED   = new FromBuckets(l(Bucket.CARRIED));
	static final CompositStrategy ACTIVE   = new FromBuckets(l(Bucket.ACTIVE));
	static final CompositStrategy ENABLED   = new FromBuckets(l(Bucket.ACTIVE, Bucket.ENABLED));
	static final CompositStrategy DISABLED  = new FromBuckets(l(Bucket.DISABLED));
	static final CompositStrategy ANCESTORS = new FromAncestors();
	static final CompositStrategy NONE      = new None();
	static final CompositStrategy SIZE_CS   = new Size();
	static final CompositStrategy CARRIED_C = new CarriedCargo();

	private CompositStrategies() {}

	static final class FromBuckets implements CompositStrategy {
		final List<Bucket> buckets;
		FromBuckets(List<Bucket> buckets) { this.buckets = buckets; }

		public <T> T composit(Stat<T> k, Part assembly) {
			T value = (T) assembly.kv.get(k);
			for (Bucket b : buckets) {
				for (Part it : assembly.buckets.get(b)) {
					value = value == null ? it.get(k) : k.composit(value, it.get(k));
				}
			}
			return value == null ? k.defaultValue() : value;
		}
	}

	static final class FromAncestors implements CompositStrategy {
		public <T> T composit(Stat<T> k, Part assembly) {
			T value = (T) assembly.kv.get(k);
			Part anc = assembly;
			while ((anc = anc.getParent()) != null) {
				value = value == null ? anc.get(k) : k.composit(value, anc.get(k));
			}
			return value == null ? k.defaultValue() : value;
		}
	}

	static final class None implements CompositStrategy {
		public <T> T composit(Stat<T> k, Part assembly) {
			return (T) (assembly.kv.containsKey(k) ? assembly.kv.get(k) : k.defaultValue());
		}
	}

	static final class Size implements CompositStrategy {
		public <T> T composit(Stat<T> k, Part p) {
			return (T) Float.valueOf((float) Math.pow(p.get(Stats.WEIGHT), 1.0 / 3.0));
		}
	}

	static final class CarriedCargo implements CompositStrategy {
		public <T> T composit(Stat<T> k, Part p) {
			return (T) CARRIED.composit(Stats.WEIGHT, p);
		}
	}
}
