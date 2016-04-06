package jifa.racecarsandstuff;

import android.graphics.Canvas;
import android.view.View;

public class AICar extends Car{
    public AICar(View view, World world, int x, int y){
        super(view);
        xPos = x;
        yPos = y;
        this.world = world;
    }
    public void draw(Canvas canvas){
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(xPos - indWidth*8 + (int)world.translateX, yPos - indHeight*8 + (int)world.translateY);
        canvas.scale(9, 9);
        canvas.rotate((int)angleDeg, width/4, height/4);
        canvas.drawBitmap(image, null, scaleRect, null);
        canvas.restore();
    }
    public void trackSurfacePenalties(){

    }
}
