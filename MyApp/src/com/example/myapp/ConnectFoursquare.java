package com.example.myapp;

import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class ConnectFoursquare
{
	public static boolean isConnected()
	{
		try{
			File file=new File(Environment.getExternalStorageDirectory().getPath()+"/foursquareAccessToken.txt");
			return file.exists();
		}catch(Exception e){return false;}
	}
	public static String getAccessToken()
	{
		try{
			FileInputStream in=new FileInputStream(Environment.getExternalStorageDirectory().getPath()+"/foursquareAccessToken.txt");
			byte buff[]=new byte[200];
			int o;
			String res="";
			while((o=in.read(buff))>0)
				res+=new String(buff, 0, o);
			return res;
		}catch(Exception e){return "";}
	}
	public static void connect(Activity activity)
	{
		// redirects to http://localhost:AppPort/foursquare?accesstoken=TOKEN to be stored locally
		try{
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("https://foursquare.com/oauth2/authenticate?client_id=BZ4QPVWSF213QA2ICE1QSHIGDMCNZBW20QD3EXBVH0OHG3IT&response_type=code&redirect_uri=http://yoga1290.appspot.com/schoolmate/oauth/foursquare/callback/"));
			activity.startActivity(i);
		}catch(Exception e){e.printStackTrace();}
	}
	
	
}
