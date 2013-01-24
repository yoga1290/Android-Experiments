package yoga1290.schoolmate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import yoga1290.schoolmate.R;

public class view4sqr extends Fragment implements OnClickListener,URLThread_CallBack, OnAudioFocusChangeListener
{
	private static final int RECORDER_BPP = 16;
//    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
//    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
//    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    
    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;
	
	
	View v;
	AudioManager am;
	URLThread connect=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        v=inflater.inflate(R.layout.view4sqr, container, false);
        
        Button checkin= (Button)v.findViewById(R.id.checkin);
        checkin.setOnClickListener(this);
        
        am = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
        int result	=	am.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
        
        return v;
    }
    
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.checkin: //matching some button id
				if(connect==null)
				{					
					connect=new URLThread("http://yoga1290.appspot.com/index.html", this, "");
					connect.start();
				}
//				Intent intent = new Intent(this.getActivity().getApplicationContext(), ConnectActivity.class);
//                startActivity(intent);
		}
	}

	@Override
	public void URLCallBack(String resp)
	{
		final String res=resp;
		this.getActivity().runOnUiThread(
				new Runnable()
				{
			            public void run()
			            {
				            	TextView tv=(TextView) v.findViewById(R.id.textView1);
				        		tv.setText(res);
			            }
				}
		);
	}

	@Override
	public void onAudioFocusChange(int arg0) {
		TextView tv=(TextView) v.findViewById(R.id.textView1);
		tv.setText("AUDIO FOCUS CHANGED");
		System.out.println("AUDIO FOCUS CHANGED!");
//		am.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
		// TODO Auto-generated method stub
		
	}
    
}