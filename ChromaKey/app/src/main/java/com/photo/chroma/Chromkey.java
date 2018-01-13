package com.photo.chroma;

import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Chromkey {
private Bitmap background,temp,old;
private float xPos = 0;
private float yPos = 0;
private int Threshold = 0;
private int width, height;
public boolean processing = false;
public int savedsize = 0;

public Chromkey(int image, Context mContext) {
	background = BitmapFactory.decodeResource(mContext.getResources(), image);
	
}

public Chromkey(String location, int w, int h, int gradientError) {
width = w;
height = h;
	
	
	File imagefile = new File(location);

	try {
		FileInputStream fis = new FileInputStream(imagefile);

	temp = BitmapFactory.decodeStream(fis);
	temp = Bitmap.createScaledBitmap(temp, width, height, true);

	background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);	

	int sum = 0;		
	
	for (int i = 0; i < width/10; i++) {
		for (int j = 0; j < height/10; j++) {
			sum += Color.blue(temp.getPixel(i, j));
		}			
	}
Threshold = sum / ((width/10)*(height/10));

	
for (int i = 0; i < width; i++) {
	for (int j = 0; j < height; j++) {

		
		
		int c = temp.getPixel(i, j);
		int red = Color.red(c);
		int green = Color.green(c);
		int blue = Color.blue(c);
		int alpha = Color.alpha(c);
//		Threshold = 200;

		if (blue < Threshold-gradientError) {
			//if (blue > (150) && red < 100) {
//			int diff = (Threshold-gradientError - blue); // calculate the difference
	//		 alpha = diff;
		//	}
			int color = Color.argb(alpha, red, green, blue);
			
	background.setPixel(i, j, color);
		
		}
		else {
			background.setPixel(i, j, Color.TRANSPARENT);
		}
	}
	}
	fis.close();

	}
	catch (Exception e) {
		
	}
	
	
	
}


public void fix(int error) {
processing = true;
	background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);	
	
	for (int i = 0; i < width; i++) {
		for (int j = 0; j < height; j++) {

	
			
			int c = temp.getPixel(i, j);
			int red = Color.red(c);
			int green = Color.green(c);
			int blue = Color.blue(c);
			int alpha = Color.alpha(c);
			if (blue < Threshold-error) {
				int color = Color.argb(alpha, red, green, blue);
		background.setPixel(i, j, color);
			
			}
			else {
				background.setPixel(i, j, Color.TRANSPARENT);
			}
		}
		}
	rotate(90);
	processing = false;
	
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
	return height;
	}
	public int getWidth() {
		return width;
		}
		
	public float getX() {
		return xPos;
		}
	public float getY() {
		return yPos;
		}


public void draw(Canvas canvas) {
	if (background != null && processing == false)
	canvas.drawBitmap(background, xPos, yPos, new Paint());

}




public void rotate(float degrees) {
	Matrix rotation = new Matrix();
	rotation.postRotate(degrees);
	
		background = Bitmap.createBitmap(background, 0, 0, getWidth(), getHeight(), rotation, false);
	
	
}


public void save() {
	old = Bitmap.createBitmap(background);

}


public void erase(float x, float y) {
float actualX = 0;
float actualY = 0;
if (x >= getX()+10 && y >= getY()+10 && x < getX()+getWidth()-10 && y < getY()+getHeight()-10) {
 actualX = x - getX();
		 actualY = y - getY();
for (int i = 0; i <= 20; i++) {
	for (int j = 0; j <= 20; j++) {
		
		background.setPixel((int)actualX-5+i, (int)actualY-5+j, Color.TRANSPARENT);
	}
}
}
	
	
}


public void rescale() {
double scaleX = (1024/512)*getWidth();
double scaleY = getHeight()*(600/300);




	
	
	background = Bitmap.createScaledBitmap(old, (int)scaleX, (int)scaleY, false);
	
	
	
}


public void resize(int size) {

	// TODO

savedsize = size;

		background = Bitmap.createScaledBitmap(old, height, width, true);

	
}



}