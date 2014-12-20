package com.sri.cricbuzzandroidapp.json;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Player {

	@Override
	public String toString() {
		return getName();
	}

	private JSONObject playerObject;

	public Player(JSONObject jsonObject, Context context) {
		this.playerObject = jsonObject;
	}

	public String getId() {
		try {
			return playerObject.getString("id");
		} catch (JSONException e) {
			return null;
		}
	}

	public String getName() {
		try {
			return playerObject.getString("name");
		} catch (JSONException e) {
			return null;
		}
	}

	public String getProfileUrl() {
		try {
			return playerObject.getString("url");
		} catch (JSONException e) {
			return null;
		}
	}

	public String getJson() {
		return playerObject.toString();
	}

	public boolean isFavorite(Set<String> favoritePlayers) {
		if (favoritePlayers == null) {
			return false;
		}

		for (String str : favoritePlayers) {
			if (getId().equals(str)) {
				return true;
			}
		}

		return false;
	}

}