package jifa.racecarsandstuff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public abstract class Car {
    public Bitmap graphics;
    public Rect scaleRect;
    public Bitmap image;
    public World world;
    double dx, dy, xPos, yPos;
    public int width, height, indWidth, indHeight, damage;
    public double currentSpeed = 0;
    public int currentTopSpeed, trackTopSpeed, grassTopSpeed, oilTopSpeed;
    public double accelerationRate;
    public double decelerationRate;
    public double angleDeg;
    public double turningRate;
    public boolean turningLeft, turningRight, accelerating, breaking;
    public int turnLockTime;
    public int id;
    public int lapCount;
    protected Canvas imageCanv;
    public int collisionRange;
    public int collisionVoidTime;
    public int[][] points;

    public Car(View view, World world, int id, int[][]points){
        xPos = -200;
        yPos = 0;
        dx = 0;
        dy = 0;
        this.id = id;
        this.points = points;
        turnLockTime = 0;
        breaking = false;
        turningLeft = false;
        turningRight = false;
        accelerating = false;
        oilTopSpeed = 4;
        collisionVoidTime = 0;
        damage = 0;
        this.world = world;
        decelerationRate = 0.08;
        lapCount = 0;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inScaled = false;
        graphics = BitmapFactory.decodeResource(view.getResources(), R.drawable.graphics, options);

        indWidth = graphics.getWidth() / 8;
        indHeight = graphics.getHeight() / 8;

        scaleRect = new Rect(0, 0, indWidth*4, indHeight*4);
        image = Bitmap.createBitmap(indWidth * 4, indHeight * 4, Bitmap.Config.ARGB_4444);
        imageCanv = new Canvas(image);
        width = image.getWidth();
        height = image.getHeight();
        collisionRange = height;
    }

    public double solveCurrentTurnRate(){
        return turningRate - (currentSpeed * 0.01);
    }

    public void loadCar(Canvas imageCanv, String style){
        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        paint.setDither(false);

        int col = 0, row = 0;

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

        for(int x = 0; x < 2; x++){
            for(int y = 0; y < 2; y++) {
                Rect rect = new Rect(indWidth*y, indHeight*x, indWidth*y + indWidth, indHeight*x + indHeight);
                Rect rect1 = new Rect(  y*indWidth+(indWidth*4) + (indWidth * col),
                        x*indHeight+(indWidth * row) + (indHeight * row),
                        y*indWidth+(indWidth*4) + (indWidth * (col + 1)),
                        x*indHeight+(indHeight * row) + (indHeight * (row + 1)));
                imageCanv.drawBitmap(graphics, rect1, rect, paint);
            }
        }
    }

    public void trackSurfacePenalties(){
        int pixel = readTrack();
        if (Color.blue(pixel) == 255){
            if(currentTopSpeed != trackTopSpeed) {
                currentTopSpeed = trackTopSpeed;
            }
        } else if(Color.green(pixel) == 255){
            if(currentTopSpeed != grassTopSpeed) {
                currentTopSpeed = grassTopSpeed;
            }
        } else if(Color.red(pixel) == 255){
            // car is on track edge
        } else if(Color.red(pixel) == 0){
            damage += (currentSpeed / 5);
            if (damage > 20) damage = 20;
            currentSpeed += 3;
            currentSpeed = -currentSpeed;
            turnLockTime += 10;
        } else {
            if (currentTopSpeed != oilTopSpeed) {
                currentTopSpeed = oilTopSpeed;
            }
        }
    }

    public void checkCollides(){
        int x1 = (int)xPos + (int) world.translateX + 7;
        int y1 = (int)yPos + (int) world.translateY + 7;
        for(int i = 0; i < world.carList.size(); i++){
            if(world.carList.get(i).id != id) {
                int x2 = (int)world.carList.get(i).xPos + (int) world.translateX + 7;
                int y2 = (int)world.carList.get(i).yPos + (int) world.translateY + 7;
                if (Math.pow((double) (x1 - x2), 2) + Math.pow((double) (y1 - y2), 2) < Math.pow((double) height * 2, 2)) {
                    damage += 1;
                    collisionVoidTime += 10;
                    world.carList.get(i).collisionVoidTime += 10;
                }
            }
        }
    }

    public int readTrack(){
        return 0;
    }

    public void draw(Canvas canvas){

    }

    public void update(){
        collisionVoidDecay();
        if(collisionVoidTime == 0) {
            checkCollides();
        }
        turnLockDecay();
        if(turnLockTime == 0) {
            if (currentSpeed > 1 || currentSpeed < -1) {
                if (turningLeft) {
                    angleDeg -= solveCurrentTurnRate();
                }
                if (turningRight) {
                    angleDeg += solveCurrentTurnRate();
                }
            }
        }
        if (accelerating){
            if (currentSpeed < currentTopSpeed) {
                if (currentSpeed > 0)
                    currentSpeed += accelerationRate;
                else // just makes it seem better when reversing
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
        if (breaking){
            if(currentSpeed > 0){
                currentSpeed -= (decelerationRate * 2) - (currentSpeed * 0.002);
            } else {
                currentSpeed += (decelerationRate * 2);
            }
        }
        trackSurfacePenalties();
        if(currentSpeed > currentTopSpeed){
            currentSpeed -= decelerationRate*5;
            if(currentTopSpeed == oilTopSpeed){
                currentSpeed -= decelerationRate*5;
            }
        }
    }

    public void turnLockDecay(){
        turnLockTime -= 1;
        if(turnLockTime < 0)
            turnLockTime = 0;
        else if(turnLockTime > 200){
            turnLockTime = 200;
        }
    }
    
    public void collisionVoidDecay(){
        collisionVoidTime -= 1;
        if(collisionVoidTime < 0)
            collisionVoidTime = 0;
        else if(collisionVoidTime > 20){
            collisionVoidTime = 20;
        }
    }
}
