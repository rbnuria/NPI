package com.photo.chroma;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Sprite  {
private Bitmap sprite = null;
private Bitmap[] spritearray = null;
private int currentFrame = 0;
private int totalFrames = 0;
private long previousTime = 0;
private long currentDelay = 500;
private boolean animStarted = false;
private boolean hidden = false;


private float xPos, yPos;



public Sprite(int[] image, Context mContext, float x, float y) {
	spritearray = new Bitmap[image.length];
	for (int i = 0; i < image.length; i++) {
	spritearray[i] = BitmapFactory.decodeResource(mContext.getResources(), image[i]);
}
	xPos = x;
	yPos = y;
	totalFrames = image.length;

}	

public Sprite(int[] image, Context mContext, float x, float y, int width, int height) {
	spritearray = new Bitmap[image.length];
	for (int i = 0; i < image.length; i++) {
		Bitmap temp = BitmapFactory.decodeResource(mContext.getResources(), image[i]);
		spritearray[i] = Bitmap.createScaledBitmap(temp, width, height, true);

	}
	xPos = x;
	yPos = y;
	totalFrames = image.length;

}	


public Sprite(int image, Context mContext, float x, float y) {
    	sprite = BitmapFactory.decodeResource(mContext.getResources(), image);
    	xPos = x;
    	yPos = y;
	}

	public Sprite(int image, Context mContext, float x, float y, int width, int height) {
		Bitmap temp = BitmapFactory.decodeResource(mContext.getResources(), image);
    	sprite = Bitmap.createScaledBitmap(temp, width, height, true);

    	temp = null;
    	xPos = x;
    	yPos = y;
	}
	
	
	
public void draw(Canvas canvas) {
	if (hidden == false) {
	if (sprite != null)
	canvas.drawBitmap(sprite, xPos, yPos, new Paint());
	else {
		canvas.drawBitmap(spritearray[currentFrame], xPos, yPos, new Paint());
	}
	}	
}
public void draw(Canvas canvas, int frame) {
	if (hidden == false) {
	canvas.drawBitmap(spritearray[frame], xPos, yPos, new Paint());
	}
}

public void setDelay(long delay) {
	currentDelay = delay;
}

public void animate() {
if (animStarted == false) {
	previousTime = System.currentTimeMillis();
animStarted = true;
}
else {

	if (System.currentTimeMillis() > previousTime + currentDelay) {
if (currentFrame < totalFrames) {
	currentFrame++;
}
else
	currentFrame = 0;

previousTime = System.currentTimeMillis();
	}	
}	
}

public void stopAnimation() {
	animStarted = false;
}

public void hideSprite(boolean hide) {
	if (hide == true)
	hidden = true;
	else
		hidden = false;
}





public void setFrame(int frame) {
currentFrame = frame;	
}

public int getFrame() {
	return currentFrame;	
	}

public int getTotalFrames() {
	return totalFrames;	
	}


	
public void moveX(float momentum) {
	xPos += momentum;
}
public void moveY(float momentum) {
	yPos += momentum;
}
public void move(float x, float y) {
	xPos += x;
	yPos += y;
}
public void setPos(float x, float y) {
	xPos = x;
	yPos = y;

}

public void setX(float x) {
	xPos = x;
}
public void setY(float y) {
	yPos = y;
}


public int getHeight() {
	if (sprite != null)
	return sprite.getHeight();
	else
		return spritearray[currentFrame].getHeight();
}
public int getWidth() {
	if (sprite != null)
	return sprite.getWidth();
	else
		return spritearray[currentFrame].getWidth();

}
	
public float getX() {
	return xPos;
	}
public float getY() {
	return yPos;
	}

public boolean checkCollision(float x, float y) {	
	if (x > xPos && x < xPos + getWidth() && y > yPos && y < yPos + getHeight() && hidden == false) {
	return true;
	}
	else return false;
}



public void rotate(float degrees) {
	Matrix rotation = new Matrix();
	rotation.postRotate(degrees);
	
	if (sprite != null) {
		sprite = Bitmap.createBitmap(sprite, 0, 0, getWidth(), getHeight(), rotation, true);
	}
	else {
		for (int i = 0; i < totalFrames;i++) {
			spritearray[i] = Bitmap.createBitmap(spritearray[i], 0, 0, getWidth(), getHeight(), rotation, true);
			
		}
	}
	
	
}

public void scale(int width, int height) {
	spritearray[currentFrame] = Bitmap.createScaledBitmap(spritearray[currentFrame],width,height,false);

}



}
