package com.metalbeetle.bg2.activity;

import com.metalbeetle.bg2.Position;
import com.metalbeetle.bg2.item.Part;

public interface LocatedActivity extends Activity {
	public static final String TOO_FAR_AWAY = "too far away";
	public String impossibleReason(Part actor);
	public Position getPosition();
}
