package com.asusoda.whatshappeningatasu;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Whats_Happening_At_ASUActivity extends Activity implements OnClickListener, OnSeekBarChangeListener, OnItemClickListener {
	//Debug tag
	private final String TAG = "WHAA";
	
	//UI
	private Button buttonSearch;
	private TextView textViewRadius, textViewDelay;
	private SeekBar seekBarRadius, seekBarDelay;
	private ListView listViewEvents;
	
	//Data
	private android.location.Location currentLocation;
	private EventAdapter eventAdapter;
	private int radius, delay;
	private List<Event> eventsList;
	private LocationManager locationManager;
	private LocationListener listenerCoarse;
	private LocationListener listenerFine;
	private boolean locationAvailable;
	DecimalFormat decimalFormat;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//Set default layout
    	Log.d(TAG, "Starting the app...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Link UI handles
        Log.d(TAG, "Linking UI handles...");
        buttonSearch = (Button)findViewById(R.id.buttonWhatsHappening);
        textViewRadius = (TextView)findViewById(R.id.textViewRadius);
        seekBarRadius = (SeekBar)findViewById(R.id.seekBarRadius);
        textViewDelay = (TextView)findViewById(R.id.textViewDelay);
        seekBarDelay = (SeekBar)findViewById(R.id.seekBarDelay);
        listViewEvents = (ListView)findViewById(R.id.listViewEvents);
        
        //Set default values
        radius = 125; //125 meters
        delay = 1440; //1440 minutes, 1 day
        eventsList = new ArrayList<Event>();
        locationAvailable = true;
        decimalFormat = new DecimalFormat("#.##");;
        textViewRadius.setText("Radius: " + formatRadius(radius));
        textViewDelay.setText("Delay: " + formatDelay(delay));
        
        //Register listeners
        Log.d(TAG, "Registering listeners...");
        buttonSearch.setOnClickListener(this);
        seekBarRadius.setOnSeekBarChangeListener(this);
        seekBarDelay.setOnSeekBarChangeListener(this);
        listViewEvents.setOnItemClickListener(this);
        
        //Register adapters
        Log.d(TAG, "Registering adapters...");
        eventAdapter = new EventAdapter(this,R.layout.event_item, eventsList);
        listViewEvents.setAdapter(eventAdapter);
        
        //Setup location services
        registerLocationListeners();
    }
    
    @Override
    protected void onPause() {
    	//Stop storing location
    	Log.d(TAG, "Stopping location services...");
    	locationManager.removeUpdates(listenerCoarse);
    	locationManager.removeUpdates(listenerFine);
    	super.onPause();
    }
    
    private void registerLocationListeners() {
        //Setup location manager
    	Log.d(TAG, "Creating location manager...");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        //Setup location provider criterion
        Log.d(TAG, "Creating location criterion...");
        Criteria fine = new Criteria();
        fine.setAccuracy(Criteria.ACCURACY_FINE);
        Criteria coarse = new Criteria();
        coarse.setAccuracy(Criteria.ACCURACY_COARSE);
        
        //Store last known location
        Log.d(TAG, "Storing last known location...");
        currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(fine,  true));
        
        //Setup location listeners
        Log.d(TAG, "Creating location listeners...");
        if (listenerFine == null || listenerCoarse == null) {
        	createLocationListeners();
        }

        //Register location manager with coarse location listener, poll every 500ms until fix is accurate to 1000m
        Log.d(TAG, "Registering location manager with coarse location listener...");
        locationManager.requestLocationUpdates(locationManager.getBestProvider(coarse, true), 500, 1000, listenerCoarse);
          
      	//Register location manager with fine location listener, poll every 500ms until fix is accurate to 50m
        Log.d(TAG, "Registering location manager with fine location listener...");
        locationManager.requestLocationUpdates(locationManager.getBestProvider(fine, true), 500, 50, listenerFine);
    }
    
    private void createLocationListeners() {
    	//Setup coarse location listener
    	Log.d(TAG, "Creating coarse location listener...");
    	listenerCoarse = new LocationListener() {
        	
        	public void onLocationChanged(android.location.Location location) {
        		//Get GPS fix
        		Log.d(TAG, "Location changed: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
            	currentLocation = location;
            	if (location.getAccuracy() > 1000 && location.hasAccuracy()) {
            		//Fix acquired, stop storing location
            		Log.d(TAG, "GPS Fix acquired");
            		locationManager.removeUpdates(this);
            	}
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            	switch(status) {
            	case LocationProvider.OUT_OF_SERVICE:
            	case LocationProvider.TEMPORARILY_UNAVAILABLE:
            		//Coarse GPS unavailable
            		Log.d(TAG, "Coarse GPS unavailable");
            		locationAvailable = false;
            		break;
            	case LocationProvider.AVAILABLE:
            		//Coarse GPS available
            		Log.d(TAG, "Coarse GPS available");
            		locationAvailable = true;
            		break;
            	}
            }

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
          };
          
        //Setup fine location listener
      	Log.d(TAG, "Creating fine location listener...");
      	listenerFine = new LocationListener() {
          	
          	public void onLocationChanged(android.location.Location location) {
          		//Get GPS fix
          		Log.d(TAG, "Location changed: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
              	currentLocation = location;
              	if (location.getAccuracy() > 1000 && location.hasAccuracy()) {
              		//Fix acquired, stop storing location
              		Log.d(TAG, "GPS Fix acquired");
              		locationManager.removeUpdates(this);
              	}
              }

              public void onStatusChanged(String provider, int status, Bundle extras) {
              	switch(status) {
              	case LocationProvider.OUT_OF_SERVICE:
              	case LocationProvider.TEMPORARILY_UNAVAILABLE:
              		//Fine GPS unavailable
              		Log.d(TAG, "Fine GPS unavailable");
              		locationAvailable = false;
              		break;
              	case LocationProvider.AVAILABLE:
              		//Fine GPS available
            		Log.d(TAG, "Fine GPS available");
              		locationAvailable = true;
              		break;
              	}
              }

              public void onProviderEnabled(String provider) {}

              public void onProviderDisabled(String provider) {}
            };
    }

	@Override
	public void onClick(View v) {
	    switch (v.getId()) {
	    case R.id.buttonWhatsHappening:
	      if(currentLocation == null) {
	    	  Toast.makeText(this, "No location found, make sure GPS is turned on.", Toast.LENGTH_LONG).show();
	    	 break;
	      }
	      Toast.makeText(this, "Working...", Toast.LENGTH_SHORT).show();
	      Log.i(TAG, "Searching for events within a " + radius + "m radius of (" + currentLocation.getLatitude() + ", " + currentLocation.getLongitude() + ")");
	      eventAdapter.clear();
	      runJSONParser(currentLocation.getLatitude(), currentLocation.getLongitude());
	      if(eventsList.isEmpty()){ 
	    	  Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
	      }
	      for(Event ev : eventsList) {
	    	  eventAdapter.add(ev);
			  Log.i(TAG, ev.getName() + " in " + ev.getLocation().getBuilding().getName());
		  }
		  eventAdapter.notifyDataSetChanged();
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
	
	public void runJSONParser(double latitude, double longitude) {
		eventsList.clear();
        try{
        	InputStream source = getJSONData("http://slyduck.com/api/events/near/" + latitude + "," + longitude + "/?radius=" + radius + "&delay=" + delay);
        	Gson gson = new Gson();
        	Reader reader = new InputStreamReader(source);
        	eventsList = gson.fromJson(reader, new TypeToken<ArrayList<Event>>(){}.getType());
        }catch(Exception ex){
            ex.printStackTrace();
        }
        if(eventsList == null) {
        	eventsList = new ArrayList<Event>();
        }
    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.seekBarDelay:
			delay = progress;
			if(delay < 30) {
				delay = 30;
			}
	        textViewDelay.setText("Delay: " + formatDelay(delay));
			break;
		case R.id.seekBarRadius:
			radius = progress;
			if(radius < 10) {
				radius = 10;
			}
			textViewRadius.setText("Radius: " + formatRadius(radius));
			break;
		}
	}

	private String formatDelay(int inDelay)
	{
		String temp = inDelay + " minutes";
		
		if(inDelay >= 60 && inDelay < 1440) {
			temp = decimalFormat.format(((double)inDelay/60.0d)) + " hours";
		}else if(delay >= 1440) {
			temp = decimalFormat.format(((double)inDelay/1440.0d)) + " days";
		}
		
		return temp;
	}
	
	private String formatRadius(int inRadius)
	{
		String temp = inRadius + " meters";
		
		if(inRadius >= 100) {
			temp = decimalFormat.format(((double)inRadius/1000.0d)) + " kilometers";
		}
		
		return temp;
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	private class EventAdapter extends ArrayAdapter<Event> {

        private List<Event> events;

        public EventAdapter(Context context, int textViewResourceId, List<Event> items) {
                super(context, textViewResourceId, items);
                this.events = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.event_item, null);
                }
                Event e = events.get(position);
                if (e != null) {
                        TextView textViewEventName = (TextView) v.findViewById(R.id.textViewEventName);
                        TextView textViewEventOrganization = (TextView) v.findViewById(R.id.textViewEventOrganization);
                        TextView textViewEventLocation = (TextView) v.findViewById(R.id.textViewEventLocation);
                        TextView textViewEventStartTime = (TextView) v.findViewById(R.id.textViewEventStartTime);
                        if (textViewEventName != null) {
                              textViewEventName.setText(e.getName());
                        }
                        if (textViewEventOrganization != null) {
                            textViewEventOrganization.setText(e.getOrganization().getName());
                        }
                        if (textViewEventLocation != null) {
                            textViewEventLocation.setText("Where: " + e.getLocation().toString());
                        }
                        if (textViewEventStartTime != null) {
                            textViewEventStartTime.setText("When: " + e.getStart_timeframe());
                        }
                }
                return v;
        }
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		try {
		Intent mapCall = new Intent(Intent.ACTION_VIEW, eventsList.get(arg2).getLocation().getBuilding().getDirectionsFrom(currentLocation));
		startActivity(mapCall); 
		}catch(ActivityNotFoundException ex) {
			ex.printStackTrace();
		}
	}
}