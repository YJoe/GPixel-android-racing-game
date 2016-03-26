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
import java.util.Random;

public class Track {
    Bitmap image;
    Rect scaleRect;
    int translateX = 0;
    int translateY = 0;
    Bitmap graphics;
    ArrayList<ArrayList<Rect>> graphicSpaces = new ArrayList<>();

    public Track(View view){
        String [][] array = {
                {"gra", "gra", "gra", "gra", "gra", "gra", "gra"},
                {"gra", "rco", "trt", "trt", "trt", "trt", "trt"},
                {"gra", "trl", "tra", "tra", "tra", "tra", "tra"},
                {"gra", "trl", "tra", "rci", "trb", "trb", "trb"},
                {"gra", "trl", "tra", "trr", "gra", "gra", "gra"},
                {"gra", "trl", "tra", "trr", "gra", "gra", "gra"},
                {"gra", "trl", "tra", "trr", "gra", "gra", "gra"}};

        int count = array.length;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inScaled = false;

        graphics = BitmapFactory.decodeResource(view.getResources(), R.drawable.graphics, options);
        // Define graphic spaces

        graphicSpaces.add(new ArrayList<Rect>());
        graphicSpaces.add(new ArrayList<Rect>());
        graphicSpaces.add(new ArrayList<Rect>());
        graphicSpaces.add(new ArrayList<Rect>());

        // grass
        graphicSpaces.get(0).add(new Rect(0,  0, 10, 10));
        graphicSpaces.get(0).add(new Rect(10, 0, 20, 10));
        graphicSpaces.get(0).add(new Rect(20, 0, 30, 10));
        // track edges
        graphicSpaces.get(1).add(new Rect(0,  10, 10, 20));
        graphicSpaces.get(1).add(new Rect(10, 10, 20, 20));
        graphicSpaces.get(1).add(new Rect(20, 10, 30, 20));
        graphicSpaces.get(1).add(new Rect(30, 10, 40, 20));
        // track
        graphicSpaces.get(2).add(new Rect(0,  20, 10, 30));
        graphicSpaces.get(2).add(new Rect(10, 20, 20, 30));
        // curves
        graphicSpaces.get(3).add(new Rect(0,  30, 10, 40));
        graphicSpaces.get(3).add(new Rect(10, 30, 20, 40));


        int indWidth = graphics.getWidth() / 6;
        int indHeight = graphics.getHeight() / 6;

        scaleRect = new Rect(0, 0, 10*count, 10*count);
        image = Bitmap.createBitmap(indWidth*count, indHeight*count, Bitmap.Config.RGB_565);
        Canvas imageCanv = new Canvas(image);

        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        paint.setDither(false);

        count = 2;

        for(int x = 0; x < count; x++){
            for(int y = 0; y < count; y++) {
                Rect rect = new Rect(indWidth * y, indHeight * x, indWidth*y + indWidth, indHeight * x + indHeight);
                getTexture(array[x][y], imageCanv, rect, paint);
            }
        }
    }

    public void getTexture(String str, Canvas canvas, Rect rect, Paint paint){
        int rowIndex = 0;
        switch(str){
            case "gra": rowIndex = 0;
                break;
            case "tra": rowIndex = 2;
                break;
            case "trl": canvas.drawBitmap(graphics, graphicSpaces.get(1).get(0), rect, paint);
                return;
            case "trt": canvas.drawBitmap(graphics, graphicSpaces.get(1).get(1), rect, paint);
                return;
            case "trr": canvas.drawBitmap(graphics, graphicSpaces.get(1).get(2), rect, paint);
                return;
            case "trb": canvas.drawBitmap(graphics, graphicSpaces.get(1).get(3), rect, paint);
                return;
            case "rco": canvas.drawBitmap(graphics, graphicSpaces.get(3).get(0), rect, paint);
                return;
            case "rci": canvas.drawBitmap(graphics, graphicSpaces.get(3).get(1), rect, paint);
                return;
        }
        int colIndex = new Random().nextInt(graphicSpaces.get(rowIndex).size());
        canvas.drawBitmap(graphics, graphicSpaces.get(rowIndex).get(colIndex), rect, paint);
    }

    public void draw(Canvas canvas){
        canvas.translate(translateX, translateY);
        canvas.scale(10, 10);
        canvas.drawBitmap(image, null, scaleRect, null);
    }
}
