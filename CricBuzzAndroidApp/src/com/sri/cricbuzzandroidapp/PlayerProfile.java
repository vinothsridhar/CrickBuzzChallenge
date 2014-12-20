package com.sri.cricbuzzandroidapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sri.cricbuzzandroidapp.api.AsyncConnection;
import com.sri.cricbuzzandroidapp.api.ConnectionListener;
import com.sri.cricbuzzandroidapp.json.JSONKeys;
import com.sri.cricbuzzandroidapp.json.Player;
import com.sri.cricbuzzandroidapp.json.Playlist;
import com.sri.cricbuzzandroidapp.json.Profile;
import com.sri.cricbuzzandroidapp.utils.ApplicationSettings;
import com.sri.cricbuzzandroidapp.utils.L;

public class PlayerProfile extends BaseActivity implements View.OnClickListener {

	private Player player;
	private Playlist playlist;

	private TextView playerName, networkError;
	private RatingBar ratingBar;
	private Button test, odi, t20, info;
	private ProgressBar profileProgress;
	private RelativeLayout profileInfo;
	private LinearLayout detailsLayout;
	private ImageButton shareButton;

	private String[] playerInfoKeys = {"Team", "Role", "Bat Style", "Bowl Style"};
	private String[] playerInfoValues = {JSONKeys.TEAM, JSONKeys.PLAYER_ROLE, JSONKeys.BAT_STYLE, JSONKeys.BOWL_STYLE};
	private String[] batStatsKeys = {JSONKeys.BAT_INNINGS, JSONKeys.RUNS, JSONKeys.NOTOUTS, JSONKeys.BAT_AVG, JSONKeys.BAT_STRRATE, JSONKeys.HUNDREDS, JSONKeys.FIFTIES};
	private String[] bowlStatsKeys = {JSONKeys.BOWL_INNINGS, JSONKeys.WICKETS_TAKEN, JSONKeys.BOWN_AVG, JSONKeys.BOWN_STRRATE, JSONKeys.FIVE_WICKET, JSONKeys.TEN_WICKET};
	private String[] fieldStatsKeys = {JSONKeys.CATCHES, JSONKeys.STUMPINGS};

	private Profile profile;

	private static LayoutInflater inflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addContentView(R.layout.activity_playerprofile);
		initUI();
		initComponents();
	}

	private void initUI() {
		playerName = (TextView) findViewById(R.id.playerName);
		networkError = (TextView) findViewById(R.id.networkError);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar1);

		test = (Button) findViewById(R.id.test);
		odi = (Button) findViewById(R.id.odi);
		t20 = (Button) findViewById(R.id.t20);
		info = (Button) findViewById(R.id.info);
		shareButton = (ImageButton) findViewById(R.id.share);

		test.setOnClickListener(this);
		odi.setOnClickListener(this);
		t20.setOnClickListener(this);
		info.setOnClickListener(this);
		shareButton.setOnClickListener(this);

		profileProgress = (ProgressBar) findViewById(R.id.profileProgress);

		profileInfo = (RelativeLayout) findViewById(R.id.profileInfo);
		detailsLayout = (LinearLayout) findViewById(R.id.details);
	}

	private void initComponents() {
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		JSONObject selectedObject;
		try {
			selectedObject = new JSONObject(ApplicationSettings.getSelectedJson(this));
		} catch (JSONException e) {
			return;
		}

		playlist = getPlaylist();
		player = new Player(selectedObject, PlayerProfile.this);
		showBack();
		setTitleBar(player.getName());

		renderPlayer();
		loadProfile();
	}

	private void renderPlayer() {
		shareButton.setVisibility(View.VISIBLE);
		playerName.setText(player.getName());

		if (player.isFavorite(ApplicationSettings.getFavoritePlayer(PlayerProfile.this))) {
			ratingBar.setRating(1f);
		} else {
			ratingBar.setRating(0f);
		}

		ratingBar.setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if (event.getAction() == MotionEvent.ACTION_UP) {
		        	L.d("onrating changed");
					if (ratingBar.getRating() == 1f) {
						ratingBar.setRating(0f);
						ApplicationSettings.removeFavoritePlayer(PlayerProfile.this, player.getId());
					} else {
						ApplicationSettings.addFavoritePlayer(PlayerProfile.this, player.getId());
						ratingBar.setRating(1f);
					}

					updateFavoritePlayer();
		        }
		        return true;
		    }
		});
	}

	private void loadProfile() {
		profileInfo.setVisibility(View.GONE);
		profileProgress.setVisibility(View.VISIBLE);
		AsyncTask<Void, Void, Void> profileInfoConnection = new AsyncConnection(AsyncConnection.METHOD_GET, player.getProfileUrl(), null, null, new ConnectionListener() {

			@Override
			public void OnSuccess(String result) {
				L.d("profile info: " + result);
				try {
					profile = new Profile(result);
				} catch (JSONException e) {
					return;
				}
				profileProgress.setVisibility(View.GONE);
				profileInfo.setVisibility(View.VISIBLE);
				renderProfile();
			}

			@Override
			public void OnFailure(String error, int status) {
				profileProgress.setVisibility(View.GONE);
				profileInfo.setVisibility(View.GONE);
				networkError.setVisibility(View.VISIBLE);
				networkError.setText("Error: " + error);
			}

			@Override
			public void OnConnectionError(int errorCode) {
				profileProgress.setVisibility(View.GONE);
				profileInfo.setVisibility(View.GONE);
				networkError.setVisibility(View.VISIBLE);
			}
		}).execute();
	}



	private void renderProfile() {
		info.setBackgroundResource(R.drawable.button_unfocus_item);
		test.setBackgroundResource(R.drawable.button_item);
		odi.setBackgroundResource(R.drawable.button_item);
		t20.setBackgroundResource(R.drawable.button_item);

		detailsLayout.removeAllViews();

		for (int i=0; i<playerInfoKeys.length; i++) {
			View v = inflater.inflate(R.layout.statistics_item, null);
			TextView static_item = (TextView) v.findViewById(R.id.static_item);
			JSONObject object = profile.playerInfo();
			try {
				static_item.setText(playerInfoKeys[i] + " : " + object.getString(playerInfoValues[i]));
			} catch (JSONException e) {}

			detailsLayout.addView(v);
		}

		View v1 = inflater.inflate(R.layout.statistics_item, null);
		TextView static_item1 = (TextView) v1.findViewById(R.id.static_item);
		JSONObject object1 = profile.odiStats();
		try {
			static_item1.setText("Matches" + " : " + object1.getString(JSONKeys.MATCHES));
		} catch (JSONException e) {}

		detailsLayout.addView(v1);

		View v2 = inflater.inflate(R.layout.statistics_item, null);
		TextView static_item2 = (TextView) v2.findViewById(R.id.static_item);
		static_item2.setText("Profile" + " : " + Html.fromHtml(profile.playerProfile()));

		detailsLayout.addView(v2);
	}

	private void renderTestStats() {
		info.setBackgroundResource(R.drawable.button_item);
		test.setBackgroundResource(R.drawable.button_unfocus_item);
		odi.setBackgroundResource(R.drawable.button_item);
		t20.setBackgroundResource(R.drawable.button_item);
		detailsLayout.removeAllViews();
		View titleV1 = inflater.inflate(R.layout.title_item, null);
		TextView title1 = (TextView) titleV1.findViewById(R.id.title_item);
		title1.setText("Batting");
		detailsLayout.addView(titleV1);
		JSONObject object = profile.testStats();
		for (int i=0; i<batStatsKeys.length; i++) {
			View v = inflater.inflate(R.layout.statistics_half_item, null);
			TextView key = (TextView) v.findViewById(R.id.key);
			TextView value = (TextView) v.findViewById(R.id.value);

			key.setText(batStatsKeys[i]);
			try {
				value.setText(object.getString(batStatsKeys[i]));
			} catch (JSONException e) {
			}

			detailsLayout.addView(v);
		}

		View titleV2 = inflater.inflate(R.layout.title_item, null);
		TextView title2 = (TextView) titleV2.findViewById(R.id.title_item);
		title2.setText("Bowling");
		detailsLayout.addView(titleV2);

		for (int i=0; i<bowlStatsKeys.length; i++) {
			View v = inflater.inflate(R.layout.statistics_half_item, null);
			TextView key = (TextView) v.findViewById(R.id.key);
			TextView value = (TextView) v.findViewById(R.id.value);

			key.setText(bowlStatsKeys[i]);
			try {
				value.setText(object.getString(bowlStatsKeys[i]));
			} catch (JSONException e) {
			}

			detailsLayout.addView(v);
		}

		View titleV3 = inflater.inflate(R.layout.title_item, null);
		TextView title3 = (TextView) titleV3.findViewById(R.id.title_item);
		title3.setText("Fielding");
		detailsLayout.addView(titleV3);

		for (int i=0; i<fieldStatsKeys.length; i++) {
			View v = inflater.inflate(R.layout.statistics_half_item, null);
			TextView key = (TextView) v.findViewById(R.id.key);
			TextView value = (TextView) v.findViewById(R.id.value);

			key.setText(fieldStatsKeys[i]);
			try {
				value.setText(object.getString(fieldStatsKeys[i]));
			} catch (JSONException e) {
			}

			detailsLayout.addView(v);
		}
	}

	private void renderODIStats() {
		info.setBackgroundResource(R.drawable.button_item);
		test.setBackgroundResource(R.drawable.button_item);
		odi.setBackgroundResource(R.drawable.button_unfocus_item);
		t20.setBackgroundResource(R.drawable.button_item);
		detailsLayout.removeAllViews();

		View titleV1 = inflater.inflate(R.layout.title_item, null);
		TextView title1 = (TextView) titleV1.findViewById(R.id.title_item);
		title1.setText("Batting");
		detailsLayout.addView(titleV1);
		JSONObject object = profile.odiStats();
		for (int i=0; i<batStatsKeys.length; i++) {
			View v = inflater.inflate(R.layout.statistics_half_item, null);
			TextView key = (TextView) v.findViewById(R.id.key);
			TextView value = (TextView) v.findViewById(R.id.value);

			key.setText(batStatsKeys[i]);
			try {
				value.setText(object.getString(batStatsKeys[i]));
			} catch (JSONException e) {
			}

			detailsLayout.addView(v);
		}

		View titleV2 = inflater.inflate(R.layout.title_item, null);
		TextView title2 = (TextView) titleV2.findViewById(R.id.title_item);
		title2.setText("Bowling");
		detailsLayout.addView(titleV2);

		for (int i=0; i<bowlStatsKeys.length; i++) {
			View v = inflater.inflate(R.layout.statistics_half_item, null);
			TextView key = (TextView) v.findViewById(R.id.key);
			TextView value = (TextView) v.findViewById(R.id.value);

			key.setText(bowlStatsKeys[i]);
			try {
				value.setText(object.getString(bowlStatsKeys[i]));
			} catch (JSONException e) {
			}

			detailsLayout.addView(v);
		}

		View titleV3 = inflater.inflate(R.layout.title_item, null);
		TextView title3 = (TextView) titleV3.findViewById(R.id.title_item);
		title3.setText("Fielding");
		detailsLayout.addView(titleV3);

		for (int i=0; i<fieldStatsKeys.length; i++) {
			View v = inflater.inflate(R.layout.statistics_half_item, null);
			TextView key = (TextView) v.findViewById(R.id.key);
			TextView value = (TextView) v.findViewById(R.id.value);

			key.setText(fieldStatsKeys[i]);
			try {
				value.setText(object.getString(fieldStatsKeys[i]));
			} catch (JSONException e) {
			}

			detailsLayout.addView(v);
		}
	}

	private void renderT20Stats() {
		info.setBackgroundResource(R.drawable.button_item);
		test.setBackgroundResource(R.drawable.button_item);
		odi.setBackgroundResource(R.drawable.button_item);
		t20.setBackgroundResource(R.drawable.button_unfocus_item);
		detailsLayout.removeAllViews();

		View titleV1 = inflater.inflate(R.layout.title_item, null);
		TextView title1 = (TextView) titleV1.findViewById(R.id.title_item);
		title1.setText("Batting");
		detailsLayout.addView(titleV1);

		JSONObject object = profile.t20Stats();
		for (int i=0; i<batStatsKeys.length; i++) {
			View v = inflater.inflate(R.layout.statistics_half_item, null);
			TextView key = (TextView) v.findViewById(R.id.key);
			TextView value = (TextView) v.findViewById(R.id.value);

			key.setText(batStatsKeys[i]);
			try {
				value.setText(object.getString(batStatsKeys[i]));
			} catch (JSONException e) {
			}

			detailsLayout.addView(v);
		}

		View titleV2 = inflater.inflate(R.layout.title_item, null);
		TextView title2 = (TextView) titleV2.findViewById(R.id.title_item);
		title2.setText("Bowling");
		detailsLayout.addView(titleV2);

		for (int i=0; i<bowlStatsKeys.length; i++) {
			View v = inflater.inflate(R.layout.statistics_half_item, null);
			TextView key = (TextView) v.findViewById(R.id.key);
			TextView value = (TextView) v.findViewById(R.id.value);

			key.setText(bowlStatsKeys[i]);
			try {
				value.setText(object.getString(bowlStatsKeys[i]));
			} catch (JSONException e) {
			}

			detailsLayout.addView(v);
		}

		View titleV3 = inflater.inflate(R.layout.title_item, null);
		TextView title3 = (TextView) titleV3.findViewById(R.id.title_item);
		title3.setText("Fielding");
		detailsLayout.addView(titleV3);

		for (int i=0; i<fieldStatsKeys.length; i++) {
			View v = inflater.inflate(R.layout.statistics_half_item, null);
			TextView key = (TextView) v.findViewById(R.id.key);
			TextView value = (TextView) v.findViewById(R.id.value);

			key.setText(fieldStatsKeys[i]);
			try {
				value.setText(object.getString(fieldStatsKeys[i]));
			} catch (JSONException e) {
			}

			detailsLayout.addView(v);
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.test:
			renderTestStats();
			break;
		case R.id.odi:
			renderODIStats();
			break;
		case R.id.t20:
			renderT20Stats();
			break;
		case R.id.info:
			renderProfile();
			break;
		case R.id.share:
			shareProfile();
			break;
		}
	}

	private void shareProfile() {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
	    sharingIntent.setType("text/plain");
	    String shareBody = "Hey Buddy, I have shared " + player.getName() + "profile to you. You can click this link to get the profile information. " + player.getProfileUrl();
	    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, player.getName());
	    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
	    startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
}
