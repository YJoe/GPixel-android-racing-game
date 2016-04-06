package jifa.racecarsandstuff;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

public class Track {
    public Bitmap image;
    public Bitmap colourImage;
    public Rect scaleRect;
    public float translateX;
    public float translateY;
    public Bitmap graphics;
    public Bitmap colourGraphics;
    public ArrayList<ArrayList<Rect>> graphicSpaces;
    public int scale;

    public Track(View view, int[][] points){
        // set the scale at which to print the track
        scale = 9;
        // define an array list for graphic spaces
        graphicSpaces = new ArrayList<>();

        // define a string to hold the string representation of the track
        String [][] track = new String[50][50];

        // fill the entire track with grass
        drawTrackSection(track, 0, 0, track.length, track.length, "grass");

        // create lines of tracks from the points defined
        formTrack(track, points, true);
        // create track edges (just the straights)
        formStraightEdges(track);
        // create the track corner edges
        formCornerEdges(track);
        // create a ring of tires surrounding the world
        formWorldBorders(track);
        // form all of the tires
        formTires(track);
        // draw the start line
        formStart(track, points);

        // entirely assumes the array will be square
        int count = track.length;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inScaled = false;

        graphics = BitmapFactory.decodeResource(view.getResources(), R.drawable.graphics, options);
        colourGraphics = BitmapFactory.decodeResource(view.getResources(), R.drawable.colour_graphics, options);

        // Define graphic spaces
        for(int y = 0; y < 6; y++){
            graphicSpaces.add(new ArrayList<Rect>());
            for(int x = 0; x < 8; x++){
                graphicSpaces.get(y).add(new Rect((x*10), (y*10), (x*10)+10, (y*10)+10));
            }
        }

        int indWidth = graphics.getWidth() / 8;
        int indHeight = graphics.getHeight() / 6;

        scaleRect = new Rect(0, 0, indWidth*count, indHeight*count);
        image = Bitmap.createBitmap(indWidth*count, indHeight*count, Bitmap.Config.RGB_565);
        Canvas imageCanv = new Canvas(image);

        colourImage = Bitmap.createBitmap(indWidth*count, indHeight*count, Bitmap.Config.RGB_565);
        Canvas colourCanv = new Canvas(colourImage);

        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        paint.setAntiAlias(false);
        paint.setDither(false);

        for(int x = 0; x < count; x++){
            for(int y = 0; y < count; y++) {
                Rect rect = new Rect(indWidth * y, indHeight * x, indWidth*y + indWidth, indHeight * x + indHeight);
                getTexture(track[x][y], imageCanv, colourCanv, rect, paint);
            }
        }
    }

    public void formStart(String[][] track, int[][]points){
        drawTrackSection(track, points[0][0], points[0][1], points[0][0] + points[0][2], points[0][1] + 1, "start");
        for(int y = 0; y < 8; y+=4){
            for(int x = 0; x < 6; x+= 3) {
                if (x == 3){y += 2;}
                track[points[0][1] + 2 + y][points[0][0] + x] = "sp000";
                track[points[0][1] + 3 + y][points[0][0] + x] = "sp180";
                track[points[0][1] + 2 + y][points[0][0] + 1 + x] = "sp090";
                track[points[0][1] + 3 + y][points[0][0] + 1 + x] = "sp270";
                if (x == 3){y -= 2;}
            }
        }
    }

    public void formTires(String[][] track){
        drawTrackSection(track, 2, 2, 10, 4, "tires");
        drawTrackSection(track, 10, 3, 15, 4, "tires");
        drawTrackSection(track, 2, 4, 4, 6, "tires");
        drawTrackSection(track, 3, 6, 4, 8, "tires");
        drawTrackSection(track, 11, 11, 39, 12, "tires");
        drawTrackSection(track, 35, 12, 39, 13, "tires");
        drawTrackSection(track, 23, 20, 49, 21, "tires");
        drawTrackSection(track, 22, 21, 23, 27, "tires");
        drawTrackSection(track, 23, 21, 25, 24, "tires");
        drawTrackSection(track, 23, 24, 24, 25, "tires");
        drawTrackSection(track, 25, 21, 26, 22, "tires");

        drawTrackSection(track, 35, 28, 37, 39, "tires");
        drawTrackSection(track, 34, 36, 35, 38, "tires");
        drawTrackSection(track, 33, 37, 34, 38, "tires");

        drawTrackSection(track, 11, 38, 35, 39, "tires");
        drawTrackSection(track, 12, 37, 17, 38, "tires");
        drawTrackSection(track, 12, 36, 14, 37, "tires");
        drawTrackSection(track, 12, 35, 13, 36, "tires");
        drawTrackSection(track, 11, 12, 12, 38, "tires");
        drawTrackSection(track, 12, 12, 14, 14, "tires");
        drawTrackSection(track, 12, 14, 13, 20, "tires");
    }

    public void formWorldBorders(String[][]track){
        drawTrackSection(track, 0, 0, track.length, 1, "tires");
        drawTrackSection(track, track.length-1, 0, track.length, track.length, "tires");
        drawTrackSection(track, 0, track.length-1, track.length, track.length, "tires");
        drawTrackSection(track, 0, 0, 1, track.length, "tires");
    }

    public void formTrack(String[][]track, int[][]p, boolean link){
        for(int i = 0; i < p.length - 1; i++){
            drawTrackLine(track, p[i][0], p[i][1], p[i+1][0], p[i+1][1], p[i][2]);
        }
        if(link)
            drawTrackLine(track, p[p.length - 1][0], p[p.length - 1][1], p[0][0], p[0][1], p[p.length - 1][2]);
    }

    public void formCornerEdges(String[][]track){
        for(int i = 0; i < track.length - 2; i++){
            for(int j = 0; j < track.length - 2; j++){
                //      00, 01, 10, 11
                boolean zz, zo, oz, oo;
                zz = track[j][i].equals("track");
                zo = track[j][i+1].equals("track");
                oz = track[j+1][i].equals("track");
                oo = track[j+1][i+1].equals("track");

                if (zz) {
                    if (zo) {
                        if (!oz && oo){
                            track[j + 1][i] = "cs270";
                        } else if (oz && !oo){
                            track[j + 1][i + 1] = "cs180";
                        }
                    } else {
                        if (!oz && !oo){
                            track[j + 1][i + 1] = "cb180";
                        } else if (oz && oo){
                            track[j][i + 1] = "cs090";
                        }
                    }
                } else {
                    if (zo) {
                        if (!oz && !oo){
                            track[j + 1][i] = "cb270";
                        } else if (oz && oo){
                            track[j][i] = "cs000";
                        }
                    } else {
                        if (oz && !oo){
                            track[j][i + 1] = "cb090";
                        } else if (!oz && oo){
                            track[j][i] = "cb000";
                        }
                    }
                }
            }
        }
    }

    public void formStraightEdges(String[][]track){
        for(int i = 0; i < track.length - 2; i++){
            for(int j = 0; j < track.length - 2; j++){
                //      00, 01, 10, 11
                boolean zz, zo, oz, oo;
                zz = track[j][i].equals("track");
                zo = track[j][i+1].equals("track");
                oz = track[j+1][i].equals("track");
                oo = track[j+1][i+1].equals("track");

                if(zz){
                    if(zo){
                        if(!oz && !oo){
                            track[j+1][i] = "ed270";
                            track[j+1][i+1] = "ed270";
                        }
                    } else {
                        if (oz && !oo){
                            track[j][i+1] = "ed180";
                            track[j+1][i+1] = "ed180";
                        }
                    }
                } else {
                    if(oz){
                        if(!zo && oo){
                            track[j][i] = "ed090";
                            track[j][i+1] = "ed090";
                        }
                    } else if(zo && oo){
                        track[j][i] = "ed000";
                        track[j+1][i] = "ed000";
                    }
                }
            }
        }
    }

    public void drawTrackLine(String[][]track, int x,int y,int x2, int y2, int stroke) {
        // http://tech-algorithm.com/articles/drawing-line-using-bresenham-algorithm/
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

    public void drawTrackSection(String[][]track, int x0, int y0, int x1, int y1, String tag){
        for(int y = y0; y < y1; y++){
            for(int x = x0; x < x1; x++){
                track[y][x] = tag;
            }
        }
    }

    public void getTexture(String str, Canvas canvas, Canvas colourCanvas, Rect rect, Paint paint){
        int rowIndex = 0, colIndex = 0;
        if (Character.isDigit(str.charAt(str.length() - 1))){
            switch(str.substring(0, 2)){
                case "ed": rowIndex = 1; break;
                case "cb": rowIndex = 2; break;
                case "cs": rowIndex = 3; break;
                case "sp": rowIndex = 2; colIndex = 4; System.out.println("HERE");
            }
            colIndex += Integer.parseInt(str.substring(2, 5)) / 90;
        } else {
            Random rand = new Random();
            switch(str){
                case "grass": rowIndex = 0; colIndex = rand.nextInt(3); break;
                case "track": rowIndex = 4; colIndex = rand.nextInt(3); break;
                case "start": rowIndex = 4; colIndex = 3; break;
                case "tires": rowIndex = 5; colIndex = 0; break;
            }
        }
        canvas.drawBitmap(graphics, graphicSpaces.get(rowIndex).get(colIndex), rect, paint);
        colourCanvas.drawBitmap(colourGraphics, graphicSpaces.get(rowIndex).get(colIndex), rect, paint);
    }

    public void update(double dx, double dy){
        translateX += dx;
        translateY += dy;
    }

    public void draw(Canvas canvas){
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(translateX, translateY);
        canvas.scale(scale, scale);
        canvas.drawBitmap(image, null, scaleRect, null);
        canvas.restore();
    }
}