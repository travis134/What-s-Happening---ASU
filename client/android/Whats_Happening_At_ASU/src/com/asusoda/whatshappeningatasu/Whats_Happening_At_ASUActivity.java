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
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	public final static String TAG = "WHAA";
	
	//UI
	private Button buttonSearch;
	private TextView textViewRadius, textViewDelay, textViewEvents;
	private SeekBar seekBarRadius, seekBarDelay;
	private ListView listViewEvents;
	
	//Data
	private EventAdapter eventAdapter;
	private int radius, delay;
	private List<Event> eventsList;
	private LocationHelper locationHelper;
	private boolean formatMetric;
	private Toast toastMessage;
	DecimalFormat decimalFormat;
	
	//Activity lifecycle overrides
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "Created.");
    	//Welcome!
    	toastMessage = Toast.makeText(this, null, Toast.LENGTH_LONG);
    	
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
        textViewEvents = (TextView)findViewById(R.id.textViewEvents);
        listViewEvents = (ListView)findViewById(R.id.listViewEvents);
        
        //Set default values
        radius = 125; //125 meters
        delay = 60; //60 minutes
        eventsList = new ArrayList<Event>();
        formatMetric = false;
        decimalFormat = new DecimalFormat("#.##");
        
        //Register adapters
        Log.d(TAG, "Registering adapters...");
        eventAdapter = new EventAdapter(this,R.layout.event_item, eventsList);
        listViewEvents.setAdapter(eventAdapter);
        
        //Register listeners
        Log.d(TAG, "Registering listeners...");
        buttonSearch.setOnClickListener(this);
        seekBarRadius.setOnSeekBarChangeListener(this);
        seekBarDelay.setOnSeekBarChangeListener(this);
        listViewEvents.setOnItemClickListener(this);
        
        //Load any saved data
        Log.d(TAG, "Loading any saved data...");
        final SaveData data = (SaveData) getLastNonConfigurationInstance();
        if(data != null) {
        	radius = data.getRadius();
        	delay = data.getDelay();
        	formatMetric = data.getFormatMetric();
        	eventsList = (ArrayList<Event>) data.getEventsList();
        }
        
        //Setup UI values
        Log.d(TAG, "Setting UI values...");
        textViewRadius.setText("Radius: " + formatRadius(radius));
        textViewDelay.setText("Delay: " + formatDelay(delay));
    	seekBarRadius.setProgress(radius);
    	seekBarDelay.setProgress(delay);
    	populateEventsList();
    }
    
    @Override
    protected void onResume() {
    	Log.d(TAG, "Resumed.");
    	//Setup location services
    	Log.d(TAG, "Creating location helper...");
    	locationHelper = new LocationHelper(this);

        if(!locationHelper.isGpsEnabled()) {
            buildAlertMessageNoGps();
        }
        super.onResume();
    }
    
    @Override
    protected void onPause() {
    	
    	Log.d(TAG, "Paused.");
    	locationHelper.dispose();
    	super.onPause();
    }
    
    public void onDestroy() {
    	Log.d(TAG, "Destroyed.");
    	super.onDestroy();
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	final SaveData data = new SaveData(radius, delay, formatMetric, (ArrayList<Event>)eventsList);
    	return data;
    }
    
    //Activity event overrides
    @Override
	public void onClick(View v) {
	    switch (v.getId()) {
	    case R.id.buttonWhatsHappening:
	      if(!locationHelper.isLocationAvailable()) {
	    	  Toast.makeText(this, "Waiting for location...", Toast.LENGTH_SHORT).show();
	    	  listViewEvents.setVisibility(8);
	    	  textViewEvents.setVisibility(8);
	    	  break;
	      }
	      Toast.makeText(this, "Working...", Toast.LENGTH_SHORT).show();
	      eventAdapter.clear();
	      new Thread(new Runnable() {
	    	    public void run() {
				      Log.i(TAG, "Searching for events within a " + radius + "m radius of (" + locationHelper.getLocation().getLatitude() + ", " + locationHelper.getLocation().getLongitude() + ") with a delay of " + delay);
				      //eventAdapter.clear();
				      runJSONParser(locationHelper.getLocation().getLatitude(), locationHelper.getLocation().getLongitude());
				      runOnUiThread(new Runnable() {
				    	     public void run() {
				    	    	 populateEventsList();
				    	     }
				      });
				      
	    	    }
	      }).start();
	      break;
	    }
	  }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.whaa_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.unit:
			formatMetric = !formatMetric;
			textViewRadius.setText("Radius: " + formatRadius(radius));
			return true;
		default:
			return super.onOptionsItemSelected(item);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		try {
		Intent mapCall = new Intent(Intent.ACTION_VIEW, eventsList.get(arg2).getLocation().getBuilding().getDirections());
		startActivity(mapCall); 
		}catch(ActivityNotFoundException ex) {
			toastMessage.cancel();
			Toast.makeText(this, "No maps app found, please install Google Maps.", Toast.LENGTH_LONG);
			toastMessage.show();
			ex.printStackTrace();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.seekBarDelay:
			delay = progress;
			if(delay < 60) {
				delay = 30;
			}else if(delay >= 60 && delay < 90) {
				delay = 60;
			}else if(delay >= 90 && delay < 120) {
				delay = 90;
			}else if(delay >= 120 && delay < 150) {
				delay = 120;
			}else if(delay >= 150 && delay < 180) {
				delay = 150;
			}else if(delay >= 180 && delay < 210) {
				delay = 180;
			}else if(delay >= 210 && delay < 240) {
				delay = 210;
			}else if(delay >= 240) {
				delay = 240;
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
    
    private void populateEventsList() {
    	if(eventsList == null){
    		return;
    	}
    	
    	if(eventsList.isEmpty())
    	{
        	listViewEvents.setVisibility(8);
        	textViewEvents.setVisibility(8);
    	}else{
    		listViewEvents.setVisibility(0);
        	textViewEvents.setVisibility(0);
        	for(Event ev : eventsList) {
        		eventAdapter.add(ev);
        		Log.i(TAG, ev.getName() + " in " + ev.getLocation().getBuilding().getName());
        		}
        	eventAdapter.notifyDataSetChanged();
        	toastMessage.cancel();
    	}
    }
    
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need to have GPS enabled to use this application. Would you like to enable GPS?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(final DialogInterface dialog, final int id) {
                	   startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                   }
               });
        final AlertDialog alert = builder.create();
        alert.show();
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

	private String formatDelay(int inDelay)
	{
		String unit;
		double temp = inDelay;
		
		if(temp >= 60)
		{
			temp /= 60.0d;
			unit = "hour(s)";
		}else{
			unit = "minute(s)";
		}

		return decimalFormat.format(temp) + " " + unit;
	}
	
	private String formatRadius(int inRadius)
	{
		String unit;
		double temp = inRadius;
		
		if(formatMetric)
		{
			if(temp >= 100) {
				temp /= 1000.0d;
				unit = "kilometer(s)";
			}else{
				unit = "meter(s)";
			}
		}else{
			if(temp >= 152.336) {
				temp /= 1609.344d;
				unit = "mile(s)";
			}else{
				temp /= 0.3048d;
				unit = "feet";
			}
		}
		
		return decimalFormat.format(temp) + " " + unit;
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
}