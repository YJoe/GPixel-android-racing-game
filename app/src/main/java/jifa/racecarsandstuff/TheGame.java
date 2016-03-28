package jifa.racecarsandstuff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

public class TheGame extends GameThread{
    private Car car;
    private Track track;

    public TheGame(GameView gameView) {
        super(gameView);
        track = new Track(gameView);
        car = new Car(gameView);
    }

    @Override
    public void setupBeginning() {

    }

    @Override
    protected void doDraw(Canvas canvas) {
        if(canvas == null) return;
        super.doDraw(canvas);
        track.draw(canvas);
        //car.draw(canvas);
    }

    @Override
    protected void actionOnTouch(float x, float y) {
    }

    @Override
    protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
    }

    @Override
    protected void updateGame(float secondsElapsed) {
    }
}