package jifa.racecarsandstuff;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Player extends Car{
    public int checkPointIndex;

    public Player(View view, World world, int id, int[][]points){
        super(view, world, id, points);
        checkPointIndex = 1;
        angleDeg = 0;
        trackTopSpeed = 20;
        grassTopSpeed = 5;
        turningRate = 1.5;
        accelerationRate = 0.1;
        currentTopSpeed = trackTopSpeed;

        loadCar(imageCanv, "pokemon");
    }

    public int readTrack(){
        return world.track.colourImage.getPixel(
                (int)((-world.track.translateX) + xPos) / world.scale,
                (int)((-world.track.translateY) + yPos) / world.scale);
    }

    public void update(){
        super.update();
        if (collidingCheckPoint()) {
            if(checkPointIndex == 0){
                lapCount++;
            }
            checkPointIndex += 1;
            if (checkPointIndex > points.length - 1) {
                checkPointIndex = 0;
            }
        }
    }

    public boolean collidingCheckPoint(){
        int tx = points[checkPointIndex][0] * 10 * world.scale;
        int ty = points[checkPointIndex][1] * 10 * world.scale;
        int range = points[checkPointIndex][2] * 10 * world.scale;
        if (-world.translateX + xPos < tx + range && -world.translateX + xPos > tx - range){
            if(-world.translateY + yPos < ty + range && -world.translateY + yPos > ty - range){
                return true;
            }
        }
        return false;
    }

    public void draw(Canvas canvas){
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate((int)xPos - indWidth * 8, (int)yPos - indHeight * 8);
        canvas.scale(9, 9);
        canvas.rotate((int) angleDeg, width / 4, height / 4);
        canvas.drawBitmap(image, null, scaleRect, null);
        canvas.restore();
    }
}
