package jifa.racecarsandstuff;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int MENU_RESUME = 1;
    private static final int MENU_START = 2;
    private static final int MENU_STOP = 3;

    private GameThread mGameThread;
    private GameView mGameView;
    private MainActivity self;
    private int trackFlag;
    private Options options;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        options = new Options();

        self = this;
        setHomeScreen();
    }

    private void setHomeScreen(){
        setContentView(R.layout.start_layout);

        mGameThread = null;
        mGameView = null;

        final Button play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.game_layout);
                carPicker();
            }
        });
    }

    public void carPicker(){
        setContentView(R.layout.car_picker);

        Button blue = (Button) findViewById(R.id.button);
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.car = "blue";
                trackPicker();
            }
        });

        Button red = (Button) findViewById(R.id.button2);
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.car = "red";
                trackPicker();
            }
        });

        Button green = (Button) findViewById(R.id.button3);
        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.car = "green";
                trackPicker();
            }
        });

        Button black = (Button) findViewById(R.id.button8);
        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.car = "black";
                trackPicker();
            }
        });

        Button toothpaste = (Button) findViewById(R.id.button5);
        toothpaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.car = "toothpaste";
                trackPicker();
            }
        });

        Button pokemon = (Button) findViewById(R.id.button6);
        pokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.car = "pokemon";
                trackPicker();
            }
        });

        Button herbie = (Button) findViewById(R.id.button7);
        herbie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.car = "herbie";
                trackPicker();
            }
        });

        Button flame = (Button) findViewById(R.id.button9);
        flame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.car = "flame";
                trackPicker();
            }
        });

        Button random = (Button) findViewById(R.id.random);
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                options.car = "toothpaste";
                trackPicker();
            }
        });

    }

    public void trackPicker(){
        setContentView(R.layout.track_picker);

        final Button track1btn = (Button) findViewById(R.id.track1);
        track1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.game_layout);
                mGameView = (GameView) findViewById(R.id.gamearea);
                mGameView.setStatusView((TextView) findViewById(R.id.text));
                mGameView.setScoreView((TextView) findViewById(R.id.score));
                mGameView.activity = self;
                trackFlag = 0;
                mGameView.trackFlag = 0;
                startGame();
            }
        });
        final Button track2btn = (Button) findViewById(R.id.track2);
        track2btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.game_layout);
                mGameView = (GameView) findViewById(R.id.gamearea);
                mGameView.setStatusView((TextView) findViewById(R.id.text));
                mGameView.setScoreView((TextView) findViewById(R.id.score));
                mGameView.activity = self;
                trackFlag = 1;
                mGameView.trackFlag = 1;
                startGame();
            }
        });
        final Button track3btn = (Button) findViewById(R.id.track3);
        track3btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.game_layout);
                mGameView = (GameView) findViewById(R.id.gamearea);
                mGameView.setStatusView((TextView) findViewById(R.id.text));
                mGameView.setScoreView((TextView) findViewById(R.id.score));
                mGameView.activity = self;
                trackFlag = 2;
                mGameView.trackFlag = 2;
                startGame();
            }
        });
    }

    public void singlePlayerStatsScreen(ArrayList<Long>lapTimes, int damage){
        setContentView(R.layout.single_player_stats);
        final Button track1btn = (Button) findViewById(R.id.return_home);
        track1btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHomeScreen();
            }
        });

        double average = 0;
        TextView lapInput = (TextView) findViewById(R.id.lap_input);
        for(int i = 0; i < lapTimes.size(); i++){
            lapInput.append(lapTimes.get(i)/1000.f + "\n");
            average += lapTimes.get(i)/1000.f;
        }
        average/=(double)lapTimes.size();
        TextView averageInput = (TextView) findViewById(R.id.average_input);
        averageInput.append(Math.round(average * 100.0) / 100.0 + "");
        TextView scoreInput = (TextView) findViewById(R.id.score_input);
        scoreInput.append(Math.round(average * (double) (10 - damage) * 100.0) / 100.0 + "");
    }

    private void startGame() {
        //Set up a new game, we don't care about previous states
        mGameThread = new TheGame(mGameView, this, trackFlag);
        mGameView.setThread(mGameThread);
        mGameThread.setState(GameThread.STATE_READY);
        mGameView.startSensor((SensorManager) getSystemService(Context.SENSOR_SERVICE));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mGameThread != null) {
            if (mGameThread.getMode() == GameThread.STATE_RUNNING) {
                mGameThread.setState(GameThread.STATE_PAUSE);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mGameView != null) {
            mGameView.cleanup();
            mGameView.removeSensor((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        }
        mGameThread = null;
        mGameView = null;
    }

    /*
     * UI Functions
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_START, 0, R.string.menu_start);
        menu.add(0, MENU_STOP, 0, R.string.menu_stop);
        menu.add(0, MENU_RESUME, 0, R.string.menu_resume);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }

        return false;
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing if nothing is selected
    }
}

// This file is part of the course "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist
// It is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// It is is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
//
// You should have received a copy of the GNU General Public License
// along with it.  If not, see <http://www.gnu.org/licenses/>.
