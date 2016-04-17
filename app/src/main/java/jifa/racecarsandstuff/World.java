package jifa.racecarsandstuff;

import android.graphics.Canvas;

import java.util.ArrayList;

public class World {
    public Track track;
    public ArrayList<AICar> carList;
    public int scale;
    public float translateX, translateY;

    public World(){
        carList = new ArrayList<>();
        translateX = 0;
        translateY = 0;
        scale = 9;
    }
    public World(Track track, int scale, int scrW, int scrH){
        this();
        this.track = track;
        this.scale = scale;
    }

    public void setStartTranslate(int screenWidth, int screenHeight){
        // move the world to the initial staring position
        this.translateX = -track.startCoords.get(carList.size()).get(1) * 10 * scale + screenWidth/2 - (10 * scale) + 10;
        this.translateY = -track.startCoords.get(carList.size()).get(0) * 10 * scale + screenHeight/2 - (10 * scale) - 50;
        track.translateX = this.translateX;
        track.translateY = this.translateY;
    }

    public void update(Car player){
        // solve how much to move the world dependent of the speed and angle of the player's car
        translateX += -player.currentSpeed * Math.sin(Math.toRadians(player.angleDeg));
        translateY += player.currentSpeed * Math.cos(Math.toRadians(player.angleDeg));
        // provide the track with the same information
        track.update(translateX, translateY);
        // update all of the cars in the car list
        for(int i = 0; i < carList.size(); i++){
            carList.get(i).update();
        }
    }

    public void draw(Canvas canvas){
        // draw the track
        track.draw(canvas);
        // draw all cars in the car list
        for(int i = 0; i < carList.size(); i++){
            carList.get(i).draw(canvas);
        }
    }
}
