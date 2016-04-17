package jifa.racecarsandstuff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.Random;

public abstract class Car {
    protected Bitmap graphics;
    protected Rect scaleRect;
    protected Bitmap image;
    protected World world;
    public double dx, dy, xPos, yPos, currentSpeed = 0;
    protected int width, height, indWidth, indHeight, damage;
    protected int currentTopSpeed, trackTopSpeed, grassTopSpeed;
    protected double accelerationRate, decelerationRate;
    public double angleDeg;
    protected double turningRate;
    public boolean turningLeft, turningRight, accelerating, breaking;
    protected int turnLockTime, id;
    public int lapCount;
    protected int slipDirection;
    protected Canvas imageCanv;
    protected int collisionRange, collisionVoidTime, oilSlipTime;
    protected int[][] points;
    protected Options options;

    public Car(View view, World world, int id, int[][]points, Options options){
        // define basic attributes
        xPos = -200;
        yPos = 0;
        dx = 0;
        dy = 0;
        this.options = options;
        this.id = id;
        this.points = points;
        turnLockTime = 0;
        breaking = false;
        turningLeft = false;
        turningRight = false;
        accelerating = false;
        collisionVoidTime = 0;
        damage = 0;
        this.world = world;
        decelerationRate = 0.08;
        lapCount = 0;

        // define options allowing for the pixel art to be scaled without loss of sharp edges
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inDither = false;
        opt.inScaled = false;
        // load the graphics image with the options defined
        graphics = BitmapFactory.decodeResource(view.getResources(), R.drawable.graphics, opt);

        // define the individual width and height of a graphics space
        indWidth = graphics.getWidth() / 8;
        indHeight = graphics.getHeight() / 8;

        // define a rect in which to scale the graphics at upon drawing
        scaleRect = new Rect(0, 0, indWidth*4, indHeight*4);
        // create a bitmap the size of four graphics tiles (each car is positioned in four squares)
        image = Bitmap.createBitmap(indWidth * 4, indHeight * 4, Bitmap.Config.ARGB_4444);
        // create a canvas from the image
        imageCanv = new Canvas(image);
        // get the width of the canvas, this will be used to draw the graphics spaces to
        // in the constructors of AI car and Player
        width = image.getWidth();
        height = image.getHeight();

        // set a collision range
        collisionRange = height;
    }

    public double solveCurrentTurnRate(){
        return turningRate - (currentSpeed * 0.01);
    }

    public void loadCar(Canvas imageCanv, String style){
        // define paint options for the graphics ensuring it wont be blurred upon drawing to the image canvas
        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        paint.setDither(false);

        // values used to decode the position of the car images on the prote sheet
        int col = 0, row = 0;

        // switch the value passed to the load function
        switch(style){
            case "blue": col = 0; row = 0; break;
            case "red" : col = 2; row = 0; break;
            case "green" : col = 0; row = 1; break;
            case "flame" : col = 2; row = 1; break;
            case "toothpaste" : col = 0; row = 2; break;
            case "pokemon" : col = 2; row = 2; break;
            case "black" : col = 2; row = 3; break;
            case "herbie" : col = 0; row = 3; break;
        }

        // solve where in the sprite sheet is needed to load in order to get the correct image
        for(int x = 0; x < 2; x++){
            for(int y = 0; y < 2; y++) {
                // rect for where to print the image to the canvas
                Rect rect = new Rect(indWidth*y, indHeight*x, indWidth*y + indWidth, indHeight*x + indHeight);
                // rect for where to retrieve the rect of the car from the graphics
                Rect rect1 = new Rect(  y*indWidth+(indWidth*4) + (indWidth * col),
                        x*indHeight+(indWidth * row) + (indHeight * row),
                        y*indWidth+(indWidth*4) + (indWidth * (col + 1)),
                        x*indHeight+(indHeight * row) + (indHeight * (row + 1)));
                // draw the car to the image canvas
                imageCanv.drawBitmap(graphics, rect1, rect, paint);
            }
        }
    }

    public void trackSurfacePenalties(){
        // get the correct pixel from beneath the car, readTrack is
        // overwritten in both Player and AICar
        int pixel = readTrack();

        // if the car is on top of a blue pixel (track)
        if (Color.blue(pixel) == 255){
            if(currentTopSpeed != trackTopSpeed) {
                currentTopSpeed = trackTopSpeed;
            }
        }
        // if the car is on top of a green pixel (grass)
        else if(Color.green(pixel) == 255){
            if(currentTopSpeed != grassTopSpeed) {
                currentTopSpeed = grassTopSpeed;
            }
        }
        // if the car is on top of a red pixel (track edge)
        else if(Color.red(pixel) == 255){
            // car is on track edge
        }
        // if the car is on top of a black pixel (tire)
        else if(Color.red(pixel) == 0){
            // increment the damage of the car depending on the current speed
            damage += (currentSpeed / 5);
            // truncate it to within 20
            if (damage > 20) damage = 20;
            // the speed is incremented before inverting in order to provide the car with enough
            // speed to "get out" of the tire pixels, if this was not implemented, the car may
            // have decelerated enough such that it does not have the speed to return to the grass
            // and would keep inverting the speed, therefore getting stuck
            currentSpeed += 3;
            currentSpeed = -currentSpeed;
            // lock the car steering for 10 updates
            turnLockTime += 10;
        }
        // if the track is on anything else (it can only be oil)
        else {
            // if the car has not slipped recently
            if(oilSlipTime == 0) {
                // pick a random direction to swerve
                if(new Random().nextInt(2) == 0) slipDirection = -1;
                else slipDirection = 1;
            }
            // note that the car is slipping for 20 updates
            oilSlipTime += 20;
        }
    }

    public void checkCollides(){
        // if there are any AI's to collide with
        if(options.ai) {
            // check if the car is colliding with another car
            int x1 = (int) xPos + (int) world.translateX + 7;
            int y1 = (int) yPos + (int) world.translateY + 7;
            for (int i = 0; i < world.carList.size(); i++) {
                // if the car is not checking itself
                if (world.carList.get(i).id != id) {
                    int x2 = (int) world.carList.get(i).xPos + (int) world.translateX + 7;
                    int y2 = (int) world.carList.get(i).yPos + (int) world.translateY + 7;
                    if (Math.pow((double) (x1 - x2), 2) + Math.pow((double) (y1 - y2), 2) < Math.pow((double) height * 2, 2)) {
                        // penalty for colliding
                        damage += 1;
                        collisionVoidTime += 10;
                        world.carList.get(i).collisionVoidTime += 10;
                    }
                }
            }
        }
    }

    abstract public int readTrack();

    abstract public void draw(Canvas canvas);

    public void update(){
        // if the car is not void from collisions check collisions, else decay the timer
        if(collisionVoidTime == 0) checkCollides();
        else collisionVoidDecay();

        // if the car's turning is not locked
        if(turnLockTime == 0) {
            // if the current speed is anything other than 0
            if (currentSpeed > 1 || currentSpeed < -1) {
                // if the car should be turning left
                if (turningLeft) {
                    // decrement the degree
                    angleDeg -= solveCurrentTurnRate();
                }
                // if the car should be turning right
                if (turningRight) {
                    // increment the degree
                    angleDeg += solveCurrentTurnRate();
                }
            }
        }
        // else decay the turn lock timer
        else turnLockDecay();

        // if the car should be slipping
        if (oilSlipTime != 0){
            // force the angle of the car depending on the slip direction
            angleDeg += 2 * slipDirection;
            oilSlipDecay();
        }

        // if the car should be accelerating
        if (accelerating){
            if (currentSpeed < currentTopSpeed) {
                if (currentSpeed > 0)
                    currentSpeed += accelerationRate;
                else // just makes it seem more realistic when he car is rebounding
                    currentSpeed += accelerationRate * 6;
            }
        } else {
            if(currentSpeed > 0){
                currentSpeed -= decelerationRate;
            } else if (currentSpeed < -(decelerationRate*4 + 0.2)){
                currentSpeed += decelerationRate * 4;
            } else {
                currentSpeed = 0;
            }
        }
        // if the car should be breaking
        if (breaking){
            if(currentSpeed > 0){
                // decrement the current speed
                currentSpeed -= (decelerationRate * 2) - (currentSpeed * 0.002);
            } else {
                // increment the current speed
                currentSpeed += (decelerationRate * 2);
            }
        }
        // check the track surface
        trackSurfacePenalties();
        // check the current speed is not greater than the top speed
        if(currentSpeed > currentTopSpeed){
            currentSpeed -= decelerationRate*5;
        }
    }

    public void turnLockDecay(){
        // an update clock for locking the car's turning
        turnLockTime -= 1;
        if(turnLockTime < 0)
            turnLockTime = 0;
        else if(turnLockTime > 200){
            turnLockTime = 200;
        }
    }
    
    public void collisionVoidDecay(){
        // an update clock for voiding the car's collisions
        collisionVoidTime -= 1;
        if(collisionVoidTime < 0)
            collisionVoidTime = 0;
        else if(collisionVoidTime > 20){
            collisionVoidTime = 20;
        }
    }

    public void oilSlipDecay(){
        // an update clock for locking the car's slipping
        oilSlipTime -= 1;
        if(oilSlipTime < 0)
            oilSlipTime = 0;
        else if(oilSlipTime > 20){
            oilSlipTime = 20;
        }
    }
}
