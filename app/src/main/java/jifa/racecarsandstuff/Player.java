package jifa.racecarsandstuff;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Player extends Car{
    // hold the index of the next checkpoint
    public int checkPointIndex;
    // an array list to store the lap times of the car
    public ArrayList<Long> lapTimes;
    // long variables storing the lap time variables
    public long lapStartTime, lastLapTime;
    // a paint object for the text fonts
    private Paint paint;

    public Player(View view, World world, int id, int[][]points, Options options){
        // call the super constructor
        super(view, world, id, points, options);
        lapTimes = new ArrayList<>();
        checkPointIndex = 1;
        angleDeg = 0;
        trackTopSpeed = (int)(2.22 * world.scale);
        grassTopSpeed = (int)(0.55 * world.scale);
        turningRate = 1.5;
        accelerationRate = (0.011 * world.scale);
        currentTopSpeed = trackTopSpeed;

        // define the text's paint
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize((int)(6.5*world.scale));

        // load the car image, selected by the options menu
        loadCar(imageCanv, options.car);
    }

    public int readTrack(){
        // get the pixel from underneath the car
        return world.track.colourImage.getPixel(
                (int)((-world.track.translateX) + xPos) / world.scale,
                (int)((-world.track.translateY) + yPos) / world.scale);
    }

    public void update(){
        // call the update of a regular car
        super.update();
        // if the car is colliding with its next checkpoint
        if (collidingCheckPoint()) {
            // if the checkpoint is the start line
            if(checkPointIndex == 0){
                // stop the lap timer and save the lap time
                stopLapTimer();
                // start the timer again for the next lap
                startLapTimer();
                // increment the lap counter
                lapCount++;
            }
            // increment the checkpoint index
            checkPointIndex += 1;
            // if the checkpoint is greater than the checkpoint count
            if (checkPointIndex > points.length - 1) {
                // set the next checkpoint to the start line
                checkPointIndex = 0;
            }
        }
    }

    public void startLapTimer(){
        lapStartTime = System.currentTimeMillis();
    }

    public void stopLapTimer(){
        lastLapTime = System.currentTimeMillis() - lapStartTime;
        lapTimes.add(lastLapTime);
    }

    public boolean collidingCheckPoint(){
        // get the coordinates of the next checkpoint
        int tx = points[checkPointIndex][0] * 10 * world.scale;
        int ty = points[checkPointIndex][1] * 10 * world.scale;
        // set a tolerance in which the car should be colliding with the checkpoint
        int range = points[checkPointIndex][2] * 10 * world.scale;
        // if the checkpoint has been reached
        if (-world.translateX + xPos < tx + range && -world.translateX + xPos > tx - range){
            if(-world.translateY + yPos < ty + range && -world.translateY + yPos > ty - range){
                return true;
            }
        }
        return false;
    }

    public void draw(Canvas canvas){
        // draw player car
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate((int) xPos - indWidth * 8, (int) yPos - indHeight * 8);
        canvas.scale(world.scale, world.scale);
        canvas.rotate((int) angleDeg, width / 4, height / 4);
        canvas.drawBitmap(image, null, scaleRect, null);
        canvas.restore();

        // draw player information
        double time = (System.currentTimeMillis() / 1000.0) - (lapStartTime / 1000.0);
        ArrayList<String>textDrawList = new ArrayList<>();
        textDrawList.add("Latest lap time: " + lastLapTime / 1000.0);
        textDrawList.add("Current lap time: " + (double)Math.round(time * 100) / 100);
        textDrawList.add("Lap: " + (lapCount + 1) + " / " + options.lapCount);
        textDrawList.add("Speed: " + (int)currentSpeed);
        textDrawList.add("Damage: " + damage);
        
        for(int i = 0; i < textDrawList.size(); i++){
            // move world scale pixels in on x, and move 6.5 * world scale for every line of text
            canvas.drawText(textDrawList.get(i), world.scale, (int)((6.5 * world.scale) * (i + 1)), paint);
        }
    }
}
