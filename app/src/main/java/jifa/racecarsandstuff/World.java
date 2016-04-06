package jifa.racecarsandstuff;

import android.graphics.Canvas;

import java.util.ArrayList;

public class World {
    public Track track;
    public ArrayList<Car> carList;
    public int translateX, translateY, scale;
    public double dx, dy;

    public World(){
        carList = new ArrayList<>();
        translateX = 0;
        translateY = 0;
        dx = 0;
        dy = 0;
        scale = 9;
    }
    public World(Track track, int scale, int scrW, int scrH){
        this();
        this.track = track;
        this.scale = scale;
    }

    public void setStartTranslate(int screenWidth, int screenHeight){
        this.translateX = -track.startCoords.get(0).get(1) * 10 * scale + screenWidth/2 - (10 * scale) + 10;
        this.translateY = -track.startCoords.get(0).get(0) * 10 * scale + screenHeight/2 - (10 * scale) - 50;
        track.translateX = this.translateX;
        track.translateY = this.translateY;
    }

    public void update(Car player){
        dx = -player.currentSpeed * Math.sin(Math.toRadians(player.angleDeg));
        dy = player.currentSpeed * Math.cos(Math.toRadians(player.angleDeg));
        track.update(dx, dy);
        for(int i = 0; i < carList.size(); i++){
            carList.get(i).update();
        }
    }

    public void draw(Canvas canvas){
        track.draw(canvas);
        for(int i = 0; i < carList.size(); i++){
            carList.get(i).draw(canvas);
        }
    }
}
