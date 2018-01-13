package com.photo.chroma;

import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

public class Background {
private Bitmap background;
private float xPos = 0;
private float yPos = 0;

public Background(int image, Context mContext, int width, int height) {
	Bitmap temp = BitmapFactory.decodeResource(mContext.getResources(), image);
	background = Bitmap.createScaledBitmap(temp, width, height, true);
	temp = null;
	
}

public Background(String location, int width, int height) {

	File imagefile = new File(location);

	try {
		FileInputStream fis = new FileInputStream(imagefile);

	Bitmap temp = BitmapFactory.decodeStream(fis);
	background = Bitmap.createScaledBitmap(temp, width, height, true);

//	Bitmap temp = BitmapFactory.decodeStream(fis);
	//temp = Bitmap.createScaledBitmap(temp, 1024, 600, true);

//	background = Bitmap.createBitmap(1024, 768, Bitmap.Config.ARGB_4444);

/*
	for (int i = 0; i < temp.getWidth(); i++) {
		for (int j = 0; j < temp.getWidth(); j++) {
int pixel = temp.getPixel(i, j);

			
		
		}
	}
	
*/	
	
	
	temp = null;
	fis.close();

	}
	catch (Exception e) {
		
	}
	
	/*
	String filepath = "/sdcard/";
	File imagefile = new File(filepath + "back.jpg");
	//Bitmap back = null;
	//Bitmap temp = Bitmap.createBitmap(640, 480, Bitmap.Config.RGB_565); 

	try {
		FileInputStream fis = new FileInputStream(imagefile);

	bi = BitmapFactory.decodeStream(fis);

	fis.close();
	Thread.sleep(1000);

	}
	catch (Exception e) {
		
	}
*/
	
	
}



public void draw(Canvas canvas) {
	canvas.drawBitmap(background, xPos, yPos, new Paint());
}

}
