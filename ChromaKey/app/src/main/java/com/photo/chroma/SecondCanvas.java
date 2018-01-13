package com.photo.chroma;



import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class SecondCanvas extends SurfaceView implements SurfaceHolder.Callback {
    class GameThread extends Thread {

    	// The states
    	public static final int STATE_PAUSE = 2;
        public static final int STATE_RUNNING = 4;
        public static final int STATE_LOADING = 5;
        
        
        private Paint paint = new Paint();
        public int page = 0;
        
        private int mCanvasWidth;
        private int loading = 0;
        private int mCanvasHeight;
        public int chosenback = 0;

        private Background chosenbackground;
        private Chromkey photo;
        private Sprite arrowLeft, arrowRight, next;
        private int error = 30;


        
        private long mLastTime;
        

         /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
        private int mMode;
        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRun = false;
        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;

        public GameThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mContext = context;
        	
            
        }
        
        
        public void createGFX(Canvas canvas) {
        	
        	
        	
        	if (mCanvasWidth > 0 && mCanvasHeight > 0) {
        		
            	canvas.drawARGB(255, 0, 0, 0);
            	paint.setColor(Color.WHITE);
         
            	canvas.drawText("Loading Images, please wait...", mCanvasWidth/2-60, mCanvasHeight/2-30, paint);
            	canvas.drawRect(mCanvasWidth/2-25, mCanvasHeight/2-10, mCanvasWidth/2-25+(loading*2), mCanvasHeight/2+10, paint);

        		
if (loading == 5)
        		createBackground(mCanvasWidth,mCanvasHeight);
else if (loading == 6)
        	arrowRight = new Sprite(R.drawable.plus, mContext, 0, 0,mCanvasWidth/10,mCanvasWidth/10);
else if (loading == 7) {
arrowLeft = new Sprite(R.drawable.minus, mContext, 0, 0,mCanvasWidth/10,mCanvasWidth/20);
next = new Sprite(R.drawable.tick, mContext, 0, 0,mCanvasWidth/10,mCanvasWidth/10);
}
else if (loading == 8) {
photo = new Chromkey("/sdcard/test.jpg",mCanvasWidth/2,mCanvasHeight/2,error);
	thread.photo.rotate(90);

        	photo.setPos(0,0);
}
        	else if (loading == 9)
        	mMode = STATE_RUNNING;

loading++;
        	}
        }
        

        /**
         * Start canvas.
         */
        public void doStart() {
            synchronized (mSurfaceHolder) {
            	// Initialize game here!
            	
            	
                mLastTime = System.currentTimeMillis() + 100;
                setState(STATE_RUNNING);
            }
        }
        
        public void fixPhoto() {
        	photo.fix(error);
        }
        


        public void pause() {
            synchronized (mSurfaceHolder) {
                if (mMode == STATE_RUNNING) 
                	setState(STATE_PAUSE);
            }
        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (mMode == STATE_RUNNING) {
                        	updateGame();
                        render(c);
                        }
                        else if (mMode == STATE_LOADING){
                        	createGFX(c);
                        }
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         * 
         * @param b true to run, false to shut down
         */
        public void setRunning(boolean b) {
            mRun = b;
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         * 
         * @see #setState(int, CharSequence)
         * @param mode one of the STATE_* constants
         */
        public void setState(int mode) {
            synchronized (mSurfaceHolder) {
                setState(mode, null);
            }
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         * 
         * @param mode one of the STATE_* constants
         * @param message string to add to screen or null
         */
        public void setState(int mode, CharSequence message) {
            synchronized (mSurfaceHolder) {
                mMode = mode;
            }
        }

        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;
            }
        }

        /**
         * Resumes from a pause.
         */
        public void unpause() {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder) {
                mLastTime = System.currentTimeMillis() + 100;
            }
            setState(STATE_RUNNING);
        }

        /**
         * Handles a key-down event.
         * 
         * @param keyCode the key that was pressed
         * @param msg the original event object
         * @return true
         */
        boolean doKeyDown(int keyCode, KeyEvent msg) {
        	boolean handled = false;
            synchronized (mSurfaceHolder) {
                return handled;
            }
        }

        
        public void setPos(float x, float y) {
        	
        }
        
        /**
         * Handles a key-up event.
         * 
         * @param keyCode the key that was pressed
         * @param msg the original event object
         * @return true if the key was handled and consumed, or else false
         */
        boolean doKeyUp(int keyCode, KeyEvent msg) {
        	boolean handled = false;
            synchronized (mSurfaceHolder) {
                return handled;
            }
        }

        /**
         * Render the graphics on screen
         * 
         */
        private void render(Canvas canvas) {
        	// empty canvas
        	if (page == 10) {
        		try {
        		Bitmap finalimage = Bitmap.createBitmap(mCanvasWidth, mCanvasHeight, Bitmap.Config.ARGB_4444);
        		canvas.setBitmap(finalimage);
            	canvas.drawARGB(255, 0, 0, 0);
        		chosenbackground.draw(canvas);
        		photo.draw(canvas);

        		FileOutputStream out = new FileOutputStream("/sdcard/final.jpg");
		       finalimage.compress(Bitmap.CompressFormat.JPEG, 90, out);

		       out.flush();
		       out.close();
        		}
        		catch (IOException e) {
        			Log.w("ERROR", "Could not create final.jpg!");
        		}
        		page++;

        	}
        	else if (page >= 1 && page < 10) {
        		chosenbackground.draw(canvas);
        		photo.draw(canvas);
        		page++;
        	}        	
        	else if (page == 0) {
        	canvas.drawARGB(255, 0, 0, 0);
        	if (photo != null) {
        	if (photo.processing == true) {
            	paint.setTextAlign(Align.CENTER);
            	canvas.drawText("LOADING, PLEASE WAIT...", mCanvasWidth/2-30, mCanvasHeight/2, paint);
        	}
        	else {
        			chosenbackground.draw(canvas);
        		

            	photo.draw(canvas);
            	arrowLeft.setPos(0, mCanvasHeight/2-arrowLeft.getHeight()/2);
            	arrowRight.setPos(mCanvasWidth-arrowRight.getWidth(), mCanvasHeight/2-arrowRight.getHeight()/2);
            	next.setPos(mCanvasWidth/2-arrowRight.getWidth()/2, 0);

            	arrowLeft.draw(canvas);
            	arrowRight.draw(canvas);
            	next.draw(canvas);
        	}

        	}
        	
        	}
        }

        
        private void createBackground(int resX, int resY) {
			if (chosenback == 1)
	        	chosenbackground = new Background(R.drawable.choice1, mContext,resX,resY);
			else if (chosenback == 2)
	        	chosenbackground = new Background(R.drawable.choice2, mContext,resX,resY);
			else if (chosenback == 3)
	        	chosenbackground = new Background(R.drawable.choice3, mContext,resX,resY);
			else if (chosenback == 4)
	        	chosenbackground = new Background(R.drawable.choice4, mContext,resX,resY);
			else if (chosenback == 5)
	        	chosenbackground = new Background(R.drawable.choice5, mContext,resX,resY);
			else if (chosenback == 6)
	        	chosenbackground = new Background(R.drawable.choice6, mContext,resX,resY);
			else if (chosenback == 7)
	        	chosenbackground = new Background(R.drawable.choice7, mContext,resX,resY);
			else if (chosenback == 8)
	        	chosenbackground = new Background(R.drawable.choice8, mContext,resX,resY);
			else if (chosenback == 9)
	        	chosenbackground = new Background(R.drawable.choice9, mContext,resX,resY);

				
        }
        
        /**
         * Updates the game.
         */
        private void updateGame() {
            long now = System.currentTimeMillis();
            if (mLastTime > now) 
            	return;
            double elapsed = (now - mLastTime) / 1000.0;
            mLastTime = now;
        }
    }

    /** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;

    /** The thread that actually draws the animation */
    private GameThread thread;

    public SecondCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new GameThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            }
        });

        setFocusable(true); // make sure we get key events
    }

    /**
     * Fetches the animation thread corresponding to this LunarView.
     * 
     * @return the animation thread
     */
    public GameThread getThread() {
        return thread;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) {
    	case MotionEvent.ACTION_DOWN:{
    		if (thread.arrowRight.checkCollision(event.getX(), event.getY()) == true) {
      			thread.error += 10;
      			thread.fixPhoto();
      		}
      		else if (thread.arrowLeft.checkCollision(event.getX(), event.getY()) == true) {
      			thread.error -= 10;
      			thread.fixPhoto();
      		}
      		else if (thread.next.checkCollision(event.getX(), event.getY()) == true) {
      			thread.page = 1;
      		}
    		
    		
    		break;
    	}
    	
    	case MotionEvent.ACTION_MOVE:{
    		if (thread.photo != null) {

    		if (thread.arrowRight.checkCollision(event.getX(), event.getY()) == false && thread.arrowLeft.checkCollision(event.getX(), event.getY()) == false && thread.next.checkCollision(event.getX(), event.getY()) == false) {

    			thread.photo.setPos(event.getX()-thread.photo.getWidth()/2, event.getY()-thread.photo.getHeight()/2);	
    		}
    	}
    		break;
    	}
    	
    	}
    	return true;
    	
    	
    }

    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        return thread.doKeyDown(keyCode, msg);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        return thread.doKeyUp(keyCode, msg);
    }

    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus)
        	thread.pause();
    }

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        thread.setSurfaceSize(width, height);
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        if (thread.mMode == thread.STATE_LOADING)
        thread.start();
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}