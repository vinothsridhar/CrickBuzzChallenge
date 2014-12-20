package com.sri.cricbuzzandroidapp;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sri.cricbuzzandroidapp.json.Player;
import com.sri.cricbuzzandroidapp.json.Playlist;
import com.sri.cricbuzzandroidapp.utils.ApplicationSettings;
import com.sri.cricbuzzandroidapp.utils.L;

public class PlaylistActivity extends BaseActivity {

	private Playlist playlist;
	private LinearLayout playlistLayout;
	private AutoCompleteTextView playerSearch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addContentView(R.layout.activity_playlist);
		initUI();
		initComponents();
	}

	private void initUI() {
		playlistLayout = (LinearLayout) findViewById(R.id.playlistLayout);
		playerSearch = (AutoCompleteTextView) findViewById(R.id.playerSearch);
	}

	private void initComponents() {
		showHome();
		setTitleBar("FavPlayer");
		playlist = getPlaylist();
		renderPlaylist();
	}

	private void renderPlaylist() {
		final ArrayList<Player> players = playlist.getPlayers();
		if (players == null) {
			return;
		}

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i=0; i<players.size(); i++) {
			final Player player = players.get(i);
			View v = inflater.inflate(R.layout.playlist_item, null);
			TextView playerName = (TextView) v.findViewById(R.id.playerName);
			playerName.setText(player.getName());
			final RatingBar playerFavorite = (RatingBar) v.findViewById(R.id.ratingBar1);

			if (player.isFavorite(ApplicationSettings.getFavoritePlayer(PlaylistActivity.this))) {
				playerFavorite.setRating(1f);
			} else {
				playerFavorite.setRating(0f);
			}

			playerFavorite.setOnTouchListener(new OnTouchListener() {
			    @Override
			    public boolean onTouch(View v, MotionEvent event) {
			        if (event.getAction() == MotionEvent.ACTION_UP) {
			        	L.d("onrating changed");
						if (playerFavorite.getRating() == 1f) {
							playerFavorite.setRating(0f);
							ApplicationSettings.removeFavoritePlayer(PlaylistActivity.this, player.getId());
						} else {
							ApplicationSettings.addFavoritePlayer(PlaylistActivity.this, player.getId());
							playerFavorite.setRating(1f);
						}

						updateFavoritePlayer();
			        }
			        return true;
			    }
			});

			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(PlaylistActivity.this, PlayerProfile.class);
					ApplicationSettings.setSelectedJson(PlaylistActivity.this, player.getJson());
					startActivity(i);
				}
			});

			playlistLayout.addView(v);
		}


		ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(this, android.R.layout.simple_list_item_1, players);
		playerSearch.setAdapter(adapter);
		playerSearch.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(PlaylistActivity.this, PlayerProfile.class);
				Player selected = (Player) parent.getAdapter().getItem(position);
				ApplicationSettings.setSelectedJson(PlaylistActivity.this, selected.getJson());
				startActivity(i);
			}
		});

	}
}
