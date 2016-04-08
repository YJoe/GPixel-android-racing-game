package jifa.racecarsandstuff;

import android.graphics.Canvas;
import android.view.View;

public class AICar extends Car{
    private int[][] points;
    private int pointIndex;
    public double dx, dy, desiredAngle;

    public AICar(View view, World world, int x, int y, int[][] points){
        super(view);
        xPos = x;
        yPos = y;
        dx = 0;
        dy = 0;
        angleDeg = -90;
        this.world = world;
        this.points = points;
        pointIndex = 1; // skip the start line point
        currentTopSpeed = 20;

        for(int i = 0; i < points.length; i++){
            points[i][0] += points[i][2] /2;
            points[i][1] += points[i][2] /2;
        }
    }

    public void trackSurfacePenalties(){

    }

    public void update(){
        super.update();
        accelerating = true;
        breaking = false;
        turningLeft = false;
        turningRight = false;
        if (collidingOuterTarget()){
            if (currentSpeed > 12){
                accelerating = false;
                if (currentSpeed > 16){
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
}
