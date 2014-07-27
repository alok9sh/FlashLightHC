package com.alok9shm.hcflashlight;

import com.alok9shm.hcflashlight.R;


import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	ImageButton onOffButton;

	private Camera camera;
	private boolean isLedOn;
	private boolean LedAvailableOnDevice;
	Parameters params;
	MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// On/Off Switch
		onOffButton = (ImageButton) findViewById(R.id.btnSwitch);

		//Check for flashlight support on device
		LedAvailableOnDevice = getApplicationContext().getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		if (!LedAvailableOnDevice) {
			// device doesn't support flash, show error and exit
			
			AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
					.create();
			alert.setTitle("HackCommunity FlashLight");
			alert.setMessage("Unsupported!! Go get a better phone you dumbass!!");
			alert.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// exit app
					finish();
				}
			});
			alert.show();
			return;
		}

		// Getting Camera
		getCamera();
		
		// display switch
		toggleSwitchPng();
		
		//toggle switch on/off
		onOffButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isLedOn) {
					// if On, then Switch off
					ledOff();
				} else {
					// Else Switch on
					ledOn();
				}
			}
		});
	}

	//Get Camera
	private void getCamera() {
		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
			} catch (RuntimeException e) {
				Log.e("You got a fucked up camera mate: ", e.getMessage());
			}
		}
	}

	//turning light on
	private void ledOn() {
		if (!isLedOn) {
			if (camera == null || params == null) {
				return;
			}
			// play the swit
			playSound();
			
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			camera.startPreview();
			isLedOn = true;
			
			// changing button/switch image
			toggleSwitchPng();
		}

	}

	/*
	 * Turning Off flash
	 */
	private void ledOff() {
		if (isLedOn) {
			if (camera == null || params == null) {
				return;
			}
			// play sound
			playSound();
			
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
			isLedOn = false;
			
			// changing button/switch image
			toggleSwitchPng();
		}
	}
	
	/*
	 * Playing sound
	 * will play button toggle sound on flash on / off
	 * */
	private void playSound(){
		if(isLedOn){
			mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_off);
		}else{
			mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_on);
		}
		mp.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        }); 
		mp.start();
	}
	
	/*
	 * Toggle switch button images
	 * changing image states to on / off
	 * */
	private void toggleSwitchPng(){
		if(isLedOn){
			onOffButton.setImageResource(R.drawable.btn_switch_on);
		}else{
			onOffButton.setImageResource(R.drawable.btn_switch_off);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// on pause turn off the flash
		ledOff();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// on resume turn on the flash
		if(LedAvailableOnDevice)
			ledOn();
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		// on starting the app get the camera params
		getCamera();
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		// on stop release the camera
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

}
