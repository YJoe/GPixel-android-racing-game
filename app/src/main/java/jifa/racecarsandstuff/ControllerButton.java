package jifa.racecarsandstuff;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class ControllerButton {
    public Rect area;
    public Paint releasePaint, pressPaint;
    public int x, y;
    public boolean pressed;

    public ControllerButton(int x, int y, int w ,int h){
        this.x = x;
        this.y = y;
        area = new Rect(x, y, x+w, y+h);
        releasePaint = new Paint();
        releasePaint.setColor(Color.rgb(0, 200, 200));
        pressPaint = new Paint();
        pressPaint.setColor(Color.rgb(0, 100, 100));
        pressed = false;
    }

    public void draw(Canvas canvas){
        if (pressed){
            canvas.drawRect(area, pressPaint);
        } else {
            canvas.drawRect(area, releasePaint);
        }
    }
}
