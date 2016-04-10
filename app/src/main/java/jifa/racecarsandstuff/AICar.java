package jifa.racecarsandstuff;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.Random;

public class AICar extends Car{
    private int[][] points;
    private int pointIndex;
    public double desiredAngle;
    private Random rand = new Random();

    public AICar(View view, World world, int x, int y, int[][] points, int id){
        super(view, world, id);
        xPos = x;
        yPos = y;
        angleDeg = -90;
        this.points = points;
        pointIndex = 1; // skip the start line point

        trackTopSpeed = 16 + rand.nextInt(5);
        grassTopSpeed = trackTopSpeed/4;
        turningRate = 1.5 + rand.nextInt(5) * 0.1;
        accelerationRate = 0.1 + rand.nextInt(5) * 0.02;

//        PRO SETTINGS
//        trackTopSpeed = 19 + rand.nextInt(3);
//        grassTopSpeed = 5;
//        turningRate = 2 + (rand.nextInt(5) * 0.1);
//        accelerationRate = 0.15 + (rand.nextInt(5) * 0.1);
//        currentTopSpeed = trackTopSpeed;

        String[] colours = {"blue", "red", "purple", "green", "yellow", "white"};
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

        desiredAngle = Math.toDegrees(getAngleTo(xPos, yPos, tx, ty));

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
        canvas.translate(xPos - indWidth * 8 + (int) world.translateX, yPos - indHeight * 8 + (int) world.translateY);
        canvas.scale(9, 9);
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
            if (currentSpeed > 8 * turningRate){
                accelerating = false;
                if (currentSpeed > 10.6 * turningRate){
                    decelerationRate *= 1.01;
                }
            }
        }
        if (collidingInnerTarget()){
            pointIndex++;
            if (pointIndex > points.length - 1){
                pointIndex = 1;
            }
        }
        directAngleToTarget();
        dx = currentSpeed * Math.cos(Math.toRadians(angleDeg));
        dy = currentSpeed * Math.sin(Math.toRadians(angleDeg));
        xPos += dx;
        yPos += dy;
    }

    public int readTrack(){
        return world.track.colourImage.getPixel(xPos / world.scale, yPos / world.scale);
    }
}
