package com.photo.chroma;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class GuiKeyboard  {
private Bitmap[] spritearray = null;
private int currentFrame = 0;
private int totalFrames = 0;
private long previousTime = 0;
private long currentDelay = 500;
private boolean animStarted = false;
private boolean hidden = false;

private final int ENGLISH_NOCAPS = 0;
private final int ENGLISH_CAPS = 1;
private final int GREEK_NOCAPS = 2;
private final int GREEK_CAPS = 3;
private final int GREEK_TONES_NOCAPS = 4;
private final int GREEK_TONES_CAPS = 5;
private final int NUMS = 6;




private float xPos, yPos;


public String message = "";
public boolean close = false;




public GuiKeyboard(int[] image, Context mContext, float x, float y, int width, int height) {
	close = false;
	spritearray = new Bitmap[image.length];
	for (int i = 0; i < image.length; i++) {
		Bitmap temp = BitmapFactory.decodeResource(mContext.getResources(), image[i]);
		spritearray[i] = Bitmap.createScaledBitmap(temp, width, height, true);

	}
	xPos = x;
	yPos = y;
	totalFrames = image.length;

}	

	
public void draw(Canvas canvas) {
	if (hidden == false) {
		canvas.drawBitmap(spritearray[currentFrame], xPos, yPos, new Paint());
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
		return spritearray[currentFrame].getHeight();
}
public int getWidth() {
		return spritearray[currentFrame].getWidth();

}
	
public float getX() {
	return xPos;
	}
public float getY() {
	return yPos;
	}

public void checkCollision(float x, float y) {	
	if (x > xPos && x < xPos + getWidth() && y > yPos && y < yPos + getHeight() && hidden == false) {
		float align = 600-302;
if (message.length() < 64) {
		if (x > 86 && x < 86+70 && y > align+3 && y < align+3+65) {
			if (currentFrame == ENGLISH_NOCAPS)
	message += "q";
			else if (currentFrame == ENGLISH_CAPS)
				message += "Q";
			else if (currentFrame == GREEK_NOCAPS)
				currentFrame = GREEK_TONES_NOCAPS;
			else if (currentFrame == GREEK_CAPS)
				currentFrame = GREEK_TONES_CAPS;
			else if (currentFrame == GREEK_TONES_NOCAPS)
				currentFrame = GREEK_NOCAPS;
			else if (currentFrame == GREEK_TONES_CAPS)
				currentFrame = GREEK_CAPS;
			else if (currentFrame == NUMS)
				message += "1";
			
		
		}
		
		else if (x > 106 && x < 172 && y > align+234 && y < align+300) {
			if (currentFrame == NUMS) {
				currentFrame = ENGLISH_NOCAPS;
			}
			else
				currentFrame = NUMS;
			
			
		}		
		
else if (x > 162 && x < 162+70 && y > align+3 && y < align+68) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "w";
	else if (currentFrame == ENGLISH_CAPS)
		message += "W";
	else if (currentFrame == NUMS)
		message += "2";
	else 
		message += "ς";
		


}
else if (x > 236 && x < 236+70 && y > align+3 && y < align+68) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "e";
	else if (currentFrame == ENGLISH_CAPS)
		message += "E";
	else if (currentFrame == GREEK_CAPS) {
		message += "Ε";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "ε";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Έ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "έ";
	}
	else if (currentFrame == NUMS)
		message += "3";
		

}
else if (x > 312 && x < 312+70 && y > align+3 && y < align+68) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "r";
	else if (currentFrame == ENGLISH_CAPS)
		message += "R";
	else if (currentFrame == GREEK_CAPS) {
		message += "Ρ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "ρ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Ρ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "ρ";
	}
	else if (currentFrame == NUMS)
		message += "4";

}
else if (x > 388 && x < 388+70 && y > align+3 && y < align+68) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "t";
	else if (currentFrame == ENGLISH_CAPS)
		message += "T";
	else if (currentFrame == GREEK_CAPS) {
		message += "Τ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "τ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Τ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "τ";
	}
	else if (currentFrame == NUMS)
		message += "5";

}
else if (x > 464 && x < 464+70 && y > align+3 && y < align+68) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "y";
	else if (currentFrame == ENGLISH_CAPS)
		message += "Y";
	else if (currentFrame == GREEK_CAPS) {
		message += "Υ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "υ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Ύ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "ύ";
	}
	else if (currentFrame == NUMS)
		message += "6";

}
else if (x > 540 && x < 540+70 && y > align+3 && y < align+68) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "u";
	else if (currentFrame == ENGLISH_CAPS)
		message += "U";
	else if (currentFrame == GREEK_CAPS) {
		message += "Θ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "θ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Θ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "θ";
	}
	else if (currentFrame == NUMS)
		message += "7";

}
else if (x > 616 && x < 616+70 && y > align+3 && y < align+68) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "i";
	else if (currentFrame == ENGLISH_CAPS)
		message += "I";
	else if (currentFrame == GREEK_CAPS) {
		message += "Ι";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "ι";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Ί";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "ί";
	}
	else if (currentFrame == NUMS)
		message += "8";

}
else if (x > 692 && x < 692+70 && y > align+3 && y < align+68) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "o";
	else if (currentFrame == ENGLISH_CAPS)
		message += "O";
	else if (currentFrame == GREEK_CAPS) {
		message += "Ο";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "ο";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Ό";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "ό";
	}
	else if (currentFrame == NUMS)
		message += "9";

}
else if (x > 768 && x < 768+70 && y > align+3 && y < align+68) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "p";
	else if (currentFrame == ENGLISH_CAPS)
		message += "P";
	else if (currentFrame == GREEK_CAPS) {
		message += "Π";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "π";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Π";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "π";
	}
	else if (currentFrame == NUMS)
		message += "0";

}
else if (x > 108 && x < 108+70 && y > align+79 && y < align+79+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "a";
	else if (currentFrame == ENGLISH_CAPS)
		message += "A";
	else if (currentFrame == GREEK_CAPS) {
		message += "Α";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "α";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Ά";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "ά";
	}
}
else if (x > 185 && x < 185+70 && y > align+79 && y < align+79+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "s";
	else if (currentFrame == ENGLISH_CAPS)
		message += "S";
	else if (currentFrame == GREEK_CAPS) {
		message += "Σ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "σ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Σ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "σ";
	}
}
else if (x > 260 && x < 260+70 && y > align+79 && y < align+79+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "d";
	else if (currentFrame == ENGLISH_CAPS)
		message += "D";
	else if (currentFrame == GREEK_CAPS) {
		message += "Δ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "δ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Δ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "δ";
	}
}
else if (x > 336 && x < 336+70 && y > align+79 && y < align+79+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "f";
	else if (currentFrame == ENGLISH_CAPS)
		message += "F";
	else if (currentFrame == GREEK_CAPS) {
		message += "Φ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "φ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Φ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "φ";
	}
}
else if (x > 412 && x < 412+70 && y > align+79 && y < align+79+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "g";
	else if (currentFrame == ENGLISH_CAPS)
		message += "G";
	else if (currentFrame == GREEK_CAPS) {
		message += "Γ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "γ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Γ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "γ";
	}
}
else if (x > 488 && x < 488+70 && y > align+79 && y < align+79+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "h";
	else if (currentFrame == ENGLISH_CAPS)
		message += "H";
	else if (currentFrame == GREEK_CAPS) {
		message += "Η";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "η";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Ή";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "ή";
	}
}
else if (x > 565 && x < 565+70 && y > align+79 && y < align+79+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "j";
	else if (currentFrame == ENGLISH_CAPS)
		message += "J";
	else if (currentFrame == GREEK_CAPS) {
		message += "Ξ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "ξ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Ξ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "ξ";
	}
}
else if (x > 641 && x < 641+70 && y > align+79 && y < align+79+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "k";
	else if (currentFrame == ENGLISH_CAPS)
		message += "K";
	else if (currentFrame == GREEK_CAPS) {
		message += "Κ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "κ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Κ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "κ";
	}
}
else if (x > 717 && x < 717+70 && y > align+79 && y < align+79+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "l";
	else if (currentFrame == ENGLISH_CAPS)
		message += "L";
	else if (currentFrame == GREEK_CAPS) {
		message += "Λ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "λ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Λ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "λ";
	}
}
else if (x > 793 && x < 793+70 && y > align+79 && y < align+79+65) {
	message += ";";
}
else if (x > 869 && x < 869+70 && y > align+79 && y < align+79+65) {
	message += "!";
}
else if (x > 134 && x < 134+70 && y > align+161 && y < align+161+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "z";
	else if (currentFrame == ENGLISH_CAPS)
		message += "Z";
	else if (currentFrame == GREEK_CAPS) {
		message += "Ζ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "ζ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Ζ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "ζ";
	}
}
else if (x > 210 && x < 210+70 && y > align+161 && y < align+161+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "x";
	else if (currentFrame == ENGLISH_CAPS)
		message += "X";
	else if (currentFrame == GREEK_CAPS) {
		message += "Χ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "χ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Χ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "χ";
	}
}
else if (x > 287 && x < 287+70 && y > align+161 && y < align+161+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "c";
	else if (currentFrame == ENGLISH_CAPS)
		message += "C";
	else if (currentFrame == GREEK_CAPS) {
		message += "Ψ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "ψ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Ψ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "ψ";
	}
}
else if (x > 362 && x < 362+70 && y > align+161 && y < align+161+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "v";
	else if (currentFrame == ENGLISH_CAPS)
		message += "V";
	else if (currentFrame == GREEK_CAPS) {
		message += "Ω";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "ω";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Ώ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "ώ";
	}
}
else if (x > 439 && x < 439+70 && y > align+161 && y < align+161+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "b";
	else if (currentFrame == ENGLISH_CAPS)
		message += "B";
	else if (currentFrame == GREEK_CAPS) {
		message += "Β";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "β";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Β";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "β";
	}
}
else if (x > 515 && x < 515+70 && y > align+161 && y < align+161+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "n";
	else if (currentFrame == ENGLISH_CAPS)
		message += "N";
	else if (currentFrame == GREEK_CAPS) {
		message += "Ν";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "ν";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Ν";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "ν";
	}
}
else if (x > 591 && x < 591+70 && y > align+161 && y < align+161+65) {
	if (currentFrame == ENGLISH_NOCAPS)
		message += "m";
	else if (currentFrame == ENGLISH_CAPS)
		message += "M";
	else if (currentFrame == GREEK_CAPS) {
		message += "Μ";
	}
	else if (currentFrame == GREEK_NOCAPS) {
		message += "μ";
	}
	else if (currentFrame == GREEK_TONES_CAPS) {
		message += "Μ";
	}
	else if (currentFrame == GREEK_TONES_NOCAPS) {
		message += "μ";
	}
}
else if (x > 667 && x < 667+70 && y > align+161 && y < align+161+65) {
	message += ",";
}
else if (x > 744 && x < 744+70 && y > align+161 && y < align+161+65) {
	message += ".";
}
else if (x > 819 && x < 819+70 && y > align+161 && y < align+161+65) {
	message += "?";
}


else if (x > 258 && x < 834 && y > align+234 && y < align+302) {
	message += " ";
}

}

if (x > 844 && x < 993 && y > align+3 && y < align+68) {
if (message.length() > 0) {
message = message.substring(0, message.length()-1);	
}
}

else if (x > 12 && x < 128 && y > align+157 && y < align+227) {
if (currentFrame == ENGLISH_NOCAPS) {
	currentFrame = ENGLISH_CAPS;
}
else if (currentFrame == ENGLISH_CAPS) {
	currentFrame = ENGLISH_NOCAPS;
}
else if (currentFrame == GREEK_NOCAPS) {
	currentFrame = GREEK_CAPS;
}
else if (currentFrame == GREEK_CAPS) {
	currentFrame = GREEK_NOCAPS;
}
else if (currentFrame == GREEK_TONES_NOCAPS) {
	currentFrame = GREEK_TONES_CAPS;
}
else if (currentFrame == GREEK_TONES_CAPS) {
	currentFrame = GREEK_TONES_NOCAPS;
}

}

else if (x > 180 && x < 248 && y > align+234 && y < align+302) {
	if (currentFrame == ENGLISH_NOCAPS) {
		currentFrame = GREEK_NOCAPS;
	}
	else if (currentFrame == ENGLISH_CAPS) {
		currentFrame = GREEK_CAPS;
	}
	else if (currentFrame == GREEK_NOCAPS || currentFrame == GREEK_TONES_NOCAPS) {
		currentFrame = ENGLISH_NOCAPS;
	}
	else if (currentFrame == GREEK_CAPS || currentFrame == GREEK_TONES_CAPS) {
		currentFrame = ENGLISH_CAPS;
	}

	
}
else if (x > 894 && x < 1017+70 && y > align+161 && y < align+161+65) {
	close = true;
}






	
	
	
	
	
	
	
	
	
	
	}
}



public void rotate(float degrees) {
	Matrix rotation = new Matrix();
	rotation.postRotate(degrees);
	
		for (int i = 0; i < totalFrames;i++) {
			spritearray[i] = Bitmap.createBitmap(spritearray[i], 0, 0, getWidth(), getHeight(), rotation, true);
			
	}
	
	
}

public void scale(int width, int height) {
	spritearray[currentFrame] = Bitmap.createScaledBitmap(spritearray[currentFrame],width,height,false);

}



}
