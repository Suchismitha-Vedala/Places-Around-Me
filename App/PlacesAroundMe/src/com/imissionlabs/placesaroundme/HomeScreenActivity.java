package com.imissionlabs.placesaroundme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeScreenActivity extends FragmentActivity implements
		OnMyLocationChangeListener, OnClickListener {

	private GoogleMap map;
	private ProgressDialog dialog;
	private LatLng myLocation;
	private Marker m;
	private Button shareLocation;
	private ImageView search;
	private EditText searchQuery;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		shareLocation = (Button) findViewById(R.id.sharelocation);
		searchQuery=(EditText) findViewById(R.id.searchTxt);
		search=(ImageView)findViewById(R.id.search);
		map.setMyLocationEnabled(true);
		turnGPSOn();
		map.setOnMyLocationChangeListener(this);
		dialog = ProgressDialog.show(this, null, "Fetching current location");
		dialog.setCancelable(false);
		shareLocation.setOnClickListener(this);
		search.setOnClickListener(this);

	}

	private void turnGPSOn() {
		String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

		if (!provider.contains("gps")) { // if gps is disabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			sendBroadcast(poke);
		}
	}

	@Override
	public void onMyLocationChange(Location location) {
		if (m != null) {
			m.remove();
			m = null;
		}
		myLocation = new LatLng(location.getLatitude(), location.getLongitude());
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(myLocation);
		markerOptions.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		markerOptions.title("Hey! I am here..");
		m = map.addMarker(markerOptions);
		m.showInfoWindow();
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
		dialog.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sharelocation:
			if (myLocation != null) {

				Double latitude = myLocation.latitude;
				Double longitude = myLocation.longitude;

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
			}
			break;

		case R.id.search:
			Intent intent=new Intent(HomeScreenActivity.this, PlacesActivity.class);
			intent.putExtra("location", myLocation);
			intent.putExtra("query", searchQuery.getText().toString());
			startActivity(intent);
			break;

		default:
			break;
		}
	}

}
