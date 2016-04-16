package jifa.racecarsandstuff;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.Random;

public class AICar extends Car{
    private int pointIndex;
    public double desiredAngle;
    private Random rand = new Random();

    public AICar(View view, World world, int x, int y, int[][] points, int id){
        super(view, world, id, points);
        xPos = x;
        yPos = y;
        angleDeg = -90;
        pointIndex = 1; // skip the start line point

//        Beginner
//        trackTopSpeed = 12 + rand.nextInt(5);
//        grassTopSpeed = trackTopSpeed/4;
//        turningRate = 0.9 + rand.nextInt(7) * 0.1;
//        accelerationRate = 0.02 + rand.nextInt(8) * 0.02;

//        Regular
        trackTopSpeed = 14 + rand.nextInt(5);
        grassTopSpeed = trackTopSpeed/4;
        turningRate = 1.5 + rand.nextInt(7) * 0.1;
        accelerationRate = 0.04 + rand.nextInt(7) * 0.02;

//        Pro
//        trackTopSpeed = 16 + rand.nextInt(3);
//        grassTopSpeed = trackTopSpeed/4;
//        turningRate = 1.6 + rand.nextInt(5) * 0.1;
//        accelerationRate = 0.11 + rand.nextInt(5) * 0.02;

//        Impossible
//        trackTopSpeed = 17 + rand.nextInt(3);
//        grassTopSpeed = 5;
//        turningRate = 2 + (rand.nextInt(5) * 0.1);
//        accelerationRate = 0.15 + (rand.nextInt(5) * 0.1);

        String[] colours = {"blue", "red", "green_white", "flame", "blue_red",
                            "pokemon", "black", "herbie"};
        loadCar(imageCanv, colours[rand.nextInt(colours.length)]);
    }

    public double getAngleTo(int x1, int y1, int x2, int y2){
        return Math.atan2(y2 - y1, x2 - x1);
    }

    public void directAngleToTarget(){
        int tx = points[pointIndex][0]*10*world.scale;
        int ty = points[pointIndex][1]*10*world.scale;

        if(angleDeg > 360) angleDeg -= 360;
        else if (angleDeg < 0) angleDeg += 360;

        desiredAngle = Math.toDegrees(getAngleTo((int)xPos, (int)yPos, tx, ty));

        int smallestAngle = solveSmallestAngle((int)desiredAngle, (int)angleDeg);

        int tolerance = 5;
        if ((int)angleDeg > angleDeg + smallestAngle + tolerance || (int)angleDeg < angleDeg + smallestAngle - tolerance){
            if ((int)angleDeg < angleDeg + smallestAngle)
                turningRight = true;
            else turningLeft = true;
        }
    }

    public int solveSmallestAngle(int targetAngle, int sourceAngle){
        int smallestAngle = targetAngle - sourceAngle;
        smallestAngle += (smallestAngle>180) ? -360 : (smallestAngle<-180) ? 360 : 0;
        return smallestAngle;
    }

    public boolean collidingInnerTarget(){
        int tx = points[pointIndex][0] * 10 * world.scale;
        int ty = points[pointIndex][1] * 10 * world.scale;
        int range = points[pointIndex][2] * 10 * world.scale;
        if (xPos < tx + range && xPos > tx - range){
            if(yPos < ty + range && yPos > ty - range){
                return true;
            }
        }
        return false;
    }

    public boolean collidingOuterTarget(){
        int tx = points[pointIndex][0] * 10 * world.scale;
        int ty = points[pointIndex][1] * 10 * world.scale;
        int range = points[pointIndex][2] * 10 * world.scale * 2;
        if (xPos < tx + range && xPos > tx - range){
            if(yPos < ty + range && yPos > ty - range){
                return true;
            }
        }
        return false;
    }

    public void draw(Canvas canvas){
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate((int)xPos - indWidth * 8 + (int) world.translateX, (int)yPos - indHeight * 8 + (int) world.translateY);
        canvas.scale(world.scale, world.scale);
        canvas.rotate((int) angleDeg + 90, width / 4, height / 4);
        canvas.drawBitmap(image, null, scaleRect, null);
        canvas.restore();
    }

    public void update(){
        super.update();
        accelerating = true;
        breaking = false;
        turningLeft = false;
        turningRight = false;
        if (collidingOuterTarget()){
            if (pointIndex != 0) {
                if (currentSpeed > 8 * turningRate) {
                    accelerating = false;
                    if (currentSpeed > 10.6 * turningRate) {
                        decelerationRate *= 1.01;
                    }
                }
            }
        }
        if (collidingInnerTarget()){
            if(pointIndex == 0){
                lapCount++;
            }
            pointIndex++;
            if (pointIndex > points.length - 1){
                pointIndex = 0;
            }
        }
        directAngleToTarget();
        dx = currentSpeed * Math.cos(Math.toRadians(angleDeg));
        dy = currentSpeed * Math.sin(Math.toRadians(angleDeg));
        xPos += dx;
        yPos += dy;
    }

    public int readTrack(){
        return world.track.colourImage.getPixel((int)xPos / world.scale, (int)yPos / world.scale);
    }
}
