package jifa.racecarsandstuff;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

    public Track(View view){

        char [][] array = {
                {'g', 'g', 'g', 'g', 'g'},
                {'g', 'a', 't', 't', 'g'},
                {'g', 't', 'g', 'g', 'g'},
                {'g', 't', 'g', 'g', 'g'},
                {'g', 'g', 'g', 'g', 'g'}};

        int count = array.length;

        Bitmap grass = BitmapFactory.decodeResource(view.getResources(), R.drawable.grass);
        Bitmap turn = BitmapFactory.decodeResource(view.getResources(), R.drawable.turn);
        Bitmap tr = BitmapFactory.decodeResource(view.getResources(), R.drawable.track);

        int indWidth = grass.getWidth();
        int indHeight = grass.getHeight();

        image = Bitmap.createBitmap(indWidth*count, indHeight*count, Bitmap.Config.RGB_565);
        Canvas imageCanv = new Canvas(image);

        for(int x = 0; x < count; x++){
            for(int y = 0; y < count; y++) {
                switch(array[x][y]){
                    case 'g':
                        imageCanv.drawBitmap(grass, indWidth*x, indHeight*y, null);
                        break;
                    case 't':
                        imageCanv.drawBitmap(tr, indWidth*x, indHeight*y, null);
                        break;
                    case 'a':
                        imageCanv.drawBitmap(turn, indWidth*x, indHeight*y, null);
                        break;
                }
            }
        }
    }

    private String readTxt(View view){

        InputStream inputStream = view.getResources().openRawResource(R.raw.track1);
        System.out.println(inputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            int i = inputStream.read();
            while (i != -1){
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(image, 0, 0, null);
    }
}
