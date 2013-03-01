package yoga1290.schoolmate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
class AudioProperties
{
	public static final int 	sampleRateInHz=44100,//44100,
							channelConfigIN=AudioFormat.CHANNEL_IN_STEREO,
							channelConfigOUT=AudioFormat.CHANNEL_OUT_STEREO,
							audioFormat=AudioFormat.ENCODING_PCM_16BIT;
	public static final int bufferSizeIN = AudioRecord.getMinBufferSize(AudioProperties.sampleRateInHz,AudioProperties.channelConfigIN,AudioProperties.audioFormat);
	public static final int bufferSizeOUT = AudioRecord.getMinBufferSize(AudioProperties.sampleRateInHz,AudioProperties.channelConfigOUT,AudioProperties.audioFormat);
}
class ServerData
{
	public static LinkedList<String> followers=new LinkedList<String>();
	public static String following="";
	public boolean isFree2Listen=true;
	public byte buff[];
	public static int lastUsedPort=1291;
	public static void addClient(String IP)
	{
		followers.add(IP);
	}
	public static void send2Followers(final byte data[],final int offset)
	{
		final Iterator<String> it=followers.iterator();
		while(it.hasNext())
		{
			final String follower=it.next();
			new Thread(new Runnable() {
				
				@Override
				public void run() 
				{
					try
					{
						int p=0,o;
						byte ip[]=new byte[4];
						String cur=follower;
						while(p<4 && (o=cur.indexOf("."))>-1)
						{
							ip[p++]=(byte) Integer.parseInt(cur.substring(0,o));
							cur=cur.substring(o+1);
						}
						ip[3]=(byte) Integer.parseInt(cur);
						System.out.println("Connecting to "+follower);
						
						
						Socket s=new Socket(InetAddress.getByAddress(ip) , ServerProperties.port);
					
						PrintWriter out=new PrintWriter(s.getOutputStream());
			            out.println("LISTEN");
			            out.flush();
//			            out.close();
			            System.out.println("waiting for response b4 data...");
			            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
			            String resp=in.readLine();
			            //TODO FIX; connection timeouts here but why?!!
			            
			            System.out.println(s.getInetAddress()+" response: "+resp);
			            out.close();
			            in.close();
			            s.close();
						
						Socket s2=new Socket(InetAddress.getByAddress(ip) , 
										Integer.parseInt(resp.split(" ")[1])	);
						s2.getOutputStream().write(data,0,offset);
						s2.close();
					}catch(Exception e){e.printStackTrace();}
					
				}
			}).start();
		}
	}
}

class DataTransferThread extends Thread implements Runnable
{
	private int port=1291;
	public DataTransferThread(int port)//,ServerData data)
	{
		this.port=port;
//		this.data=data;//to get followers
	}
	@Override
	public void run()
	{
		try
		{
			ServerSocket ss = new ServerSocket(port);
            System.out.println("Waiting at port#"+port);
            Socket s = ss.accept();
            System.out.println("LISTENing at port#"+port);
            InputStream in=s.getInputStream();
//            int bufferSize = AudioRecord.getMinBufferSize(AudioProperties.sampleRateInHz,AudioProperties.channelConfig,AudioProperties.audioFormat);
            byte buff[]=new byte[AudioProperties.bufferSizeOUT];
			// Create a new AudioTrack object using the same parameters as the AudioRecord
			// object used to create the file.
			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 
			AudioProperties.sampleRateInHz,//44100,//11025, 
			AudioProperties.channelConfigOUT,//AudioFormat.CHANNEL_IN_STEREO,//AudioFormat.CHANNEL_CONFIGURATION_MONO,
			AudioProperties.audioFormat,//AudioFormat.ENCODING_PCM_16BIT,//AudioFormat.ENCODING_PCM_16BIT, 
			AudioProperties.bufferSizeOUT,// 
			AudioTrack.MODE_STREAM);
			// Start playback
			audioTrack.play();
			int offset=0;
	
			while((offset=in.read(buff))>0)
			{
				// Write the music buffer to the AudioTrack object
				audioTrack.write(buff, 0, offset);
				//Pass what you hear to your followers
				ServerData.send2Followers(buff,offset);
			}
			audioTrack.stop();
			
            ss.setReuseAddress(true);
		}catch(Exception e){e.printStackTrace();}
	}
}
class ServerRequestHandler extends Thread implements Runnable
{
	private Socket s;
	public ServerRequestHandler(Socket s)
	{
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
			System.out.println("Command from "+s.getInetAddress()+">");
			BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
            String CMD=in.readLine();
//            while(CMD==null)
//            		CMD=in.readLine();
            System.out.println((CMD==null ? "NULL?!!":CMD)+"<");
//            if(CMD==null)
//            {
//            		System.out.println("NO CMD was sent");
//            		s.close();
//            		return;
//            }
            PrintWriter out=new PrintWriter(s.getOutputStream());
            
            
            if(CMD.equals("LISTEN") || CMD.equals("hear me"))
            {
            		
            		System.out.println("LISTEN request...");
//				for(ServerData.lastUsedPort++; isPortAvailable(ServerData.lastUsedPort)==false;ServerData.lastUsedPort++);//port<65535 &&
            		int port=1291;
            		for(;!isPortAvailable(port);port++);
            		
				out.println("YES "+port);
				out.flush();
				System.out.println("New LISTEN port at "+port);
				//TODO LISTEN & pass it to everyone
				new DataTransferThread(port).start();								
								
            }
            else if(CMD.indexOf("foursquare?access_token=")>-1)
            {
            		String access_token=CMD.substring(CMD.indexOf("foursquare?access_token=")+24);
            		if(access_token.indexOf(" ")!=-1)
            			access_token=access_token.substring(0,access_token.indexOf(" "));
            		while((CMD=in.readLine())!=null); //ignore the rest of input
            		FileOutputStream file=new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/foursquareAccessToken.txt");
    				
            		file.write(access_token.getBytes());
            		file.close();
            		//TODO give the web browser a better response
            		OutputStream out2=s.getOutputStream();
            		String msg="<html><head><title>Done :)</title></head><body>Now,you can close the browser<script>window.close();</script></body></html>";
            		out2.write(("HTTP/1.1 200 OK\nAllow: GET\nServer: yoga1290\nContent-Type: text/html; charset=UTF-8\n Content-Length: "+msg.getBytes().length+"\n\n"+msg+"\r\n\r\n\n\n").getBytes());
            		out2.close();
            }
            else if(CMD.equals("AddMe"))
            {
            		System.out.println("Adding new follower "+s.getInetAddress().getHostAddress());
        			ServerData.addClient(s.getInetAddress().getHostAddress());
            }
            
            in.close();
            out.close();
            s.close();
            System.out.println("closing socket");
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
            		new ServerRequestHandler(s).start();//new Thread to deal with this request
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
