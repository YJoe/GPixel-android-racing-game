package jifa.racecarsandstuff;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class TheGame extends GameThread{
    private View view;
    private Car car;
    private Track track;

    public TheGame(GameView gameView) {
        super(gameView);
        view = gameView;
        track = new Track(gameView);
        car = new Car(view, mCanvasWidth/2, mCanvasHeight/2);
    }

    @Override
    public void setupBeginning() {
        car = new Car(view, mCanvasWidth/2, mCanvasHeight/2);
    }

    @Override
    protected void doDraw(Canvas canvas) {
        if(canvas == null) return;
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
        car.angleDeg += 1;
    }
}