package yoga1290.schoolmate;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class view4sqr extends Fragment implements OnClickListener,URLThread_CallBack
{
	View v;
	URLThread connect=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) 
    {
        v=inflater.inflate(R.layout.view4sqr, container, false);
        
        Button checkin= (Button)v.findViewById(R.id.checkin);
        checkin.setOnClickListener(this);
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
    
}