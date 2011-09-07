package com.asusoda.whatshappeningatasu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

public class Whats_Happening_At_ASUActivity extends Activity implements OnClickListener {
	public static final String TAG = "Whats Happening @ ASU";
	Button buttonStart, buttonStop;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);
        
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
	    switch (v.getId()) {
	    case R.id.buttonStart:
	      Log.d(TAG, "onClick: starting");
	      Toast.makeText(this, "Parsing events...", Toast.LENGTH_SHORT).show();
		  for(Event ev : runJSONParser())
		  {
			  Log.d(TAG, ev.getName() + " - " + ev.getStart_timeframe_date().toString());
		  }
	      break;
	    case R.id.buttonStop:
	      Log.d(TAG, "onClick: stopping");
	      break;
	    }
	  }
	
	public InputStream getJSONData(String url) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        URI uri;
        InputStream data = null;
        try {
            uri = new URI(url);
            HttpGet method = new HttpGet(uri);
            HttpResponse response = httpClient.execute(method);
            data = response.getEntity().getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return data;
    }
	
	public List<Event> runJSONParser() {
		List<Event> events = new ArrayList<Event>();
        try{
        	//This doesn't work, although it used to
	        Gson gson = new Gson();
	        String url = "http://slyduck.com/api/events/near/33.41833212374169%2C-111.93501949310303/?radius=200";
	        URI uri = new URI(url);
	        HttpGet get = new HttpGet(uri);
	        HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(get);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	        StringBuilder builder = new StringBuilder();
	        for (String line = null; (line = reader.readLine()) != null;) {
	            builder.append(line).append("\n");
	        }
	        JSONTokener tokener = new JSONTokener(builder.toString());
	        JSONArray array = new JSONArray(tokener);
	        for(int i = 0; i < array.length(); i++){
	        	Log.v(TAG, array.getJSONObject(i).toString());
	        	Event event = gson.fromJson(array.getJSONObject(i).toString(), Event.class);
	        	events.add(event);
	        }
	        /*Gson gson = new Gson();
	        Reader r = new InputStreamReader(getJSONData("http://slyduck.com/api/events/all/?delay=1000000"));
	        JsonElement json = new JsonParser().parse(r);
	        JsonArray array= json.getAsJsonArray();
	        Iterator<JsonElement> iterator = array.iterator();
	        while(iterator.hasNext()){
	        	JsonElement json2 = (JsonElement)iterator.next();
	        	Event event = gson.fromJson(json2, Event.class);
	        	events.add(event);
	        }*/
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return events;
    }
}