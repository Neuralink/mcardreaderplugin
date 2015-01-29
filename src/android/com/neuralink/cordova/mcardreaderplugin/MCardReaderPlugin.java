package com.neuralink.cordova.mcardreaderplugin;

import hdx.msr.MagneticStripeReader;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.os.Handler;
import android.os.Messenger;
import android.os.Message;

public class MCardReaderPlugin extends CordovaPlugin {
    private CallbackContext callback = null;
	private MagneticStripeReader msr;
	
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		msr = new MagneticStripeReader(new MyHandler());
	
		try {
			msr.Open();
		} catch ( IOException e ) {
		} 
		
		msr.StartReading();			
		callback = callbackContext;
		return true;
    }    

	
    private class MyHandler extends Handler {
    	void ParseOneTrack(int startPos,byte[] data)
    	{
    		int len;
    		
    		len = data[startPos];
    		if(len == 0)
    		{
    		    //editText.setText("Error");
				//callbackContext.error("Card Reading...");
    		}
    		else
    		{
    			String tmp="";

    			try {
					tmp = new String(data,startPos+1,len,"GBK");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
    			//editText.setText(tmp);
				callback.success(tmp);

    		}
    	}
    	void ParseData(int size,byte[] data)
    	{

    		int pos;
    		ParseOneTrack(0,data);
    		pos = data[0]+1;
    		ParseOneTrack(pos,data);
    		pos += data[pos]+1;
    		ParseOneTrack(pos,data);

			//callback.success(data);
		
			msr.Close();

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
