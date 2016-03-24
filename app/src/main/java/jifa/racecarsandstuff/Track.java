package jifa.racecarsandstuff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

public class Track {
    Bitmap image;

    public Track(View view){
        Bitmap grass = BitmapFactory.decodeResource(view.getResources(), R.drawable.grass);

        int indWidth = grass.getWidth();
        int indHeight = grass.getHeight();

        int count = 30;

        image = Bitmap.createBitmap(indWidth*count, indHeight*count, Bitmap.Config.RGB_565);
        Canvas imageCanv = new Canvas(image);

        for(int x = 0; x < count; x++){
            for(int y = 0; y < count; y++) {
                imageCanv.drawBitmap(grass, indWidth*x, indHeight*y, null);
            }
        }
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(image, 0, 0, null);
    }
}
