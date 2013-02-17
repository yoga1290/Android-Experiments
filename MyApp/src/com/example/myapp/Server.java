package com.example.myapp;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Environment;

class ServerProperties
{
	public static final int port=1290;
}
class ServerData
{
	public LinkedList<String> followers;
	public boolean isFree2Listen=true;
	public byte buff[];
	public ServerData()
	{
		followers=new LinkedList<String>();
	}
	public void addClient(String IP)
	{
		followers.add(IP);
	}
	public void send2Followers(final byte data[])
	{
		final Iterator<String> it=followers.iterator();
		while(it.hasNext())
		{
			new Thread(new Runnable() {
				
				@Override
				public void run() 
				{
					try
					{
						String cur[]=it.next().split(".");
						Socket s=new Socket(InetAddress.getByAddress(new byte[]{new Byte(cur[0]),new Byte(cur[1]),new Byte(cur[2]),new Byte(cur[3])}) , ServerProperties.port);
						
						InputStream in=s.getInputStream();
						OutputStream out=s.getOutputStream();
						
						out.write("LISTEN".getBytes());
						String resp="";
						int o;
						byte buff[]=new byte[200];
						while((o=in.read(buff))>0)
							resp+=new String(buff,0,o);
						in.close();
						out.close();
						
						new Socket(InetAddress.getByAddress(new byte[]{new Byte(cur[0]),new Byte(cur[1]),new Byte(cur[2]),new Byte(cur[3])}) , 
										Integer.parseInt(resp.split(" ")[1])	).getOutputStream().write(data);
					}catch(Exception e){e.printStackTrace();}
					
				}
			}).start();
		}
	}
}

class DataTransferThread extends Thread implements Runnable
{
	private int port=1291;
	private ServerData data=null;
	public DataTransferThread(int port,ServerData data)
	{
		this.port=port;
		this.data=data;//to get followers
	}
	@Override
	public void run()
	{
		try
		{
			ServerSocket ss = new ServerSocket(port);
            Socket s = ss.accept();
            InputStream in=s.getInputStream();
            int bufferSize = AudioRecord.getMinBufferSize(44100,AudioFormat.CHANNEL_IN_STEREO,AudioFormat.ENCODING_PCM_16BIT);
            byte buff[]=new byte[bufferSize];
			// Create a new AudioTrack object using the same parameters as the AudioRecord
			// object used to create the file.
			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 
			44100,//11025, 
			AudioFormat.CHANNEL_IN_STEREO,//AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioFormat.ENCODING_PCM_16BIT,//AudioFormat.ENCODING_PCM_16BIT, 
			bufferSize,// 
			AudioTrack.MODE_STREAM);
			// Start playback
			audioTrack.play();
			int offset=0;
	
			while((offset=in.read(buff))>0)
			{
				// Write the music buffer to the AudioTrack object
				audioTrack.write(buff, 0, offset);
				//Pass what you hear to your followers
				data.send2Followers(buff);//TODO sub-buff(0,o)
			}
			audioTrack.stop();
			
			audioTrack.stop();
			
            ss.setReuseAddress(true);
		}catch(Exception e){}
	}
}
class ServerRequestHandler extends Thread implements Runnable
{
	private ServerData data;
	private Socket s;
	public ServerRequestHandler(Socket s,ServerData data)
	{
		this.data=data;
		this.s=s;
	}
	public static boolean isPortAvailable(int port) 
	{
		ServerSocket ss = null;
		try 
		{
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        return true;
    		} catch (Exception e) {}
		finally
	    {
	        if (ss != null) 
	        {
	            try {
	                ss.close();
	            } catch (Exception e) {}
	        }
	    }
    return false;
	}
	@Override
	public void run()
	{
		try
		{
			BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out=new PrintWriter(s.getOutputStream());
            
            String CMD=in.readLine();
            if(CMD.equals("AddMe"))
        			data.addClient(s.getInetAddress().getHostAddress());
            else if(CMD.equals("LISTEN"))
            {
            		if(data.isFree2Listen)
            		{
            			synchronized (data)
            			{
							if(data.isFree2Listen)
							{
								int port=1291;
								for(port=1291;port<65535 && isPortAvailable(port)==false;port++);
								
								out.println("YES "+port);
								data.isFree2Listen=false;
								//TODO LISTEN & pass it to everyone
								new Thread(new Runnable() {
									@Override
									public void run() {
										try{
											
										// TODO Auto-generated method stub
											
										}catch(Exception e){}
									}
								}).start();
							}else
								out.println("NO");
					}
            		}else
            			out.println("NO");
            }
            else if(CMD.indexOf("foursquare?access_token=")>-1)
            {
            		String access_token=CMD.substring(CMD.indexOf("foursquare?access_token=")+24);
            		access_token+="\n";
            		while((CMD=in.readLine())!=null); //ignore the rest of input
            		FileOutputStream file=new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/access_tokens.txt");
    				
            		file.write(access_token.getBytes());
            		file.close();
            		//TODO give the web browser a better response
            		String msg="<html><head><title>Done :)</title></head><body>Now,you can close the browser<script>window.close();</script></body></html>";
            		out.println("HTTP/1.0 302 Found \r\nContent-Type: text/html; charset=UTF-8 \r\n Content-Length: "+msg.getBytes().length+"\r\n\r\n"+msg);
            		out.close();
            }
            s.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
}
public class Server extends Thread
{
	public ServerData data;
	public Server(ServerData data)
	{
		this.data=data;
	}
	@Override
	public void run()
	{
		try {
            ServerSocket ss = new ServerSocket(1290);
            while(true)
            {
            		Socket s = ss.accept();
            		new ServerRequestHandler(s,data).start();//new Thread to deal with this request
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
