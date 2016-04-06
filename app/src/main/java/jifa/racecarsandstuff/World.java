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
    public World(Track track, int scale){
        this();
        this.track = track;
        this.scale = scale;
    }

    public void update(){
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
