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

    public TheGame(GameView gameView, Activity activity) {
        super(gameView);
        view = gameView;

        // Define buttons
        Button left = (Button) activity.findViewById(R.id.left);
        left.setX(0);
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    car.turningLeft = true;
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    car.turningLeft = false;
                }
                return false;
            }
        });
        Button right = (Button) activity.findViewById(R.id.right);
        right.setX(200);
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    car.turningRight = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    car.turningRight = false;
                }
                return false;
            }
        });
        Button accel = (Button) activity.findViewById(R.id.accel);
        accel.setX(400);
        accel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    car.accelerating = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    car.accelerating = false;
                }
                return false;
            }
        });

        track = new Track(gameView);
        car = new Car(view, -200, 0);
    }

    @Override
    public void setupBeginning() {
        car.xPos = mCanvasWidth/2;
        car.yPos = mCanvasHeight/2;
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
        car.update();
    }
}