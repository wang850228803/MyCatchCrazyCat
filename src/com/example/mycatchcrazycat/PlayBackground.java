package com.example.mycatchcrazycat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class PlayBackground extends SurfaceView implements OnTouchListener {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    public static final int BLOCKS = 15;
    public static final int DIAM = 100;
    public static final int Y_OFFSET = 350;
    
    private Dot metric[][];
    
    private int  cat_x=4, cat_y=5;
    
    private HashMap<Integer,Dot> avaNeibsMap=new HashMap<Integer,Dot>();
    private HashMap<Integer,Integer> disNotBlock=new HashMap<Integer, Integer>();
    private HashMap<Integer,Integer> disBlock=new HashMap<Integer, Integer>();

    PlayBackground(Context context) {
        super(context);
        getHolder().addCallback(mCallback);
        metric = new Dot[WIDTH][HEIGHT];
        
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                metric[i][j] = new Dot(i, j);
        
        initGame();
        setOnTouchListener(this);
    }
    
    private void initGame() {
        Random random =new Random();
        int loc =0;
        for(int i = 0; i < BLOCKS; i++){
            loc  =random.nextInt(100);
            metric[loc/10][loc%10].setStatus(Dot.STATUS_ON);
        }
            
        metric[cat_x][cat_y].setStatus(Dot.STATUS_IN);
    }

    Callback mCallback = new Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            
        }
        
    };
    
    private void move() {
        int bestDir=0, offset=0;
        List<Map.Entry<Integer, Integer>> mappingList = null;
        if (getAvaNeibs() == 0)
            win();
        else {
            getDistance();

            if (disNotBlock.size() > 0) {
                mappingList = new ArrayList<Map.Entry<Integer, Integer>>(disNotBlock.entrySet());
                Collections.sort(mappingList, new Comparator<Map.Entry<Integer, Integer>>() {
                    public int compare(Map.Entry<Integer, Integer> mapping1,
                            Map.Entry<Integer, Integer> mapping2) {
                        return mapping1.getValue().compareTo(mapping2.getValue());
                    }
                });
                bestDir=mappingList.get(0).getKey();
            } else if (disBlock.size() > 0) {
                mappingList = new ArrayList<Map.Entry<Integer, Integer>>(disBlock.entrySet());
                Collections.sort(mappingList, new Comparator<Map.Entry<Integer, Integer>>() {
                    public int compare(Map.Entry<Integer, Integer> mapping1,
                            Map.Entry<Integer, Integer> mapping2) {
                        return mapping1.getValue().compareTo(mapping2.getValue());
                    }
                });
                bestDir=mappingList.get(mappingList.size()-1).getKey();
            }
            
            metric[cat_x][cat_y].setStatus(Dot.STATUS_OFF);
            if(cat_y%2==1)
                offset=1;
            switch(bestDir){
                case 0:
                    cat_x=cat_x+1;
                    break;
                case 1:
                    cat_x=cat_x-1;
                    break;
                case 2:
                    cat_x=cat_x+offset;
                    cat_y=cat_y+1;
                    break;
                case 3:
                    cat_x=cat_x-1+offset;
                    cat_y=cat_y+1;
                    break;
                case 4:
                    cat_x=cat_x+offset;
                    cat_y=cat_y-1;
                    break;
                default:
                    cat_x=cat_x-1+offset;
                    cat_y=cat_y-1;
                    break;
            }
            metric[cat_x][cat_y].setStatus(Dot.STATUS_OFF);
        }

    }
    
    private int getAvaNeibs(){
        int offset =0, x, y;
        if(cat_y%2==1)
            offset=1;
        for (int i=0;i<6;i++){
            switch(i){
                case 0:
                    x=cat_x+1;
                    y=cat_y;
                    break;
                case 1:
                    x=cat_x-1;
                    y=cat_y;
                    break;
                case 2:
                    x=cat_x+offset;
                    y=cat_y+1;
                    break;
                case 3:
                    x=cat_x-1+offset;
                    y=cat_y+1;
                    break;
                case 4:
                    x=cat_x+offset;
                    y=cat_y-1;
                    break;
                default:
                    x=cat_x-1+offset;
                    y=cat_y-1;
                    break;
            }
            if(x<0||x>9||y<0||y>9){
                lose();
                avaNeibsMap.clear();
                break;
            }
            else if(metric[x][y].getStatus()==Dot.STATUS_OFF)
                avaNeibsMap.put(i, metric[x][y]);
        }
        return avaNeibsMap.size();
    }
    
    private void lose(){
        Toast.makeText(this.getContext(), "you lose", Toast.LENGTH_LONG).show();
    }
    
    private void win(){
        Toast.makeText(this.getContext(), "you win", Toast.LENGTH_LONG).show();
    }
    
    private void getDistance(){
        Integer dir;
        Dot dot;
        int distance=1;
        int xDir=0, yDir=0, x, y;
        Iterator ite=avaNeibsMap.entrySet().iterator();
        while(ite.hasNext()){
            Entry entry=(Entry)ite.next();
            dir = (Integer)entry.getKey();
            dot = (Dot)entry.getValue();
            switch(dir){
                case 0:
                    xDir=1;
                    yDir=0;
                    break;
                case 1:
                    xDir=-1;
                    yDir=0;
                    break;
                case 2:
                    xDir=0;
                    yDir=1;
                    break;
                case 3:
                    xDir=-1;
                    yDir=1;
                    break;
                case 4:
                    xDir=0;
                    yDir=-1;
                    break;
                default:
                    xDir=-1;
                    yDir=-1;
                    break;
            }
            while(true){
                x = dot.getX() + xDir;
                y = dot.getY() + yDir;
                if(x<0||x>9||y<0||y>9){
                    disNotBlock.put(dir, distance);
                    break;
                }
                else if(metric[x][y].getStatus()==Dot.STATUS_ON){
                    disBlock.put(dir, distance);
                    break;
                }
                distance++;
            }
            distance =1;
        }
    }
    
    public void redraw() {
        int offset = 0;
        Canvas c = getHolder().lockCanvas();
        c.drawColor(Color.LTGRAY);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0;i<WIDTH;i++)
            for(int j=0;j<HEIGHT;j++){
                if(j%2==1)
                    offset = DIAM/2;
                else
                    offset = 0;
               if(metric[i][j].getStatus() == Dot.STATUS_IN)
                   paint.setColor(Color.RED);
               else if(metric[i][j].getStatus() == Dot.STATUS_OFF)
                   paint.setColor(Color.WHITE);
               else
                   paint.setColor(Color.YELLOW);
               
               c.drawOval(new RectF(i*DIAM+offset, j*DIAM+Y_OFFSET, (i+1)*DIAM+offset, (j+1)*DIAM+Y_OFFSET), paint);
            }
        getHolder().unlockCanvasAndPost(c);
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        int x, y;
        if(event.getAction()==(MotionEvent.ACTION_UP)){
            y=(int)(event.getY()-Y_OFFSET)/DIAM;
            if (y%2==1)
                x = (int)(event.getX()-DIAM/2)/DIAM;
            else
                x = (int)(event.getX())/DIAM;
            if(metric[x][y].getStatus()==Dot.STATUS_OFF)
                    metric[x][y].setStatus(Dot.STATUS_ON);
        }
        move();
        redraw();
        return true;
    }
}
