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
    float translateX = 0;
    float translateY = 0;
    double dx, dy;
    int angle = 0;
    Bitmap graphics;
    ArrayList<ArrayList<Rect>> graphicSpaces = new ArrayList<>();

    public Track(View view){
        dx = 0; dy = 0;

        String [][] track = new String[50][50];
        for(int i = 0; i < 50; i++){
            for (int j = 0; j < 50; j++){
                track[i][j] = "grass";
            }
        }

        int [][] points = { {5, 5}, {40, 5}, {40, 14}, {20, 14}, {20, 30}, {29, 30},
                            {29, 22}, {38, 22}, {38, 40}, {5, 40}};
        formTrack(track, points, true);
        formEdges(track);

        // entirely assumes the array will be square
        int count = track.length;

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
                getTexture(track[x][y], imageCanv, rect, paint);
                //imageCanv.drawBitmap(graphics, graphicSpaces.get(1).get(1), rect, paint);
            }
        }
    }

    public void formTrack(String[][]track, int[][]p, boolean link){
        for(int i = 0; i < p.length - 1; i++){
            drawTrackLine(track, p[i][0], p[i][1], p[i+1][0], p[i+1][1], 5);
        }
        if(link)
            drawTrackLine(track, p[p.length - 1][0], p[p.length - 1][1], p[0][0], p[0][1], 5);
    }

    public void formEdges(String[][]track){
        int gCount, tCount;
        for(int i = 0; i < track.length - 2; i++){
            for(int j = 0; j < track.length - 2; j++){
                gCount = 0; tCount = 0;
                // sample
                for(int k = 0; k < 2; k++){
                    for(int l = 0; l < 2; l++){
                        if (track[j+k][i+l].equals("track"))
                            tCount++;
                        else
                            gCount++;
                    }
                }
                if (gCount == tCount) {
                    if (track[j][i].equals("track") && track[j+1][i].equals("track")){
                        track[j][i+1] = "ed180";
                        track[j+1][i+1] = "ed180";
                    } else if (track[j][i].equals("track") && track[j][i+1].equals("track")){
                        track[j+1][i] = "ed270";
                        track[j+1][i+1] = "ed270";
                    } else if (track[j][i+1].equals("track") && track[j+1][i+1].equals("track")){
                        track[j][i] = "ed000";
                        track[j+1][i] = "ed000";
                    } else {
                        track[j][i] = "ed090";
                        track[j][i+1] = "ed090";
                    }
                } else if (gCount > tCount && tCount != 0){
                    // an inner corner
                } else if (gCount < tCount && gCount != 0){
                }
            }
        }
    }

    public void drawTrackLine(String[][]track, int x,int y,int x2, int y2, int stroke) {
        int w = x2 - x ;
        int h = y2 - y ;
        int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
        if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
        if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
        if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
        int longest = Math.abs(w) ;
        int shortest = Math.abs(h) ;
        if (!(longest>shortest)) {
            longest = Math.abs(h) ;
            shortest = Math.abs(w) ;
            if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
            dx2 = 0 ;
        }
        int numerator = longest >> 1 ;
        for (int i=0;i<=longest;i++) {
            for(int k = 0; k < stroke; k++){
                for(int l = 0; l < stroke; l++){
                    track[y + k][x + l] = "track";
                }
            }
            numerator += shortest ;
            if (!(numerator<longest)) {
                numerator -= longest ;
                x += dx1 ;
                y += dy1 ;
            } else {
                x += dx2 ;
                y += dy2 ;
            }
        }
    }

    public void drawToTrack(String[][] track, int startX, int endX, int startY, int endY, String tag){
        for(int x = startX; x < endX; x++){
            for(int y = startY; y < endY; y++){
                track[y][x] = tag;
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

    public void update(){
        translateX += dx;
        translateY += dy;
    }

    public void draw(Canvas canvas){
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(translateX, translateY);
        canvas.scale(10, 10);
        canvas.drawBitmap(image, null, scaleRect, null);
        canvas.restore();
    }
}