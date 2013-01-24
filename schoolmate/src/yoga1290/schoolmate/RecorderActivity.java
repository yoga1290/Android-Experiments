package yoga1290.schoolmate;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RecorderActivity extends Fragment implements OnAudioFocusChangeListener
{
	
	private static final int RECORDER_BPP = 16;
//  private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
//  private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
//  private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
  private static final int RECORDER_SAMPLERATE = 44100;
  private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
  private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
  private AudioRecord recorder = null;
  private int bufferSize = 0;
  private Thread recordingThread = null;
  private boolean isRecording = false;
	
	
	View v;
	AudioManager am;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        v=inflater.inflate(R.layout.view4sqr, container, false);
        
        am = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
        int result	=	am.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        
        
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
        
        return v;
    }

	@Override
	public void onAudioFocusChange(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
