package com.sri.cricbuzzandroidapp.json;

import org.json.JSONException;
import org.json.JSONObject;

public class Profile {

	private JSONObject profileInfo, odiStats, testStats, t20Stats;
	private String playerProfile;

	public Profile(String json) throws JSONException {
		this(new JSONObject(json));
	}

	public Profile(JSONObject object) {
		try {
			this.odiStats = object.getJSONObject(JSONKeys.ODI_STATS);
			this.testStats = object.getJSONObject(JSONKeys.TEST_STATS);
			this.t20Stats = object.getJSONObject(JSONKeys.T20_STATS);
			this.profileInfo = object.getJSONObject(JSONKeys.PLAYER_INFO);
			this.playerProfile = object.getString(JSONKeys.PLAYER_PROFILE);
		} catch (JSONException e) {
		}

	}

	public JSONObject playerInfo() {
		return profileInfo;
	}

	public JSONObject testStats() {
		return testStats;
	}

	public JSONObject odiStats() {
		return odiStats;
	}

	public JSONObject t20Stats() {
		return t20Stats;
	}

	public String playerProfile() {
		return playerProfile;
	}

}