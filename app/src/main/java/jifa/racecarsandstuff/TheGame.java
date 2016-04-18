package jifa.racecarsandstuff;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class TheGame extends GameThread{
    private Player player;
    private World world;
    private MainActivity activity;
    private Options options;
    private boolean notTriedYet;

    public TheGame(GameView gameView, MainActivity activity, Options options) {
        // call the super constructor (from the GameThread class)
        super(gameView);
        this.activity = activity;
        this.options = options;
        notTriedYet = true;
        setButtons(activity);
        int[][] points;
        // switch the track flag passed to the game, tracks are constructed from a set of points
        // by loading a different set of points a new track can be loaded
        switch(options.trackFlag){
            case 0: points = new int[][]{   {5, 15, 5}, {5, 5, 5}, {40, 5, 5}, {40, 14, 5},
                    {20, 14, 4}, {16, 18, 5} ,{16, 30, 5}, {29, 30, 5},
                    {29, 22, 5}, {38, 22, 5}, {38, 40, 5}, {5, 40, 5}};
                    break;
            case 1: points = new int[][]{   {40, 10, 5}, {40 ,5, 5}, {15, 5, 5}, {15, 40, 5},
                    {5, 40, 5}, {5, 25, 5}, {40, 25, 5}};
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

        System.out.println(scale);

        // create a world from a new track
        world = new World(new Track(gameView, points, scale, options), scale, mCanvasWidth, mCanvasHeight);
        int id = 0;
        // create the player
        player = new Player(gameView, world, id, points, options);
        // move all points to the center of the tracks
        for(int i = 0; i < points.length; i++){
            points[i][0] += points[i][2] /2;
            points[i][1] += points[i][2] /2;
        }
        // if the options specifies ai should be present
        if(options.ai) {
            // create 7 AI cars
            for (int i = 0; i < 7; i++) {
                id++;
                // set the start position to the coordinates of the start positions defined in the track
                int x = world.track.startCoords.get(i).get(1) * world.scale * 10 + (10 * world.scale) - 10;
                int y = world.track.startCoords.get(i).get(0) * world.scale * 10 + (10 * world.scale) + 50;
                // add the new ai car to the car list
                world.carList.add(new AICar(gameView, world, x, y, points, id, options));
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
        // start the players lap timer
        player.startLapTimer();
        // set the players position to the center of the screen
        player.xPos = mCanvasWidth/2;
        player.yPos = mCanvasHeight/2;
        // set the world translate to the correct position
        world.setStartTranslate(mCanvasWidth, mCanvasHeight);
    }

    @Override
    protected void doDraw(Canvas canvas) {
        // if the canvas is not null
        if (canvas != null) {
            // draw the background
            super.doDraw(canvas);
            // draw all world elements
            world.draw(canvas);
            // draw the player
            player.draw(canvas);
        }
    }

    @Override
    protected void updateGame(float secondsElapsed) {
        // if the lapCount is equal to the defined lap count
        if(player.lapCount == options.lapCount && notTriedYet){
            // not tried yet stops this update from happening twice and therefore
            // wont submit high scores twice
            notTriedYet = false;
            // run on the ui thread
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // pass all lap times in order to add them to the high scores file
                    activity.tryForHighScore(player.lapTimes, options.trackFlag);
                    // display the single player stats screen
                    activity.singlePlayerStatsScreen(player.lapTimes, player.damage);
                }
            });
        }
        player.update();
        world.update(player);
    }
}