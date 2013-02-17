package com.example.myapp;

//import com.example.myapp.Messages;
//import com.example.myapp.NewsFeeds;
//import com.example.myapp.MyAccount;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.myapp.Profile_Viewer;
import com.example.myapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.location.*;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MyAccount extends Fragment implements OnClickListener,URLThread_CallBack,LocationListener
{
	View v;
	public double curLatitude=0,curLongitude=0;
	public final String AppAccessToken="****";
	URLThread urlThread_checkLocation=null;
	Button button_checkLocation,button_MyProfile;
	
	

	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
    {
    	v=inflater.inflate(R.layout.myaccount, container, false);
    	
    	button_MyProfile=(Button) v.findViewById(R.id.button_profile);
    	
    	button_MyProfile.setOnClickListener(this);
    	
    	button_checkLocation=(Button) v.findViewById(R.id.fsqrbutton);
    	button_checkLocation.setOnClickListener(this);
    	
    	
return v;
	}

    
    public void onClick(View v) {
    	Context contt=v.getContext();
    	if(v.getId()==button_checkLocation.getId())
    	{
    		//TODO
    		LocationManager locationManager =
    	            (LocationManager) contt.getSystemService(contt.LOCATION_SERVICE);

    	    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        	    startActivity(settingsIntent);
    	    }
    	    else
    	    {
    	    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
    	    	        10000,          // 10-second interval.
    	    	        10000000,             // 10 meters.
    	    	        this);
    	    	
    	    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
    	    	        10000,          // 10-second interval.
    	    	        10000000,             // 10 meters.
    	    	        this);
    	    }
    	}
    	
    	if(v.getId()==button_MyProfile.getId())
    	{
	    	Intent intent = new Intent(contt,Profile_Viewer.class);
	    	startActivity(intent);
    	//startSubActivity(new Intent(this, Profile_Viewer.class), 0);
    	}
    }





	@Override
	public void onLocationChanged(Location location) 
	{
		button_checkLocation.setText("Re-spot location");
		((TextView) v.findViewById(R.id.tv_currentLocation)).setText("location:"+location.getLatitude()+","+location.getLongitude());
		Date d=new Date();
		urlThread_checkLocation=new URLThread("https://api.foursquare.com/v2/venues/search?ll="+location.getLatitude()+","+location.getLongitude()+"&oauth_token="+AppAccessToken+"&v=20130217", this, "");
		urlThread_checkLocation.start();
	}
	@Override
	public void URLCallBack(String response) 
	{
		//TODO just testing
// 	   ((TextView) v.findViewById(R.id.tv_currentLocation)).setText("URLCallBack:"+response);
		try
		{
			if(urlThread_checkLocation!=null)
			{
				JSONArray venues=new JSONObject(response).getJSONObject("response").getJSONArray("venues");
				
				((TextView) v.findViewById(R.id.tv_currentLocation)).setText(venues.length());
				
//				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//				builder.setTitle("Venues");
//				
//				final String ar[]=new String[venues.length()];
//				for(int i=0;i<venues.length();i++)
//					ar[i]=venues.getJSONObject(i).getString("name");
//				
//				builder.setItems(ar, new DialogInterface.OnClickListener() 
//						{
//					               public void onClick(DialogInterface dialog, int which) 
//					               {
//					            	   ((TextView) v.findViewById(R.id.tv_currentLocation)).setText("My Location: "+ar[which]);
//					            	
//					            	   try{
//					            		   
//					            		   //TODO set location on server 
//					            		   venues.getJSONObject(which).getString("id");
//					            	   
//					            	   }catch(Exception e){e.printStackTrace();}
//									// The 'which' argument contains the index position
//					               // of the selected item
//					               }
//					    });
//				builder.create();
				urlThread_checkLocation=null;
			}
		}catch(Exception e){e.printStackTrace();
			try{
				((TextView) v.findViewById(R.id.tv_currentLocation)).setText(e.getMessage());
			}catch(Exception e2){}
		
		}	
	}

	@Override
	public void onProviderDisabled(String provider) {
	
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

}
