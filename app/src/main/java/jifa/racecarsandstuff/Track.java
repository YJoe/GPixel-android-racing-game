package jifa.racecarsandstuff;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Track {
    Bitmap image;
    Rect scaleRect;
    int translateX = 0;
    int translateY = 0;

    public Track(View view){
        char [][] array = {
                {'g', 'g', 'g', 'g', 'g'},
                {'g', 'a', 't', 't', 'g'},
                {'g', 't', 'g', 'g', 'g'},
                {'g', 't', 'g', 'g', 'g'},
                {'g', 'g', 'g', 'g', 'g'}};

        int count = array.length;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inScaled = false;

        Bitmap grass = BitmapFactory.decodeResource(view.getResources(), R.drawable.grass, options);
        Bitmap turn = BitmapFactory.decodeResource(view.getResources(), R.drawable.turn, options);
        Bitmap tr = BitmapFactory.decodeResource(view.getResources(), R.drawable.track, options);

        int indWidth = grass.getWidth();
        int indHeight = grass.getHeight();

        count = 100;

        scaleRect = new Rect(0, 0, 10*count, 10*count);
        image = Bitmap.createBitmap(indWidth*count, indHeight*count, Bitmap.Config.RGB_565);
        Canvas imageCanv = new Canvas(image);

        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        paint.setDither(false);

        for(int x = 0; x < count; x++){
            for(int y = 0; y < count; y++) {
                Rect rect = new Rect(indWidth * x, indHeight * y, indWidth*x + indWidth, indHeight * y + indHeight);
                imageCanv.drawBitmap(grass, null, rect, paint);
//                switch(array[x][y]){
//                    case 'g':
//                        imageCanv.drawBitmap(grass, indWidth*x, indHeight*y, null);
//                        break;
//                    case 't':
//                        imageCanv.drawBitmap(tr, indWidth*x, indHeight*y, null);
//                        break;
//                    case 'a':
//                        imageCanv.drawBitmap(turn, indWidth*x, indHeight*y, null);
//                        break;
//                }
            }
        }
    }

    public void draw(Canvas canvas){
        canvas.translate(translateX, translateY);
        canvas.scale(10, 10);
        canvas.drawBitmap(image, null, scaleRect, null);
    }
}
