package jifa.racecarsandstuff;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.Random;

public class AICar extends Car{
    // For keeping track of the next checkpoint needed to complete the race
    private int pointIndex;
    // The angle needed to turn in order to reach the next checkpoint
    public double desiredAngle;

    public AICar(View view, World world, int x, int y, int[][] points, int id, Options options){
        // Call the super constructor of a regular Car object
        super(view, world, id, points, options);

        // set x and y coordinates
        xPos = x;
        yPos = y;
        angleDeg = -90;

        // skip the start line point
        pointIndex = 1;

        // Pick random speed, acceleration and turning angle attributes
        Random rand = new Random();
        trackTopSpeed = 14 + rand.nextInt(5);
        grassTopSpeed = trackTopSpeed/4;
        turningRate = 1.5 + rand.nextInt(7) * 0.1;
        accelerationRate = 0.04 + rand.nextInt(7) * 0.02;

        // define the possible colour choices
        String[] colours = {"blue", "red", "green", "flame", "toothpaste",
                            "pokemon", "black", "herbie"};

        // load a random colour for the car
        loadCar(imageCanv, colours[rand.nextInt(colours.length)]);
    }

    public double getAngleTo(int x1, int y1, int x2, int y2){
        // return the arc tangent of the difference between the two coordinates
        return Math.atan2(y2 - y1, x2 - x1);
    }

    public void directAngleToTarget(){
        // get the next checkpoint's target x and target y
        int tx = points[pointIndex][0]*10*world.scale;
        int ty = points[pointIndex][1]*10*world.scale;

        // truncate the current car angle between 0 and 360
        if(angleDeg > 360) angleDeg -= 360;
        else if (angleDeg < 0) angleDeg += 360;

        // solve the angle needed to aim the car towards the chackpoint
        desiredAngle = Math.toDegrees(getAngleTo((int)xPos, (int)yPos, tx, ty));

        // solve the smallest angle needed to turn to obtain the desired angle
        int smallestAngle = solveSmallestAngle((int)desiredAngle, (int)angleDeg);

        // set an angle range which will be close enough to the desired angle
        int tolerance = 5;

        // if the angle is not within the tolerance range
        if ((int)angleDeg > angleDeg + smallestAngle + tolerance || (int)angleDeg < angleDeg + smallestAngle - tolerance){
            // if the angle is smaller by incrementing clockwise
            if ((int)angleDeg < angleDeg + smallestAngle)
                // increment the angle clockwise on the next update
                turningRight = true;
            else
                // increment the angle anti-clockwise on the next update
                turningLeft = true;
        }
    }

    public int solveSmallestAngle(int targetAngle, int sourceAngle){
        // a function to solve the smallest difference in degree angle between a source and target angle
        int smallestAngle = targetAngle - sourceAngle;
        smallestAngle += (smallestAngle>180) ? -360 : (smallestAngle<-180) ? 360 : 0;
        return smallestAngle;
    }

    public boolean collidingInnerTarget(){
        // get the targets of the next checkpoint
        int tx = points[pointIndex][0] * 10 * world.scale;
        int ty = points[pointIndex][1] * 10 * world.scale;

        // set a small range in which the car must collide before progressing
        int range = points[pointIndex][2] * 10 * world.scale;

        // if the car is within the range
        if (xPos < tx + range && xPos > tx - range){
            if(yPos < ty + range && yPos > ty - range){
                return true;
            }
        }
        return false;
    }

    public boolean collidingOuterTarget(){
        // get the targets of the next checkpoints
        int tx = points[pointIndex][0] * 10 * world.scale;
        int ty = points[pointIndex][1] * 10 * world.scale;

        // set a larger range in which the car must collide to start breaking
        int range = points[pointIndex][2] * 10 * world.scale * 2;
        if (xPos < tx + range && xPos > tx - range){
            if(yPos < ty + range && yPos > ty - range){
                return true;
            }
        }
        return false;
    }

    public void draw(Canvas canvas){
        // save the canvas matrix
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate((int)xPos - indWidth * 8 + (int) world.translateX, (int)yPos - indHeight * 8 + (int) world.translateY);
        canvas.scale(world.scale, world.scale);
        canvas.rotate((int) angleDeg + 90, width / 4, height / 4);
        canvas.drawBitmap(image, null, scaleRect, null);
        // restore the canvas matrix
        canvas.restore();
    }

    public void update(){
        // do a regular car update
        super.update();

        // set all parameters to false other than accelerating
        accelerating = true;
        breaking = false;
        turningLeft = false;
        turningRight = false;

        // if the car is getting close to the checkpoint
        if (collidingOuterTarget()){
            // if the check point is not the starting line
            if (pointIndex != 0) {
                // if the car is going too fast to make the corner without breakings
                if (currentSpeed > 8 * turningRate) {
                    // stop accelerating
                    accelerating = false;
                    // if the car is going way too fast to make the corner, give the car a
                    // better deceleration rate in order to get around corners easier
                    if (currentSpeed > 10.6 * turningRate) {
                        decelerationRate *= 1.01;
                    }
                }
            }
        }

        // if the car is colliding with the checkpoint
        if (collidingInnerTarget()){
            // if the checkpoint is the start line
            if(pointIndex == 0){
                // add to the lap counter
                lapCount++;
            }
            // add to the check point index
            pointIndex++;
            // if the point index is higher than the maximum point index
            if (pointIndex > points.length - 1){
                // set the next checkpoint to the start line
                pointIndex = 0;
            }
        }
        // direct the car's angle to the nearest checkpoint
        directAngleToTarget();
        // move the cars x and y coordinates
        dx = currentSpeed * Math.cos(Math.toRadians(angleDeg));
        dy = currentSpeed * Math.sin(Math.toRadians(angleDeg));
        xPos += dx;
        yPos += dy;
    }

    public int readTrack(){
        // read the track colour
        return world.track.colourImage.getPixel((int)xPos / world.scale, (int)yPos / world.scale);
    }
}
