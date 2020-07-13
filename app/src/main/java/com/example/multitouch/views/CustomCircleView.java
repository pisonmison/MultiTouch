package com.example.multitouch.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.multitouch.R;

import java.util.ArrayList;
import java.util.Iterator;


public class CustomCircleView extends View {

    public void setAble2Draw(boolean able2Draw) {
        this.able2Draw = able2Draw;
    }

    private boolean able2Draw = false;




    private Handler mHandler;


    private Paint backgroundPaint;
    private Paint circlePaint;//enables anti aliasing, blurds edges.

    private Paint shockWavePaint;




    private ArrayList<Point> circleList = new ArrayList<Point>();
    private ArrayList<Point> shockWaveList = new ArrayList<Point>();



    public class Point{
        private float xPos;
        private float yPos;
        private boolean able2Delete = false;
        private boolean ableToDecrease = false;
        private int radius = 1;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        int id;

        public boolean isAble2Delete() {
            return able2Delete;
        }

        public void setAble2Delete(boolean able2Delete) {
            this.able2Delete = able2Delete;
        }



        public boolean isAbleToDecrease() {
            return ableToDecrease;
        }

        public void setAbleToDecrease(boolean ableToDecrease) {
            this.ableToDecrease = ableToDecrease;
        }



        public float getxPos() {
            return xPos;
        }

        public void setxPos(float xPos) {
            this.xPos = xPos;
        }

        public float getyPos() {
            return yPos;
        }

        public void setyPos(float yPos) {
            this.yPos = yPos;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }




        public Point(){

        }

        public Point(float mXPos, float mYPos){
            this.xPos = mXPos;
            this.yPos = mYPos;

        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + xPos + "/" + yPos + "/" + ableToDecrease + "]";
        }


    }





    public CustomCircleView(Context context) {
        super(context);
        init(null);
    }

    public CustomCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /**
     * init method to call from all construktors above.
     * saves duplicate code.
     * when we use custom circle in view, due to layoutinflation, the different construktors are called.
     * assign the paints here:
     */

    private void init(@Nullable AttributeSet set){


        backgroundPaint = new Paint();
        backgroundPaint.setColor(getResources().getColor(R.color.multitouch_grey));
        backgroundPaint.setStyle(Paint.Style.FILL);
        circlePaint = new Paint();
        circlePaint.setColor(getResources().getColor(R.color.multitouch_red));
        shockWavePaint = new Paint();
        shockWavePaint.setStyle(Paint.Style.STROKE);
        shockWavePaint.setColor(getResources().getColor(R.color.multitouch_yellow));


    }

    public void clearScreen(){
        if(!circleList.isEmpty() && !shockWaveList.isEmpty()){
            circleList.clear();
            shockWaveList.clear();
            able2Draw = false;
            mHandler.removeCallbacksAndMessages(null); //stop all handlers.
            postInvalidate();
        }else{
            System.out.println("empty list");
        }


    }



    /**
     * updates the radius of all points +1 by getting the current point rad, increasing it and saving
     * it back into the point object again until a point has reached 100 radius.
     */
    public void updateAllPoints(){

        for(Point p : circleList){

            int currentRad = p.getRadius();
            if(!p.isAbleToDecrease()){
                if(currentRad < 100){
                    currentRad++;
                    p.setRadius(currentRad);
                }
                else{
                    //mHandler.removeCallbacksAndMessages(null);
                    p.setAbleToDecrease(true);


                }
            }
        }
    }





    /**
     * this creates a shockwave circle at the given position
     */

    public void createShockWave(float x, float y, int id){
        Point p = new Point();
        p.setxPos(x);
        p.setyPos(y);
        p.setRadius(40);
        p.setId(id);
        shockWaveList.add(p);
        //System.out.println(p);
    }
    /**
     * uses all the methods which update the shopckwaves and points in a looper every 10ms with 10ms intervalls
     * therfore bringing animations to life.
     */

    public void scaleCircles(){
    mHandler = new Handler();


    final Runnable r = new Runnable() {
        @Override
        public void run() {
            if (able2Draw && !circleList.isEmpty()) {

                updateAllPoints();
                decreasePoints();
                increasShockWaveAnimation();
                decreasShockWaveAnimation();


            }
            mHandler.postDelayed(this, 10);
            postInvalidate();
        }

    };

    mHandler.postDelayed(r, 10);



}






    /**
     * after the point reached 100, it slowly begins to decrease in size again,
     * this is also done for all points.
     */

    public void decreasePoints(){

        for(Point p : circleList){

            int currentRad = p.getRadius();
            if(p.isAbleToDecrease())
                    if(currentRad > 0){
                        currentRad--;
                        p.setRadius(currentRad);
                    }else{
                        //when a point dissapeared, remove its handler task
                       //mHandler.removeCallbacksAndMessages(null);
                       mHandler.removeCallbacksAndMessages(null);
                       //System.exit(0);
                    }


                }


        }


    /**
     *
     * @param x changed from currentXPos/currentYpos
     * @param y changed from currentXPos/currentYpos
     */
    public void createCircle(float x, float y){
        Point p = new Point();
        p.setxPos(x);
        p.setyPos(y);
        circleList.add(p);

    }


    /**
     * functions for drawning the shockwave circles and normal circles
     * @param canvas
     */
    public void drawCircles(Canvas canvas){

        for (Point p : circleList) {
                canvas.drawCircle(p.getxPos(), p.getyPos(), p.getRadius(), circlePaint);
            }
        postInvalidate();

    }

    public void drawShockwaves(Canvas canvas){
        for (Point p : shockWaveList) {
            canvas.drawCircle(p.getxPos(), p.getyPos(), p.getRadius(), shockWavePaint);
        }
        postInvalidate();
    }



    /**
     * this method loops through or wave list and increaes it radius, when a shockwave,
     * reaches max radius, it gets deleted from the list. For deleting whie looping, we created an list operator
     * which doesnt interface with index, enables modifiyng.
     * */
    public void increasShockWaveAnimation() {
        Iterator<Point> it = shockWaveList.iterator();
        while (it.hasNext()) {
            Point p = it.next();


            int currentRad = p.getRadius();

            if (!p.isAbleToDecrease()) {
                if (currentRad < 220) {
                    currentRad+=2;
                    p.setRadius(currentRad);
                } else {
                    //mHandler.removeCallbacksAndMessages(null);
                    p.setAbleToDecrease(true);


                }

            }


        }
    }

    /**
     * decreases the shockwave circle as the normal circle, updates radius -1,5 everytime the handler calls it
     */
    public void decreasShockWaveAnimation(){
        Iterator<Point> it = shockWaveList.iterator();
        while (it.hasNext()) {
            Point p = it.next();
            int rad = p.getRadius();
            if (p.isAbleToDecrease()) {
                if (rad > 0) {
                    rad-= 1.5;
                    p.setRadius(rad);
                } else {
                     p.setAble2Delete(true);
                    if (p.isAble2Delete()) {
                        it.remove();
                        mHandler.removeCallbacksAndMessages(null);
                        //deleteShockWave(p.getId());
                    }


                }
            }

        }
    }


    /**
     * to refresh screen outside this class
     */
    public void refreshScreen(){
        postInvalidate();
    }


    /**
     * when scaler is used in on draw, it updates more frequently and faster somehow,
     * due to ondraw getting called exponentially?.
     * when used outside in touchEvents, its more slowly and gets faster the more circles exist.
     * @param canvas canvas
     */
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawPaint(backgroundPaint);
        if(able2Draw){

            drawShockwaves(canvas);
            drawCircles(canvas);
        }



    }


}
