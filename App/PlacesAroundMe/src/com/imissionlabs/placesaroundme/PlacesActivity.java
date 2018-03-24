package com.imissionlabs.placesaroundme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class PlacesActivity extends Activity {

	private ListView mPlaces;

	private ArrayList<Place> places = new ArrayList<Place>();
	private String query;
	private String URL = "https://api.foursquare.com/v2/venues/search?client_id=5AS5TOFS3IG0RSN1KC4LHC4ZAZ3NV1LBO0Q5X0CKXFBP1N2G&client_secret=NUVRKDB2105Y11OQ5XRT25AOBW2KUNIVY3M1CZSIX52FVOGF&v=20130815&llimit=50";
	private int pos;
	private SeekBar mSeekBar;
	private TextView radius,results;
	private String radiusValue="1000";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);
		mPlaces = (ListView) findViewById(R.id.listView1);
		mSeekBar=(SeekBar)findViewById(R.id.seekBar1);
		mSeekBar.setMax(10000);
		mSeekBar.setProgress(1000);
		radius=(TextView)findViewById(R.id.radius);
		results=(TextView)findViewById(R.id.total);
		results.setText("Total places found : "+places.size());

		radius.setText("Radius : 1000 m");
		LatLng location = getIntent().getParcelableExtra("location");
		query = getIntent().getStringExtra("query");

		URL = URL + "&ll=" + location.latitude + "," + location.longitude
				+ "&query=" + query.trim()+"&radius=";
		
		GetPlacesActivity task = new GetPlacesActivity();
		task.execute();
		
		mPlaces.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				pos=arg2;
				CustomAlertDialog d=new CustomAlertDialog(PlacesActivity.this);
				d.show();
			}
		});
		
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				places.clear();
				GetPlacesActivity task = new GetPlacesActivity();
				task.execute();
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				radiusValue=""+progress;

				radius.setText("Radius :"+progress+" m");
			}
		});
	}

	
	public class CustomAlertDialog extends AlertDialog {

		private TextView navigate, share;

		public CustomAlertDialog(Context context) {
			super(context);

		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.custom_alert_inapp);
			navigate = (TextView) findViewById(R.id.navigate);
			share = (TextView) findViewById(R.id.share);
			navigate.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Double latitude = places.get(pos).getLocation().latitude;
					Double longitude =  places.get(pos).getLocation().longitude;
					Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude+","+longitude);
					Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
					mapIntent.setPackage("com.google.android.apps.maps");
					startActivity(mapIntent);
					dismiss();
				}
			});
			share.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Double latitude = places.get(pos).getLocation().latitude;
					Double longitude =  places.get(pos).getLocation().longitude;

					String uri = "http://maps.google.com/maps?saddr=" + latitude
							+ "," + longitude;

					Intent sharingIntent = new Intent(
							android.content.Intent.ACTION_SEND);
					sharingIntent.setType("text/plain");
					String ShareSub = "Here is my location";
					sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
							ShareSub);
					sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
					startActivity(Intent.createChooser(sharingIntent, "Share via"));
					dismiss();
				}
			});
		}

	}

	private class GetPlacesActivity extends AsyncTask<Void, Void, Void> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(PlacesActivity.this, null,
					"Getting neary locations");
			dialog.setCancelable(false);
		}

		@Override
		protected Void doInBackground(Void... params) {

			String response = request(URL+radiusValue).toString();
			try {
				places.clear();
				JSONObject obj = new JSONObject(response);
				JSONArray venues = obj.getJSONObject("response").getJSONArray(
						"venues");
				for (int i = 0; i < venues.length(); i++) {
					JSONObject venue = venues.getJSONObject(i);
					Place place = new Place();
					place.setName(venue.getString("name"));
					JSONObject location = venue.getJSONObject("location");
					StringBuilder sb = new StringBuilder();
					sb.append(location.has("address") ? location
							.getString("address") : "Address not available");
					sb.append(location.has("crossStreet") ? location
							.getString("crossStreet") : "");
					place.setAddress(sb.toString());
					place.setLocation(new LatLng(location.getDouble("lat"),
							location.getDouble("lng")));
					place.setDistance(location.getInt("distance"));
					places.add(place);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss();
			System.out.println("SIZE :"+places.size());
			if (places.size() > 0) {
				mPlaces.setVisibility(View.VISIBLE);
				mPlaces.setAdapter(new PlacesAdpater());
				results.setText("Total places found : "+places.size());
				
			} else {
				mPlaces.setVisibility(View.GONE);
				results.setText("Sorry! No places found");
			}
		}

	}

	private StringBuffer request(String urlString) {
		System.out.println(urlString);
		StringBuffer chaine = new StringBuffer("");
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.connect();

			InputStream inputStream = connection.getInputStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					inputStream));
			String line = "";
			while ((line = rd.readLine()) != null) {
				chaine.append(line);
			}

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return chaine;
	}

	private class PlacesAdpater extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return places.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			Place place = places.get(arg0);
			arg1 = LayoutInflater.from(PlacesActivity.this).inflate(
					R.layout.item_places, null);
			TextView name = (TextView) arg1.findViewById(R.id.place_name);
			TextView address = (TextView) arg1.findViewById(R.id.place_address);
			TextView distance = (TextView) arg1
					.findViewById(R.id.place_distance);
			if (place.getDistance() > 1000) {
				distance.setText(place.getDistance() / 1000 + " km");
			} else {
				distance.setText(place.getDistance() + " m");
			}

			name.setText(place.getName());
			address.setText(place.getAddress());
			return arg1;
		}

	}

}
