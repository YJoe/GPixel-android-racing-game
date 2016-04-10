package jifa.racecarsandstuff;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.Random;

public class Player extends Car{
    public Player(View view, World world, int id){
        super(view, world, id);
        angleDeg = 0;
        trackTopSpeed = 20;
        grassTopSpeed = 5;
        turningRate = 1.5;
        accelerationRate = 0.1;
        currentTopSpeed = trackTopSpeed;

        String[] colours = {"blue", "red", "purple", "green", "yellow", "white"};
        loadCar(imageCanv, colours[new Random().nextInt(colours.length)]);
    }

    public int readTrack(){
        return world.track.colourImage.getPixel(
                (int)((-world.track.translateX) + xPos) / world.scale,
                (int)((-world.track.translateY) + yPos) / world.scale);
    }

    public void draw(Canvas canvas){
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(xPos - indWidth * 8, yPos - indHeight * 8);
        canvas.scale(9, 9);
        canvas.rotate((int) angleDeg, width / 4, height / 4);
        canvas.drawBitmap(image, null, scaleRect, null);
        canvas.restore();
    }
}
