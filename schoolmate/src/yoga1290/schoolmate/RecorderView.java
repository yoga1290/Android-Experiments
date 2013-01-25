package yoga1290.schoolmate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class RecorderView extends Fragment implements OnAudioFocusChangeListener,android.view.View.OnClickListener
{
	
	//AudioRecord code from http://code.google.com/p/krvarma-android-samples/source/browse/trunk/AudioRecorder.2/src/com/varma/samples/audiorecorder/RecorderActivity.java
	
	private static final int RECORDER_BPP = 16;
  private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
  private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
  private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
  private static final int RECORDER_SAMPLERATE = 44100;
  private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
  private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
  private AudioRecord recorder = null;
  private int bufferSize = 0;
  private Thread recordingThread = null;
  private boolean isRecording = false;
	
	
	View v;
	AudioManager am;
	Button button_ok,stopbutton;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        v=inflater.inflate(R.layout.view_record, container, false);
        
        am = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
        int result	=	am.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        
        button_ok=(Button) v.findViewById(R.id.audioPeerOk);
        button_ok.setOnClickListener(this);
        
        stopbutton=(Button) v.findViewById(R.id.stopbutton);
        stopbutton.setOnClickListener(this);
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
        
        return v;
    }

	@Override
	public void onAudioFocusChange(int arg0)
	{
		//TODO
//		if(isRecording)
//			startRecording();
//		else
//			stopRecording();
		
	}
	
	private void debug(String txt)
	{
		try
		{
			EditText et=(EditText) v.findViewById(R.id.audioPeer);
			et.setText(txt);
		}catch(Exception e){}
	}
	
	
	
	
	
	private void startRecording(){
		try{
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                        RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);
        
        recorder.startRecording();
        
        isRecording = true;
        
        recordingThread = new Thread(new Runnable() {
                
                @Override
                public void run() {
                			debug("Start thread");
                        writeAudioDataToFile();
                }
        },"AudioRecorder Thread");
        
        recordingThread.start();
		}catch(Exception e){debug("startRecording()>"+e);}
}

private void writeAudioDataToFile(){
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;
        
        try {
                os = new FileOutputStream(filename);
        } catch (Exception e) {
        			debug(1+">"+e);
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        int read = 0;
        if(null != os){
                while(isRecording){
                        read = recorder.read(data, 0, bufferSize);
                        
                        if(AudioRecord.ERROR_INVALID_OPERATION != read){
                                try {
                                        os.write(data);
                                } catch (Exception e) {
                                	debug(2+">"+e);
                                        e.printStackTrace();
                                }
                        }
                }                
                try {
                        os.close();
                } catch (Exception e) {
                	debug(3+">"+e);
                        e.printStackTrace();
                }
        }
}
private void stopRecording()
{
//	AudioTrack at=new AudioTrack(streamType, RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize, mode);
//			(MediaRecorder.AudioSource.MIC,
//            RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);
    if(null != recorder)
    {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
    	}
    
//    copyWaveFile(getTempFilename(),getFilename());
//    private void copyWaveFile(String inFilename,String outFilename){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;
        
        byte[] data = new byte[bufferSize];
        
        try {
                in = new FileInputStream(getTempFilename());
                out = new FileOutputStream(getFilename());
                totalAudioLen = in.getChannel().size();
                totalDataLen = totalAudioLen + 36;
                
                WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                                longSampleRate, channels, byteRate);
                
                while(in.read(data) != -1){
                        out.write(data);
                }
                
                in.close();
                out.close();
        } catch (Exception e) {
        	debug(4+">"+e);
                e.printStackTrace();
        }
    
    
    File file = new File(getTempFilename());
    file.delete();
}

private void WriteWaveFileHeader(
        FileOutputStream out, long totalAudioLen,
        long totalDataLen, long longSampleRate, int channels,
        long byteRate) throws Exception
        {
			byte[] header = new byte[44];	
			header[0] = 'R';  // RIFF/WAVE header
			header[1] = 'I';
			header[2] = 'F';
			header[3] = 'F';
			header[4] = (byte) (totalDataLen & 0xff);
			header[5] = (byte) ((totalDataLen >> 8) & 0xff);
			header[6] = (byte) ((totalDataLen >> 16) & 0xff);
			header[7] = (byte) ((totalDataLen >> 24) & 0xff);
			header[8] = 'W';
			header[9] = 'A';
			header[10] = 'V';
			header[11] = 'E';
			header[12] = 'f';  // 'fmt ' chunk
			header[13] = 'm';
			header[14] = 't';
			header[15] = ' ';
			header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
			header[17] = 0;
			header[18] = 0;
			header[19] = 0;
			header[20] = 1;  // format = 1
			header[21] = 0;
			header[22] = (byte) channels;
			header[23] = 0;
			header[24] = (byte) (longSampleRate & 0xff);
			header[25] = (byte) ((longSampleRate >> 8) & 0xff);
			header[26] = (byte) ((longSampleRate >> 16) & 0xff);
			header[27] = (byte) ((longSampleRate >> 24) & 0xff);
			header[28] = (byte) (byteRate & 0xff);
			header[29] = (byte) ((byteRate >> 8) & 0xff);
			header[30] = (byte) ((byteRate >> 16) & 0xff);
			header[31] = (byte) ((byteRate >> 24) & 0xff);
			header[32] = (byte) (2 * 16 / 8);  // block align
			header[33] = 0;
			header[34] = RECORDER_BPP;  // bits per sample
			header[35] = 0;
			header[36] = 'd';
			header[37] = 'a';
			header[38] = 't';
			header[39] = 'a';
			header[40] = (byte) (totalAudioLen & 0xff);
			header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
			header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
			header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
			
			out.write(header, 0, 44);
}

private String getFilename(){
    String filepath = Environment.getExternalStorageDirectory().getPath();
    File file = new File(filepath,AUDIO_RECORDER_FOLDER);
    if(!file.exists())	file.mkdirs();
   return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
}
private String getTempFilename(){
    String filepath = Environment.getExternalStorageDirectory().getPath();
    File file = new File(filepath,AUDIO_RECORDER_FOLDER);
    if(!file.exists())	file.mkdirs();
    File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);    
    if(tempFile.exists())	tempFile.delete();
    return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
}

@Override
public void onClick(View v) {
	if(v.getId()==button_ok.getId())
	{
		if(recordingThread==null)
			startRecording();
//		else
//			stopRecording();
	}
	else
		stopRecording();
}


}
