package jifa.racecarsandstuff;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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
    int angle = 0;
    Bitmap graphics;
    ArrayList<ArrayList<Rect>> graphicSpaces = new ArrayList<>();

    public Track(View view){
        String [][] track3 = {
                {"grass", "ed000", "track", "cs090", "cb090", "grass", "grass"},
                {"grass", "cb270", "cs270", "track", "ed180", "grass", "grass"},
                {"grass", "grass", "ed000", "track", "ed180", "grass", "grass"},
                {"grass", "cb000", "cs000", "track", "ed180", "grass", "grass"},
                {"ed090", "cs000", "cs180", "ed270", "cb180", "grass", "grass"},
                {"track", "track", "ed180", "grass", "grass", "grass", "grass"},
                {"ed270", "ed270", "cb180", "grass", "grass", "grass", "grass"},
        };

        String [][] blank = new String[50][50];
        for(int i = 0; i < 50; i++){
            for (int j = 0; j < 50; j++){
                blank[i][j] = "track";
            }
        }

        String[][] array = blank;

        // entirely assumes the array will be square
        int count = array.length;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inScaled = false;

        graphics = BitmapFactory.decodeResource(view.getResources(), R.drawable.graphics, options);

        // Define graphic spaces
        for(int y = 0; y < 5; y++){
            graphicSpaces.add(new ArrayList<Rect>());
            for(int x = 0; x < 4; x++){
                graphicSpaces.get(y).add(new Rect((x*10), (y*10), (x*10)+10, (y*10)+10));
            }
        }

        int indWidth = graphics.getWidth() / 8;
        int indHeight = graphics.getHeight() / 6;

        scaleRect = new Rect(0, 0, indWidth*count, indHeight*count);
        image = Bitmap.createBitmap(indWidth*count, indHeight*count, Bitmap.Config.RGB_565);
        Canvas imageCanv = new Canvas(image);

        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        paint.setDither(false);

        for(int x = 0; x < count; x++){
            for(int y = 0; y < count; y++) {
                Rect rect = new Rect(indWidth * y, indHeight * x, indWidth*y + indWidth, indHeight * x + indHeight);
                getTexture(array[x][y], imageCanv, rect, paint);
                //imageCanv.drawBitmap(graphics, graphicSpaces.get(1).get(1), rect, paint);
            }
        }
    }

    public void getTexture(String str, Canvas canvas, Rect rect, Paint paint){
        int rowIndex = 0, colIndex = 0;
        if (Character.isDigit(str.charAt(str.length() - 1))){
            switch(str.substring(0, 2)){
                case "ed": rowIndex = 1; break;
                case "cb": rowIndex = 2; break;
                case "cs": rowIndex = 3; break;
            }
            colIndex = Integer.parseInt(str.substring(2, 5)) / 90;
        } else {
            Random rand = new Random();
            switch(str){
                case "grass": rowIndex = 0; colIndex = rand.nextInt(3); break;
                case "track": rowIndex = 4; colIndex = rand.nextInt(3); break;
            }
        }
        canvas.drawBitmap(graphics, graphicSpaces.get(rowIndex).get(colIndex), rect, paint);

    }

    public void draw(Canvas canvas){
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(translateX, translateY);
        canvas.scale(10, 10);
        canvas.drawBitmap(image, null, scaleRect, null);
        canvas.restore();
    }
}
