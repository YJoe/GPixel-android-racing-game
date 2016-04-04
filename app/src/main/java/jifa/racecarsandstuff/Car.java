package jifa.racecarsandstuff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by Joe on 14/03/2016.
 */
public class Car {
    public Bitmap graphics;
    public Rect scaleRect;
    public Bitmap image;
    public Track track;
    public int xPos, yPos, width, height, indWidth, indHeight;
    public double currentSpeed = 0;
    public int currentTopSpeed, trackTopSpeed, grassTopSpeed;
    public double accelerationRate = 0.1;
    public double decelerationRate = 0.08;
    public double angleDeg;
    public boolean turningLeft, turningRight, accelerating, breaking;

    public Car(View view, int xp, int yp){
        xPos = xp;
        yPos = yp;
        angleDeg = 0;
        turningLeft = false;
        turningRight = false;
        accelerating = false;
        breaking = false;
        trackTopSpeed = 20;
        grassTopSpeed = 7;
        currentTopSpeed = trackTopSpeed;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inScaled = false;
        graphics = BitmapFactory.decodeResource(view.getResources(), R.drawable.graphics, options);

        indWidth = graphics.getWidth() / 8;
        indHeight = graphics.getHeight() / 6;

        scaleRect = new Rect(0, 0, indWidth*4, indHeight*4);
        image = Bitmap.createBitmap(indWidth * 4, indHeight * 4, Bitmap.Config.ARGB_4444);
        Canvas imageCanv = new Canvas(image);
        width = image.getWidth();
        height = image.getHeight();

        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        paint.setDither(false);

        for(int x = 0; x < 2; x++){
            for(int y = 0; y < 2; y++) {
                Rect rect = new Rect(indWidth*y, indHeight*x, indWidth*y + indWidth, indHeight*x + indHeight);
                Rect rect1 = new Rect(y*indWidth+(indWidth*4), x*indHeight, y*indWidth+(indWidth*5), x*indHeight+indHeight);
                imageCanv.drawBitmap(graphics, rect1, rect, paint);
            }
        }
    }

    public void update(){
        if (currentSpeed > 1) {
            if (turningLeft) {
                angleDeg -= 1.5 - (currentSpeed * 0.01);
            }
            if (turningRight) {
                angleDeg += 1.5 - (currentSpeed * 0.01);
            }
        }
        if (accelerating){
            if (currentSpeed < currentTopSpeed) {
                currentSpeed += accelerationRate;
            }
        } else {
            if(currentSpeed > 0){
                currentSpeed -= decelerationRate;
            } else if (currentSpeed < -(decelerationRate + 0.1)){
                currentSpeed += decelerationRate;
            } else {
                currentSpeed = 0;
            }
        }
        if (breaking){
            if(currentSpeed > 0){
                currentSpeed -= (decelerationRate * 2) - (currentSpeed * 0.002);
            } else {
                currentSpeed += (decelerationRate * 2);
            }
        }
        trackSurfacePenalties();
        if(currentSpeed > currentTopSpeed){
            currentSpeed -= decelerationRate*3;
        }
    }

    public void trackSurfacePenalties(){
        int pixel = track.colourImage.getPixel((int)((-track.translateX) + xPos) / track.scale, (int)((-track.translateY) + yPos) / track.scale);
        if (Color.blue(pixel) != 0){
            if(currentTopSpeed != trackTopSpeed) {
                currentTopSpeed = trackTopSpeed;
            }
        } else if(Color.green(pixel) != 0){
            if(currentTopSpeed != grassTopSpeed) {
                currentTopSpeed = grassTopSpeed;
            }
        } else if(Color.red(pixel) != 0){
            // car is on track edge
        } else{
            currentSpeed+=1;
            currentSpeed = -currentSpeed;
        }
    }

    public void draw(Canvas canvas){
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(xPos - indWidth*8, yPos - indHeight*8);
        canvas.scale(10, 10);
        canvas.rotate((int)angleDeg, width/4, height/4);
        canvas.drawBitmap(image, null, scaleRect, null);
        canvas.restore();
    }
}
