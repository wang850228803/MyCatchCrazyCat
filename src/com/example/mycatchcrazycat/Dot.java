package com.example.mycatchcrazycat;

public class Dot {
    
    private int x, y, status;
    
    public static final int STATUS_ON = 0;
    public static final int STATUS_OFF = 1;
    public static final int STATUS_IN = 2;

    Dot(int x, int y){
        this.x = x;
        this.y = y;
        status = STATUS_OFF;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public int getStatus() {
        return status;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
}
