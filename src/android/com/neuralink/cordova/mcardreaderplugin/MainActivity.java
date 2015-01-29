package com.example.magneticstripe;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import hdx.msr.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.example.magneticstripe.R;
import android.os.Handler;
import android.os.Messenger;
import android.os.Message;

public class MainActivity extends Activity {
    EditText editText1;
    EditText editText2;
    EditText editText3;

	private MagneticStripeReader msr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		msr = new MagneticStripeReader(new MyHandler());
		try
		{
			msr.Open();
		} catch ( IOException e ) {
		} 
		
		editText1=(EditText)findViewById(R.id.editText_track1);  
		editText2=(EditText)findViewById(R.id.editText_track2);  
		editText3=(EditText)findViewById(R.id.editText_track3);  
		
		final Button click = (Button)findViewById(R.id.button_open);
		click.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setTitle(getText(R.string.please)); 
				msr.StartReading();
				editText1.setText("");
				editText2.setText("");
				editText3.setText("");
			}
		});	
        final Button quit = (Button)findViewById(R.id.button_quit);
        quit.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				MainActivity.this.finish();
			}
		});			
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void onDestroy() {
		super.onDestroy();
		msr.Close();
	}
	
    private class MyHandler extends Handler {
    	void ParseOneTrack(int startPos,byte[] data,EditText editText)
    	{
    		int len;
    		
    		len = data[startPos];
    		if(len == 0)
    		{
    		    editText.setText("Error");
    		}
    		else
    		{
    			String tmp="";

    			try {
					tmp = new String(data,startPos+1,len,"GBK");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
    			editText.setText(tmp);
    		}
    	}
    	void ParseData(int size,byte[] data)
    	{
    		int pos;

    		ParseOneTrack(0,data,editText1);
    		pos = data[0]+1;
    		ParseOneTrack(pos,data,editText2);
    		pos += data[pos]+1;
    		ParseOneTrack(pos,data,editText3);
    		setTitle(getText(R.string.app_name));
       	}
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MagneticStripeReader.ON_READ_DATA:
            	ParseData(msg.arg1,(byte [])msg.obj);
            	break;
               default:
                  	break;
            }
        }		
	}
}
