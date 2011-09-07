package com.asusoda.whatshappeningatasu;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Whats_Happening_At_ASUActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {
	private final String TAG = "Whats Happening @ ASU";
	private android.location.Location currentLocation;
	Button buttonSearch;
	SeekBar radiusBar, delayBar;
	private int radius = 125;
	private int delay = 1440;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	Log.i(TAG, "Opened the app");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        buttonSearch = (Button)findViewById(R.id.buttonSearch);
        radiusBar = (SeekBar)findViewById(R.id.radiusBar);
        delayBar = (SeekBar)findViewById(R.id.delayBar);
        
        buttonSearch.setOnClickListener(this);
        radiusBar.setOnSeekBarChangeListener(this);
        delayBar.setOnSeekBarChangeListener(this);
        
        Log.i(TAG, "Creating location manager...");
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        Log.i(TAG, "Creating location listener...");
        LocationListener locationListener = new LocationListener() {
        	
        	public void onLocationChanged(android.location.Location location) {
              locationChanged(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
          };
          
          Log.i(TAG, "Registering location manager with location listener...");
          locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }
    
    private void locationChanged(android.location.Location location) {
    	Log.i(TAG, "Location changed: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
    	currentLocation = location;
    }
    

	@Override
	public void onClick(View v) {
	    switch (v.getId()) {
	    case R.id.buttonSearch:
	      if(currentLocation == null) {
	    	  Toast.makeText(this, "No location found, make sure GPS is turned on.", Toast.LENGTH_LONG).show();
	    	 break;
	      }
	      Log.i(TAG, "onClick: Searching for events within a " + radius + "m radius of (" + currentLocation.getLatitude() + ", " + currentLocation.getLongitude() + ")");
	      Toast.makeText(this, "Searching for events within a " + radius + "m radius...", Toast.LENGTH_LONG).show();
		  List<Event> events = runJSONParser(currentLocation.getLatitude(), currentLocation.getLongitude());
	      for(Event ev : events)
		  {
			  Log.i(TAG, ev.getName() + " in " + ev.getLocation().getBuilding().getName());
		  }
		  Toast.makeText(this, "Found " + events.size() + " events...", Toast.LENGTH_LONG).show();
	      break;
	    }
	  }
	
	public InputStream getJSONData(String url) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(url);
        try {
        	HttpResponse getResponse = client.execute(getRequest);
        	final int statusCode = getResponse.getStatusLine().getStatusCode();
        	if(statusCode != HttpStatus.SC_OK) {
        		Log.e(TAG, "Error " + statusCode + " for URL " + url);
        		return null;
        	}
        	
        	HttpEntity getResponseEntity = getResponse.getEntity();
        	return getResponseEntity.getContent();
        }catch(IOException e) {
        	getRequest.abort();
        	Log.e(getClass().getSimpleName(), "Error for URL " + url, e);
        }
        
        return null;
    }
	
	public List<Event> runJSONParser(double latitude, double longitude) {
		List<Event> events = new ArrayList<Event>();
        try{
        	InputStream source = getJSONData("http://slyduck.com/api/events/near/" + latitude + "," + longitude + "/?radius=" + radius + "&delay=" + delay);
        	Gson gson = new Gson();
        	Reader reader = new InputStreamReader(source);
        	events = gson.fromJson(reader, new TypeToken<ArrayList<Event>>(){}.getType());
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return events;
    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.delayBar:
			delay = progress;
			break;
		case R.id.radiusBar:
			radius = progress;
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
}