 /*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package hdx.msr;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.util.Log;
import android.util.TimeUtils;

import java.io.BufferedReader;

import android.os.Handler;
import android.os.Messenger;
import java.lang.*;
import java.util.concurrent.locks.*;


public class MagneticStripeReader {

	public static  final int ON_READ_DATA = 1;
	private static final String TAG = "MagneticStripeReader";
	private ReadThread mReadThread;    
	Handler mHandler;
	boolean ready;
	private FileDescriptor mFd=null;
	private FileInputStream mInputStream=null;

	
	public MagneticStripeReader(Handler handler)  {
		mHandler = handler; 
		ready = false;
	}
	public void Close()
	{
		if(mReadThread!=null)
			mReadThread.interrupt();
	    close();
	}
	
    public int StartReading()
    {
    	if(mReadThread!=null)
    	{
    		if(mReadThread.isAlive())
    		{
    			mReadThread.interrupt();
    			try {
					mReadThread.join(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    	readyforread();
    	mReadThread = new ReadThread();
		mReadThread.start();		
        return 0;
    }
    
    public int StopReading()
    {
    	if(mReadThread!=null)
    	{
    		if(mReadThread.isAlive())
    		{
    			mReadThread.interrupt();
    			try {
					mReadThread.join(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    	return 0;
    }

	public void Open() throws IOException
	{
		mFd = open();
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		mInputStream = new FileInputStream(mFd);
	}

	private class ReadThread extends Thread {
		public void run() {
			int size=0;
			super.run();
			while(!isInterrupted()) {

				if (mInputStream == null) return;
			
				byte[] buffer = new byte[512];
				try {
					size = mInputStream.read(buffer);
					if(size<=0)
					{
						try{
							sleep(1000,0);
						}catch (InterruptedException e1) {
							return;
						}
					}
					else
					{	
						mHandler.sendMessage(mHandler.obtainMessage(ON_READ_DATA, size, 0,buffer));
						return ;
					}
				}catch (IOException e) {
					e.printStackTrace();
					Log.d(TAG,"read exception");
				}
			}
		}
	
	}	
	
	
	// JNI 
	private native static FileDescriptor open();
	private native  int readyforread();
	private native void close();
	static {
		System.loadLibrary("msr");
	}
}
