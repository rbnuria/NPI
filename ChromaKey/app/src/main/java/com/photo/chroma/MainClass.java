package com.photo.chroma;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import com.photo.chroma.SecondCanvas.GameThread;
import com.photo.chroma.FirstCanvas.FirstThread;


public class MainClass extends Activity {
    private static final int MENU_SEND = Menu.FIRST;
    private static final int MENU_RESTART = Menu.FIRST+1;

    private Preview preview;
	private ImageButton buttonClick;
	
	private static final String TAG = "CameraDemo";

    /** A handle to the thread that's actually running the animation. */
    private GameThread mGameThread; // HERE

    /** A handle to the thread that's actually running the animation. */
    private FirstThread fGameThread;

    /** A handle to the View in which the game is running. */
  private SecondCanvas mGameView; // HERE
    
    /** A handle to the first canvas */
    private FirstCanvas fGameView;
    private Menu myMenu;
    
    private int chosenback = 1;
    
    
private Handler h = new Handler() {
@Override
public void handleMessage(Message msg) {
switch(msg.what) {
case 1:
	chosenback = fGameThread.chosenback;
	startCamera();
	break;
}
   			
   		}
    	   
       };

    
    

    /**
     * Invoked during init to give the Activity a chance to set up its Menu.
     * 
     * @param menu the Menu to which entries may be added
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
myMenu = menu;
        
//        menu.add(0, MENU_START, 0, R.string.menu_start);
      myMenu.add(0, MENU_SEND, 0, R.string.menu_send);
      myMenu.add(0, MENU_RESTART, 0, R.string.menu_restart);

        //        menu.add(0, MENU_SEND, 0, R.string.menu_send);

        return true;
    }

    /**
     * Invoked when the user selects an item from the Menu.
     * 
     * @param item the Menu entry which was selected
     * @return true if the Menu item was legit (and we consumed it), false
     *         otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SEND:
            	if (mGameThread != null) {
            		if (mGameThread.page == 11)
                sendPic();
            	}
            	
                return true;
            case MENU_RESTART:
                restart();
        }

        return false;
    }

    /**
     * Invoked when the Activity is created.
     * 
     * @param savedInstanceState a Bundle containing state saved from a previous
     *        execution, or null if this is a new execution
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // turn off the window's title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.first);
		fGameView = (FirstCanvas) findViewById(R.id.first);
		int height = getWindowManager().getDefaultDisplay().getHeight();
		int width = getWindowManager().getDefaultDisplay().getWidth();
		
		
        fGameThread = fGameView.getThread();
        fGameThread.setSurfaceSize(width, height);

       fGameThread.setState(FirstThread.STATE_RUNNING);
        
        
        
        // Thread that checks if FirstCanvas is done, and ready to move on to Camera.
       
       Thread t = new Thread(new Runnable() {
		boolean changescreen = false;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
			System.gc();
			}
			catch (Exception e) {
				
			}
			
			
			while (changescreen == false) {
				if (fGameThread.nextPage == true) {
					changescreen = true;
					h.sendEmptyMessage(1);
				}
				
			}
			
			
		}
	});
       t.start();
        
        Log.d(TAG, "onCreate'd");

    }

    
    
    /**
     * Start the camera. Uses the preview class
     * 
     */
    public void startCamera() {
    	setContentView(R.layout.camview);
    	fGameView = null;
    	fGameThread.stop();
    	fGameThread=null;


        preview = new Preview(this);
		
	((FrameLayout) findViewById(R.id.preview)).addView(preview);
	buttonClick = (ImageButton) findViewById(R.id.buttonClick);
	buttonClick.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {

			if (buttonClick.isSelected() == true) {
		        
				
				setContentView(R.layout.main);
				mGameView = (SecondCanvas) findViewById(R.id.game);


		        mGameThread = mGameView.getThread();
				int height = getWindowManager().getDefaultDisplay().getHeight();
				int width = getWindowManager().getDefaultDisplay().getWidth();
				
				
		        mGameThread.setSurfaceSize(width, height);

		        mGameThread.chosenback = chosenback; 

		        // set up a new game
		       mGameThread.setState(GameThread.STATE_LOADING);
		       
			}
			else {
			preview.camera.takePicture(shutterCallback, rawCallback,
					jpegCallback);
			
			buttonClick.setSelected(true);
			buttonClick.bringToFront();


			}

			
		}
	});

    }
    
    
    /**
     * Send the final image created and saved on sdcard using a program from Intent chooser
     * 
     */
    public void sendPic() {
    	Intent picMessageIntent = new Intent(android.content.Intent.ACTION_SEND);
    	picMessageIntent.setType("image/jpeg"); 
    	String filepath = "/sdcard/";
    	File imagefile = new File(filepath + "final.jpg");
    	picMessageIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imagefile));
    	
//    	startActivity(picMessageIntent); // If you only want to send MMS 
    	startActivity(Intent.createChooser(picMessageIntent, "Send picture using:"));
    	
    
    	
    }

    
    
    
    
    ////////// CAMERA STUFF
    
    
    ShutterCallback shutterCallback = new ShutterCallback() {

		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {

			
			
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;

			try {
				// Write to sdcard
				outStream = new FileOutputStream(String.format(
						"/sdcard/test.jpg", System.currentTimeMillis()));
				outStream.write(data);
				outStream.close();

				
				/* THIS IS REQUIRED FOR SAMSUNG GALAXY TAB (and maybe other devices), since preview image dissapears...
				 * Therefore we add the image from the camera to an imagebutton				
				tempPhoto = BitmapFactory.decodeByteArray(data, 0, data.length);
				int height = getWindowManager().getDefaultDisplay().getHeight();
				int width = getWindowManager().getDefaultDisplay().getWidth();

				tempPhoto = Bitmap.createScaledBitmap(tempPhoto, width, height, true);
				
				buttonClick.setImageBitmap(tempPhoto);
*/
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);


				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {

			}

			
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};
    
    
    
    
    
    
    
    
    
    private void restart() { // Restart application
    	preview.camera.release();

    	try {
        	Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(i);
        	}
        	catch (Exception e) {
        		this.finish();
        	}

    }
    
    
    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onRestart() {
    	super.onRestart();
    	Log.d("Hello", "Restarted");
    	preview.camera.release();
    }    
    
    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onStop() {
        super.onStop();
		Log.d("Hello", "Stopped");
        mGameView.getThread().pause(); 

    }
}