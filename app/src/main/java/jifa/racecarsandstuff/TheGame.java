package jifa.racecarsandstuff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

public class TheGame extends GameThread{
    private Car car = new Car(mGameView);
    private Track track = new Track(mGameView);

    public TheGame(GameView gameView) {
        super(gameView);
    }

    @Override
    public void setupBeginning() {

    }

    @Override
    protected void doDraw(Canvas canvas) {
        if(canvas == null) return;
        car.angleDeg += 1;
        car.yPos += 1;
        super.doDraw(canvas);
        track.draw(canvas);
        car.draw(canvas);
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