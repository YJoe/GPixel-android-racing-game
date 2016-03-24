package jifa.racecarsandstuff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

/**
 * Created by Joe on 14/03/2016.
 */
public class Car {
    public Bitmap image;
    public int xPos, yPos, width, height, angleDeg;

    public Car(View view){
        image = BitmapFactory.decodeResource(view.getResources(), R.drawable.car);
        xPos = 300;
        yPos = 300;
        angleDeg = 0;

        width = image.getWidth();
        height = image.getHeight();

        float scaleWidth = 0.25f;
        float scaleHeight = 0.25f;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        image = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
        width = image.getWidth();
        height = image.getHeight();
    }

    public void draw(Canvas canvas){
        Matrix transform = new Matrix();
        transform.setTranslate(xPos, yPos);
        transform.preRotate(angleDeg, width/2, height/2);
        canvas.drawBitmap(image, transform, null);
    }
}
