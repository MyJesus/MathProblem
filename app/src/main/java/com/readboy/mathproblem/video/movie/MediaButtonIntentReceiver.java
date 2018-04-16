/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.readboy.mathproblem.video.movie;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.util.Log;
import android.view.KeyEvent;


public class MediaButtonIntentReceiver extends BroadcastReceiver {
	public static final String ACTION_PAUSE = "com.readboy.mathproblem.pause";
	public static final String ACTION_START = "com.readboy.mathproblem.start";
	public static final String ACTION_START_PAUSE = "com.readboy.mathproblem.start.pause";
	
	private long mTime = 0;
	
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        Intent sendintent = new Intent();
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intentAction)) {
			sendintent.setAction(ACTION_PAUSE);
			context.sendBroadcast(sendintent);
			
			Log.e("", "intentAction: " + intentAction);        
        } else if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
        	
        	Log.e("", "intentAction: " + intentAction);   
        	KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);	
            if (event == null) {
                return;
            }

            int keycode = event.getKeyCode();
            int action = event.getAction();
            long eventtime = event.getEventTime();
            
            if (action != KeyEvent.ACTION_DOWN || eventtime-mTime<1000) {
            	return;
            }
            mTime = eventtime;
            // single quick press: pause/resume. 
            // double press: next track
            // long press: start auto-shuffle mode.

            String command = null;
            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                	sendintent.setAction(ACTION_START_PAUSE);
                	context.sendBroadcast(sendintent);
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    break;
            }

            	
            Log.e("", " actioncode: " + action+", keycode: "+keycode+", eventtime: "+eventtime);
        }
   }
    
    public static RemoteControlClient registerMediaButton(Context context) {
	   AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	   ComponentName rec = new ComponentName(context.getPackageName(), MediaButtonIntentReceiver.class.getName());
	   
	   mAudioManager.registerMediaButtonEventReceiver(rec);

	   Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
	   i.setComponent(rec);
	   PendingIntent pi = PendingIntent.getBroadcast(context, 0 /*requestCode, ignored*/, i /*intent*/, 0 /*flags*/);
	   RemoteControlClient mRemoteControlClient = new RemoteControlClient(pi);
	   
	   mAudioManager.registerRemoteControlClient(mRemoteControlClient);

	   int flags = RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
              | RemoteControlClient.FLAG_KEY_MEDIA_NEXT
              | RemoteControlClient.FLAG_KEY_MEDIA_PLAY
              | RemoteControlClient.FLAG_KEY_MEDIA_POSITION_UPDATE
              | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
              | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE
              | RemoteControlClient.FLAG_KEY_MEDIA_STOP;
	   mRemoteControlClient.setTransportControlFlags(flags);
	   return mRemoteControlClient;
   }
    
    public static void unregisterMediaButton(Context context, RemoteControlClient remoteControlClien) {
    	AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    	mAudioManager.unregisterRemoteControlClient(remoteControlClien);
    }
}
