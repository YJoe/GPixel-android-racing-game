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
    private Player player;
    private World world;

    public TheGame(GameView gameView, Activity activity) {
        super(gameView);
        view = gameView;
        setButtons(activity);
        int[][] points = new int[][]{   {5, 15, 5}, {5, 5, 5}, {40, 5, 5}, {40, 14, 5},
                                {20, 14, 4}, {16, 18, 5} ,{16, 30, 5}, {29, 30, 5},
                                {29, 22, 5}, {38, 22, 5}, {38, 40, 5}, {5, 40, 5}};
        world = new World(new Track(gameView, points, 9), 9, mCanvasWidth, mCanvasHeight);
        player = new Player(view, world);
        for(int i = 0; i < points.length; i++){
            points[i][0] += points[i][2] /2;
            points[i][1] += points[i][2] /2;
        }
        for(int i = 0; i < 3; i++) {
            int x = world.track.startCoords.get(i + 1).get(1) * world.scale * 10 + (10 * world.scale) - 10;
            int y = world.track.startCoords.get(i + 1).get(0) * world.scale * 10 + (10 * world.scale) + 50;
            world.carList.add(new AICar(view, world, x, y, points));
        }
        player.world = world;
    }

    public void setButtons(Activity activity){
        // Define buttons
        Button left = (Button) activity.findViewById(R.id.left);
        left.setX(0);
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    player.turningLeft = true;
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    player.turningLeft = false;
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
                    player.turningRight = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    player.turningRight = false;
                }
                return false;
            }
        });

        Button stop = (Button) activity.findViewById(R.id.stop);
        stop.setX(500);
        stop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    player.breaking = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    player.breaking = false;
                }
                return false;
            }
        });
        Button accel = (Button) activity.findViewById(R.id.accel);
        accel.setX(700);
        accel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    player.accelerating = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    player.accelerating = false;
                }
                return false;
            }
        });
    }

    @Override
    public void setupBeginning() {
        player.xPos = mCanvasWidth/2;
        player.yPos = mCanvasHeight/2;
        world.setStartTranslate(mCanvasWidth, mCanvasHeight);
    }

    @Override
    protected void doDraw(Canvas canvas) {
        if (canvas != null) {
            world.draw(canvas);
            player.draw(canvas);
        }
    }

    @Override
    protected void actionOnTouch(float x, float y) {
    }

    @Override
    protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
    }

    @Override
    protected void updateGame(float secondsElapsed) {
        player.update();
        world.update(player);
        setScore(world.carList.get(0).breaking + " " + world.carList.get(0).accelerating + " " + (int)world.carList.get(0).currentSpeed);
    }
}