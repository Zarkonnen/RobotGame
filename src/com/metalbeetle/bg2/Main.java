/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.metalbeetle.bg2;

import com.metalbeetle.bg2.activity.Assemble;
import com.metalbeetle.bg2.activity.GeneratePower;
import com.metalbeetle.bg2.activity.GoAndDo;
import com.metalbeetle.bg2.activity.PickUp;
import com.metalbeetle.bg2.activity.WalkTo;
import com.metalbeetle.bg2.item.Bucket;
import com.metalbeetle.bg2.item.Part;
import static com.metalbeetle.bg2.item.Stats.*;

public class Main {
    public static void main(String[] args) {
		Part bot = new Part();
		bot.set(NAME, "Robot");
		bot.set(POSITION, new Position(10f, 10f, Direction.EAST));

		Part eightLegs = new Part();
		eightLegs.set(NAME, "Eight Legs");
		eightLegs.set(SPEED, 0.003f);
		eightLegs.set(ENERGY_USE, 1);

		Part tracks = new Part();
		tracks.set(NAME, "Tracks");
		tracks.set(SPEED, 0.005f);
		tracks.set(ENERGY_USE, 1);
		tracks.set(TURN_TO_WALK, true);
		tracks.set(TURN_SPEED, 0.04f);
		tracks.set(WEIGHT, 10);
		tracks.set(SUPPORT_LIMIT, 1000);
		bot.add(tracks, Bucket.ENABLED);

		Part battery1 = new Part();
		battery1.set(NAME, "Battery");
		battery1.set(ENERGY_CAPACITY, 2000);
		battery1.set(ENERGY_RESERVE, 1000);
		battery1.set(WEIGHT, 3);
		bot.add(battery1, Bucket.ENABLED);

		Part battery2 = new Part();
		battery2.set(NAME, "Battery");
		battery2.set(ENERGY_CAPACITY, 2000);
		battery2.set(ENERGY_RESERVE, 1000);
		battery2.set(WEIGHT, 3);
		bot.add(battery2, Bucket.ENABLED);

		Part generator = new Part();
		generator.set(NAME, "Generator");
		generator.set(ENERGY_OUTPUT, 3);
		generator.set(ENERGY_CAPACITY, 3);
		generator.set(WEIGHT, 20);
		bot.add(generator, Bucket.ENABLED);

		Part cargoBay = new Part();
		cargoBay.set(NAME, "100 kg Cargo Bay");
		cargoBay.set(CARGO_LIMIT, 100);
		cargoBay.set(WEIGHT, 10);
		bot.add(cargoBay, Bucket.ENABLED);

		Part arm = new Part();
		arm.set(NAME, "Robotic Welding Arm");
		arm.set(LIFT_LIMIT, 20);
		arm.set(CARGO_LIMIT, 20);
		arm.set(WEIGHT, 15);
		arm.set(BUILD_LEVEL, 1);
		arm.set(BUILD_SPEED, 9000);
		bot.add(arm, Bucket.ENABLED);

		Part doodad = new Part();
		doodad.set(NAME, "Doodad");
		doodad.set(POSITION, new Position(22f, 10f, Direction.EAST));
		doodad.set(WEIGHT, 5);

		Part thingum = new Part();
		thingum.set(NAME, "Thingum");
		thingum.set(POSITION, new Position(9f, -4f, Direction.EAST));
		thingum.set(WEIGHT, 15);

		bot.addActivity(new GeneratePower(generator));
		//bot.addActivity(new WalkTo(new Position(20f, 10f, Direction.EAST), tracks));
		GoAndDo pickup = new GoAndDo(new PickUp(doodad));
		bot.addActivity(pickup);

		while (bot.getActivities().contains(pickup)) {
			System.out.println(bot.fullToString());
			bot.update(new World() {
				public void addPart(Part p) {
					System.out.println("* " + p.fullToString());
				}

				public void removePart(Part p) {
					System.out.println("t " + p.fullToString());
				}
			}, 1000, new Feedbacker() {
				public void say(String s) {
					System.out.println("::: " + s);
				}
			});
		}

		GoAndDo assemble = new GoAndDo(new Assemble(doodad, thingum));
		bot.addActivity(assemble);

		while (bot.getActivities().contains(assemble)) {
			System.out.println(bot.fullToString());
			bot.update(new World() {
				public void addPart(Part p) {
					System.out.println("* " + p.fullToString());
				}

				public void removePart(Part p) {
					System.out.println("t " + p.fullToString());
				}
			}, 1000, new Feedbacker() {
				public void say(String s) {
					System.out.println("::: " + s);
				}
			});
		}
    }
}
