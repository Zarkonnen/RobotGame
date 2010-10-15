package com.metalbeetle.bg2.activity;

import com.metalbeetle.bg2.Feedbacker;
import com.metalbeetle.bg2.World;
import com.metalbeetle.bg2.item.Bucket;
import com.metalbeetle.bg2.item.Part;
import static com.metalbeetle.bg2.item.Stats.*;

public class GeneratePower implements Activity {
	final Part gen;
	boolean dead = false;

	public GeneratePower(Part gen) { this.gen = gen; }

	public String getName() { return "Generate power"; }
	public int getPriority() { return 2; }

	public boolean update(World world, Part p, int delta, Feedbacker f) {
		if (dead) {
			p.move(gen, Bucket.ENABLED);
			f.say("Power generation cancelled.");
			return true;
		}

		p.move(gen, Bucket.ACTIVE);
		p.change(ENERGY_RESERVE, gen.get(ENERGY_OUTPUT) * delta);
		return false;
	}

	public void cancel() { dead = true; }
}
