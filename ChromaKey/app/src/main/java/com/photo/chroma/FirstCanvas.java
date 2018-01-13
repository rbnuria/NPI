package com.photo.chroma;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * View that draws, takes keystrokes, etc. for a simple LunarLander game.
 * 
 * Has a mode which RUNNING, PAUSED, etc. Has a x, y, dx, dy, ... capturing the
 * current ship physics. All x/y etc. are measured with (0,0) at the lower left.
 * updatePhysics() advances the physics based on realtime. draw() renders the
 * ship, and does an invalidate() to prompt another draw() as soon as possible
 * by the system.
 */
class FirstCanvas extends SurfaceView implements SurfaceHolder.Callback {
	
    class FirstThread extends Thread {
        /*
         * State-tracking constants
         */
        public static final int STATE_LOSE = 1;
        public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;
        public static final int STATE_RUNNING = 4;
        public static final int STATE_WIN = 5;
        
        public float x;
        public float y;
        public boolean unloadall = false;
        
        private Paint paint = new Paint();
        public int endload = 9;
        
        private int mCanvasWidth;
        private int mCanvasHeight;

        public int selectionscreen = 1;
        public boolean selected = false;
        public int loadinggfx = 0;
        public 	int[] images = new int[3];

        
        private long mLastTime;
        private Background background;
        private Sprite back1, back2, back3,arrowright,arrowleft,selectedback,tick,cross;
        
        

        
        public boolean loading = false;
        
        public int chosenback = 0;
        
        public boolean nextPage = false;
        
        
        
        
         /** Message handler used by thread to post stuff back to the GameView */

         /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
        private int mMode;
        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRun = false;
        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;

        public FirstThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {
            mSurfaceHolder = surfaceHolder;
            mContext = context;
        	
            x = 10;
            y = 10;
            
        }
        
        
        public void createGFX(Canvas canvas) {
        	canvas.drawARGB(255, 0, 0, 0);
        	paint.setColor(Color.WHITE);
     
        	canvas.drawText("Loading Images, please wait...", mCanvasWidth/2-60, mCanvasHeight/2-30, paint);
        	canvas.drawRect(mCanvasWidth/2-45, mCanvasHeight/2-10, mCanvasWidth/2-45+(loadinggfx*5), mCanvasHeight/2+10, paint);
        	
        	if (loadinggfx == 5) 
        	background = new Background(R.drawable.back1, mContext,mCanvasWidth, mCanvasHeight);
        	else if (loadinggfx == 1) { 
        	images[0] = R.drawable.choice1;
        	images[1] = R.drawable.choice4;
        	images[2] = R.drawable.choice7;
        	back1 = new Sprite(images, mContext, 0, 0,mCanvasWidth/4,mCanvasHeight/4);
        	}
        	else if (loadinggfx == 6) { 
        	images[0] = R.drawable.choice2;
        	images[1] = R.drawable.choice5;
        	images[2] = R.drawable.choice8;
        	back2 = new Sprite(images, mContext, 0, 0,mCanvasWidth/4,mCanvasHeight/4);
        	}
        	else if (loadinggfx == 7) { 
        	images[0] = R.drawable.choice3;
        	images[1] = R.drawable.choice6;
        	images[2] = R.drawable.choice9;
        	back3 = new Sprite(images, mContext, 0, 0,mCanvasWidth/4,mCanvasHeight/4);
        	}        	
        	else if (loadinggfx == 8) { 
        	tick = new Sprite(R.drawable.tick, mContext, 0,0,mCanvasWidth/10,mCanvasWidth/10);
        	cross = new Sprite(R.drawable.cross, mContext, 200,0,mCanvasWidth/10,mCanvasWidth/10);
        	}        	
        	else if (loadinggfx == 9) { 
            	arrowright = new Sprite(R.drawable.arrow, mContext, 0, 0,mCanvasWidth/10,mCanvasWidth/10);
            	arrowleft = new Sprite(R.drawable.arrow, mContext, 0, 0,mCanvasWidth/10,mCanvasWidth/10);
            	arrowleft.rotate(180);
        	}        	
loadinggfx++;        	
        	
        	
        }
        

        /**
         * Starts the game, setting parameters for the current difficulty.
         */
        public void doStart() {
            synchronized (mSurfaceHolder) {
            	// Initialize game here!
            	
                back1.setPos(230,285);
            	
                mLastTime = System.currentTimeMillis() + 100;
                setState(STATE_RUNNING);
            }
        }

        /**
         * Pauses the physics update & animation.
         */
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
                    	if (unloadall == false) {
                        if (mMode == STATE_RUNNING) 
                        	updateGame();
                        if (loadinggfx <= endload) {
                            createGFX(c);
                        }
                        else 
                        render(c);
                    	}
                    	else {
                    		if (background != null)
                            background = null;
                    		else if (back1 != null)
                          back1 = null;
                    		else if (back2 != null)
                                back2 = null;
                    		else if (back3 != null)
                                back3 = null;
                    		else if (arrowright != null)
                                arrowright = null;
                    		else if (arrowleft != null)
                                arrowleft = null;
                    		else if (selectedback != null)
                                selectedback = null;
                    		else if (tick != null)
                                tick = null;
                    		else if (cross != null) {
                                cross = null;
                    		System.gc();
                    		}
                    		else {
                    			nextPage = true;
                    		}
                    	}
                    }
                } finally {
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

        private void render(Canvas canvas) {
if (selected == true) {
	canvas.drawARGB(255, 0, 0, 0);

	background.draw(canvas);
selectedback.draw(canvas);	
cross.setPos(selectedback.getX()-cross.getWidth()-10,selectedback.getY()+selectedback.getHeight()+10);
cross.draw(canvas);
tick.setPos(selectedback.getX()+selectedback.getWidth()+10,selectedback.getY()+selectedback.getHeight()+10);
tick.draw(canvas);
}
        	
else {        	
if (loading == false) {
        	// empty canvas
        	canvas.drawARGB(255, 0, 0, 0);

        	// Backgrounds First
        	background.draw(canvas);

        	back1.setPos(10, mCanvasHeight/2-back1.getHeight()/2);
        	back1.draw(canvas);
        	
        	back2.setPos(mCanvasWidth/2-back2.getWidth()/2, mCanvasHeight/2-back2.getHeight()/2);
        	back2.draw(canvas);

        	back3.setPos(mCanvasWidth-back3.getWidth()-10, mCanvasHeight/2-back3.getHeight()/2);
        	back3.draw(canvas);

        	if (selectionscreen < 3) {
        		arrowright.setPos(mCanvasWidth-arrowright.getWidth(), mCanvasHeight-arrowright.getHeight());
        		arrowright.draw(canvas);
        	}

        	if (selectionscreen > 1) {
        		arrowleft.setPos(0, mCanvasHeight-arrowleft.getHeight());
        		arrowleft.draw(canvas);
        	}
        	
}
else if (loading == true) {

	background.draw(canvas);
	paint.setTextSize(32);
	canvas.drawText("Loading...", 10, 10, paint);
	
}
}
paint.setTextSize(24);
        	
        }

        /**
         * Updates the game.
         */
        private void updateGame() {
        	//// <DoNotRemove>
            long now = System.currentTimeMillis();
            // Do nothing if mLastTime is in the future.
            // This allows the game-start to delay the start of the physics
            // by 100ms or whatever.
            if (mLastTime > now) 
            	return;
            double elapsed = (now - mLastTime) / 1000.0;
            mLastTime = now;
        }
    }

    /** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;

    /** The thread that actually draws the animation */
    private FirstThread thread;

    public FirstCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new FirstThread(holder, context, new Handler() {
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
    public FirstThread getThread() {
        return thread;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (thread.loadinggfx > thread.endload) {
    	switch (event.getAction()) {
    	case MotionEvent.ACTION_DOWN:{
    		
    		if (thread.selected == false) {
        		if (thread.back1.checkCollision(event.getX(), event.getY()) == true) {
        			
        			if (thread.selectionscreen == 1) {
            			thread.chosenback = 1;
            			thread.selectedback = new Sprite(R.drawable.choice1, mContext, thread.mCanvasWidth/2-((thread.mCanvasWidth/3)/2), (thread.mCanvasHeight/2)-((thread.mCanvasHeight/3)/2),thread.mCanvasWidth/3,thread.mCanvasHeight/3);
        			}
            			else if (thread.selectionscreen == 2) {
                			thread.chosenback = 4;
                			thread.selectedback = new Sprite(R.drawable.choice4, mContext, thread.mCanvasWidth/2-((thread.mCanvasWidth/3)/2), (thread.mCanvasHeight/2)-((thread.mCanvasHeight/3)/2),thread.mCanvasWidth/3,thread.mCanvasHeight/3);
            			}
            			else if (thread.selectionscreen == 3) {
                			thread.chosenback = 7;
                			thread.selectedback = new Sprite(R.drawable.choice7, mContext, thread.mCanvasWidth/2-((thread.mCanvasWidth/3)/2), (thread.mCanvasHeight/2)-((thread.mCanvasHeight/3)/2),thread.mCanvasWidth/3,thread.mCanvasHeight/3);
            			}

        			
        			thread.selected = true;
        		}    			
        		else if (thread.back2.checkCollision(event.getX(), event.getY()) == true) {
        			
        			if (thread.selectionscreen == 1) {
            			thread.chosenback = 2;
            			thread.selectedback = new Sprite(R.drawable.choice2, mContext, thread.mCanvasWidth/2-((thread.mCanvasWidth/3)/2), (thread.mCanvasHeight/2)-((thread.mCanvasHeight/3)/2),thread.mCanvasWidth/3,thread.mCanvasHeight/3);
        			}
            			else if (thread.selectionscreen == 2) {
                			thread.chosenback = 5;
                			thread.selectedback = new Sprite(R.drawable.choice5, mContext, thread.mCanvasWidth/2-((thread.mCanvasWidth/3)/2), (thread.mCanvasHeight/2)-((thread.mCanvasHeight/3)/2),thread.mCanvasWidth/3,thread.mCanvasHeight/3);
            			}
            			else if (thread.selectionscreen == 3) {
                			thread.chosenback = 8;
                			thread.selectedback = new Sprite(R.drawable.choice8, mContext, thread.mCanvasWidth/2-((thread.mCanvasWidth/3)/2), (thread.mCanvasHeight/2)-((thread.mCanvasHeight/3)/2),thread.mCanvasWidth/3,thread.mCanvasHeight/3);
            			}
        			
        			thread.selected = true;
        		}    			

        		else if (thread.back3.checkCollision(event.getX(), event.getY()) == true) {
        			
        			if (thread.selectionscreen == 1) {
            			thread.chosenback = 3;
            			thread.selectedback = new Sprite(R.drawable.choice3, mContext, thread.mCanvasWidth/2-((thread.mCanvasWidth/3)/2), (thread.mCanvasHeight/2)-((thread.mCanvasHeight/3)/2),thread.mCanvasWidth/3,thread.mCanvasHeight/3);
        			}
            			else if (thread.selectionscreen == 2) {
                			thread.chosenback = 6;
                			thread.selectedback = new Sprite(R.drawable.choice6, mContext, thread.mCanvasWidth/2-((thread.mCanvasWidth/3)/2), (thread.mCanvasHeight/2)-((thread.mCanvasHeight/3)/2),thread.mCanvasWidth/3,thread.mCanvasHeight/3);
            			}
            			else if (thread.selectionscreen == 3) {
                			thread.chosenback = 9;
                			thread.selectedback = new Sprite(R.drawable.choice9, mContext, thread.mCanvasWidth/2-((thread.mCanvasWidth/3)/2), (thread.mCanvasHeight/2)-((thread.mCanvasHeight/3)/2),thread.mCanvasWidth/3,thread.mCanvasHeight/3);
            			}
        			
        			thread.selected = true;
        		}    			
    		
    		
    		}
    		
    		else if (thread.selected == true) {
    		if (thread.tick.checkCollision(event.getX(), event.getY()) == true) {
thread.unloadall = true;
    		}
    		else if (thread.cross.checkCollision(event.getX(), event.getY()) == true) {
    			thread.selected = false;
    		}
    		
    		}
    		
    		
    		if (thread.nextPage == false) {
    		if (thread.arrowright.checkCollision(event.getX(), event.getY()) == true || (thread.arrowleft.checkCollision(event.getX(), event.getY()) == true  && thread.selectionscreen > 1)) {
    		
    		if (thread.arrowright.checkCollision(event.getX(), event.getY()) == true && thread.selectionscreen < 3) {
    			thread.loading = true;
    			thread.selectionscreen++;
    		}
    		else if (thread.arrowleft.checkCollision(event.getX(), event.getY()) == true && thread.selectionscreen > 1) {
    			thread.loading = true;
    			thread.selectionscreen--;
    			
    			
    		}

			if (thread.selectionscreen == 1) {
				thread.back1.setFrame(0);
				thread.back2.setFrame(0);
				thread.back3.setFrame(0);

    			}

			else if (thread.selectionscreen == 2) {
				thread.back1.setFrame(1);
				thread.back2.setFrame(1);
				thread.back3.setFrame(1);

    			}
			
			else if (thread.selectionscreen == 3) {
				thread.back1.setFrame(2);
				thread.back2.setFrame(2);
				thread.back3.setFrame(2);

    			}
			
    		}
        	thread.loading = false;
    		}
    		break;
    	}
    	}
    	return true;
    	
    	}
    	else
    		return false;
    }
    
    /**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        return thread.doKeyDown(keyCode, msg);
    }

    /**
     * Standard override for key-up. We actually care about these, so we can
     * turn off the engine or stop rotating.
     */
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
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
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
