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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Whats_Happening_At_ASUActivity extends Activity implements OnClickListener {
	private final String TAG = "Whats Happening @ ASU";
	private android.location.Location currentLocation;
	Button buttonStart, buttonStop;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);
        
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        
        
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        LocationListener locationListener = new LocationListener() {
        	
        	public void onLocationChanged(android.location.Location location) {
              // Called when a new location is found by the network location provider.
              locationChanged(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
          };
          
          locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }
    
    private void locationChanged(android.location.Location location) {
    	currentLocation = location;
    }
    

	@Override
	public void onClick(View v) {
	    switch (v.getId()) {
	    case R.id.buttonStart:
	      Log.d(TAG, "onClick: starting");
	      Toast.makeText(this, "Parsing events...", Toast.LENGTH_SHORT).show();
	      if(currentLocation == null) {
	    	 break;
	      }
		  for(Event ev : runJSONParser(currentLocation.getLatitude(), currentLocation.getLongitude()))
		  {
			  Log.d(TAG, ev.getName() + " - " + ev.getOrganization().getOwner().getUsername());
		  }
	      break;
	    case R.id.buttonStop:
	      Log.d(TAG, "onClick: stopping");
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
        	InputStream source = getJSONData("http://slyduck.com/api/events/near/" + latitude + "," + longitude + "/?radius=200");
        	Gson gson = new Gson();
        	Reader reader = new InputStreamReader(source);
        	events = gson.fromJson(reader, new TypeToken<ArrayList<Event>>(){}.getType());
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return events;
    }
}