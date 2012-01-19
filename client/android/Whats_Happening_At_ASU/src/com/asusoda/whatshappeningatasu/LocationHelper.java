package com.asusoda.whatshappeningatasu;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class LocationHelper {
	private android.location.Location currentLocation;
	private LocationManager locationManager;
	private Criteria fine, coarse;
	private LocationListener fineListener, coarseListener;
	private String fineProvider, coarseProvider;
	private Boolean gpsEnabled;
	
	public LocationHelper(Context ctx)  {
		locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		
		//Setup criteria
		fine = new Criteria();
		fine.setAccuracy(Criteria.ACCURACY_FINE);
		coarse = new Criteria();
		coarse.setAccuracy(Criteria.ACCURACY_COARSE);
		
		//Set location to last known location in case listeners take longer than expected
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Log.d(Whats_Happening_At_ASUActivity.TAG, "gps enabled");
			gpsEnabled = true;
			fineProvider = locationManager.getBestProvider(fine, true);
			currentLocation = locationManager.getLastKnownLocation(fineProvider);
			Log.d(Whats_Happening_At_ASUActivity.TAG, "Last known gps location: (" + currentLocation.getLatitude() + ", " + currentLocation.getLongitude() + ") with accuracy: " + currentLocation.getAccuracy());
			fineListener = new CustomLocationListener(fineProvider, 50.0f);
			locationManager.requestLocationUpdates(fineProvider, 500, 50, fineListener);
		}else{
			Log.d(Whats_Happening_At_ASUActivity.TAG, "gps not enabled");
			gpsEnabled = false;
			coarseProvider = locationManager.getBestProvider(coarse, true);
			currentLocation = locationManager.getLastKnownLocation(coarseProvider);
			Log.d(Whats_Happening_At_ASUActivity.TAG, "Last known network location: (" + currentLocation.getLatitude() + ", " + currentLocation.getLongitude() + ") with accuracy: " + currentLocation.getAccuracy());
			coarseListener = new CustomLocationListener(coarseProvider, 1000.0f);
			locationManager.requestLocationUpdates(coarseProvider, 500, 1000, coarseListener);
		}
	}
	
	public Boolean isGpsEnabled() {
		return gpsEnabled;
	}
	
	public Boolean isLocationAvailable() {
		return (currentLocation != null);
	}
	
	public android.location.Location getLocation() {
		return currentLocation;
	}
	
	public void dispose() {
		if(isGpsEnabled())
		{
			locationManager.removeUpdates(fineListener);
		}
		locationManager.removeUpdates(coarseListener);
	}
	
	private final class CustomLocationListener implements LocationListener {

		private float mRequiredAccuracy;
		private String mProvider;
		
		public CustomLocationListener(String provider, float requiredAccuracy) {
			super();
			mProvider = provider;
			mRequiredAccuracy = requiredAccuracy;
		}
		
		@Override
		public void onLocationChanged(Location location) {
			//Try to get fix
    		Log.d(Whats_Happening_At_ASUActivity.TAG, mProvider + " Location changed: (" + location.getLatitude() + ", " + location.getLongitude() + ") with accuracy: " + location.getAccuracy());
        	currentLocation = location;
        	if (location.hasAccuracy() && location.getAccuracy() < mRequiredAccuracy) {
        		//Fix acquired, stop storing location
        		Log.d(Whats_Happening_At_ASUActivity.TAG, mProvider + " Fix acquired");
        		locationManager.removeUpdates(this);
        	}	
		}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch(status) {
	          	case LocationProvider.OUT_OF_SERVICE:
	          		Log.d(Whats_Happening_At_ASUActivity.TAG, mProvider + " out of service");
	          		if(provider.equals(LocationManager.GPS_PROVIDER))
	          		{
	          			gpsEnabled = false;
	          		}
	          		break;
	          	case LocationProvider.TEMPORARILY_UNAVAILABLE:
	          		Log.d(Whats_Happening_At_ASUActivity.TAG, mProvider + " unavailable");
	          		if(provider.equals(LocationManager.GPS_PROVIDER))
	          		{
	          			gpsEnabled = true;
	          		}
	          		break;
	          	case LocationProvider.AVAILABLE:
	        		Log.d(Whats_Happening_At_ASUActivity.TAG, mProvider + " available");
	        		if(provider.equals(LocationManager.GPS_PROVIDER))
	          		{
	          			gpsEnabled = true;
	          		}
	          		break;
			}
		}
		
	}
	
}
