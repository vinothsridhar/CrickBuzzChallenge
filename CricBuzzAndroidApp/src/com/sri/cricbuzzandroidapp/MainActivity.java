package com.sri.cricbuzzandroidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sri.cricbuzzandroidapp.api.AsyncConnection;
import com.sri.cricbuzzandroidapp.api.ConnectionListener;
import com.sri.cricbuzzandroidapp.utils.ApplicationSettings;
import com.sri.cricbuzzandroidapp.utils.L;
import com.sri.cricbuzzandroidapp.utils.ServerUtils;


public class MainActivity extends Activity {

	private TextView loadingText;
	private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingText = (TextView) findViewById(R.id.loadingText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

//        if (ApplicationSettings.getPlaylistJson(this) != null) {
//			Intent i = new Intent(MainActivity.this, PlaylistActivity.class);
//			startActivity(i);
//			finish();
//			return;
//        }

        AsyncTask<Void, Void, Void> playlistConnection = new AsyncConnection(AsyncConnection.METHOD_GET, ServerUtils.PLAYLIST_URL, null, null, new ConnectionListener() {

			@Override
			public void OnSuccess(String result) {
				L.d("Final result: " + result);
				ApplicationSettings.setPlaylistJson(MainActivity.this, result);
				Intent i = new Intent(MainActivity.this, PlaylistActivity.class);
				startActivity(i);
				finish();
			}

			@Override
			public void OnFailure(String error, int status) {
				L.d("error: " + error);
				progressBar.setVisibility(View.GONE);
				loadingText.setText("Error: " + error + "Status: " + status);
			}

			@Override
			public void OnConnectionError(int errorCode) {

			}
		}).execute();

    }
}
