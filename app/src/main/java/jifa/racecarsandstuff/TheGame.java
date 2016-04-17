package jifa.racecarsandstuff;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class TheGame extends GameThread{
    private View view;
    private Player player;
    private World world;
    private MainActivity activity;
    private Options options;
    private Boolean notTriedYet;

    public TheGame(GameView gameView, MainActivity activity, Options options) {
        super(gameView);
        view = gameView;
        this.activity = activity;
        this.options = options;
        notTriedYet = true;
        setButtons(activity);
        int[][] points;
        switch(options.trackFlag){
            case 0: points = new int[][]{   {5, 15, 5}, {5, 5, 5}, {40, 5, 5}, {40, 14, 5},
                    {20, 14, 4}, {16, 18, 5} ,{16, 30, 5}, {29, 30, 5},
                    {29, 22, 5}, {38, 22, 5}, {38, 40, 5}, {5, 40, 5}};
                    break;
            case 1: points = new int[][]{   {5, 15, 5}, {5, 5, 5}, {30, 5, 5}, {35, 5, 5},{35,10,5}, {40, 10, 5},
                    {40, 20, 5}, {20, 20, 4}, {20, 30, 5}, {40, 30, 4}, {40, 40, 4}, {10, 40, 4}, {5,40,5}};
                    break;
            case 2: points = new int[][]{   {15, 29, 5}, {15, 24, 5}, {5, 24, 4}, {5, 10, 5}, {10, 5, 5},
                    {18, 5, 5}, {23, 10, 5}, {23, 25, 5}, {28, 29, 5}, {33, 25, 5}, {33, 7, 5}, {38, 3, 5}, {43, 7, 5},
                    {43, 37, 5}, {38, 42, 5},  {15, 42, 5}};
                    break;
            default: points = new int[][]{{}};
        }

        // scale was tested originally at a scale of 9, for other phones (screen sizes)
        // this would not be the case as 9 may scale too much or too little, so by doing
        // 9 / the width of the screen tested on (1080) 120 is the factor that is needed
        // to divide the screen width by in order to get the correct scale for the phone.
        int scale = Math.round((float)activity.width / 120);

        world = new World(new Track(gameView, points, scale, options), scale, mCanvasWidth, mCanvasHeight);
        int id = 0;
        player = new Player(view, world, id, points, options);
        for(int i = 0; i < points.length; i++){
            points[i][0] += points[i][2] /2;
            points[i][1] += points[i][2] /2;
        }
        if(options.ai) {
            for (int i = 0; i < 7; i++) {
                id++;
                int x = world.track.startCoords.get(i).get(1) * world.scale * 10 + (10 * world.scale) - 10;
                int y = world.track.startCoords.get(i).get(0) * world.scale * 10 + (10 * world.scale) + 50;
                world.carList.add(new AICar(view, world, x, y, points, id, options));
            }
        }
        player.world = world;
    }

    public void setButtons(Activity activity){
        // Define buttons
        ImageButton left = (ImageButton) activity.findViewById(R.id.left);
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    player.turningLeft = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    player.turningLeft = false;
                }
                return false;
            }
        });
        ImageButton right = (ImageButton) activity.findViewById(R.id.right);
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
        player.startLapTimer();
        player.xPos = mCanvasWidth/2;
        player.yPos = mCanvasHeight/2;
        world.setStartTranslate(mCanvasWidth, mCanvasHeight);
        System.out.println(mCanvasWidth + " " + mCanvasHeight);
    }

    @Override
    protected void doDraw(Canvas canvas) {
        if (canvas != null) {
            super.doDraw(canvas);
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
        if(player.lapCount == options.lapCount && notTriedYet){
            // not tried yet stops this update from happening twice
            notTriedYet = false;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.tryForHighScore(player.lapTimes, options.trackFlag);
                    activity.singlePlayerStatsScreen(player.lapTimes, player.damage);
                }
            });
        }
        player.update();
        world.update(player);
    }
}