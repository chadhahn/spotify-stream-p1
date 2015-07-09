package com.example.chahn.spotifystreamer.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import com.example.chahn.spotifystreamer.R;
import com.example.chahn.spotifystreamer.adapter.TrackArrayAdapter;
import com.example.chahn.spotifystreamer.model.SpotifyArtist;
import com.example.chahn.spotifystreamer.model.SpotifyTrack;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Detail activity for displaying top tracks from selected album
 */
public class DetailActivity extends AppCompatActivity {

  /**
   * Hard-coded country code map required by Spotify API
   */
  public static final ImmutableMap<String, Object> COUNTRY_MAP =
      ImmutableMap.<String, Object>of("country", "US");

  private TrackArrayAdapter trackArrayAdapter;
  private ListView detailListView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.artist_detail);

    trackArrayAdapter = new TrackArrayAdapter(this, R.layout.detail_row);
    detailListView = (ListView) findViewById(R.id.detail_list_view);
    detailListView.setAdapter(trackArrayAdapter);

    //Grab intent and load the track data
    Intent intent = getIntent();
    if (intent != null && intent.hasExtra(SpotifyArtist.INTENT_TAG)) {
      SpotifyArtist spotifyArtist =
          (SpotifyArtist) intent.getSerializableExtra(SpotifyArtist.INTENT_TAG);
      new SpotifyTrackTask().execute(spotifyArtist);
      getSupportActionBar().setSubtitle(spotifyArtist.getArtistName());
    }
  }

  /**
   * Task to load tracks for the given artist
   */
  public class SpotifyTrackTask extends AsyncTask<SpotifyArtist, Void, List<SpotifyTrack>> {
    @Override protected List<SpotifyTrack> doInBackground(SpotifyArtist... params) {
      SpotifyApi api = new SpotifyApi();
      Tracks tracks = api.getService()
          .getArtistTopTrack(params[0].getId(), COUNTRY_MAP);
      return new SpotifyTrack.TrackTransformer().transform(tracks.tracks);
    }

    @Override protected void onPostExecute(List<SpotifyTrack> spotifyTracks) {
      super.onPostExecute(spotifyTracks);
      trackArrayAdapter.addAll(spotifyTracks);
      trackArrayAdapter.notifyDataSetChanged();
    }
  }

}
