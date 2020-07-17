package com.example.multitouch;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.multitouch.views.CustomCircleView;



import java.math.BigDecimal;
import java.math.RoundingMode;

public class MultiTouchGameScreen extends AppCompatActivity implements Button.OnClickListener, View.OnTouchListener{


    private CustomCircleView touchFrame;
    private Button clearScreenButton;

    int currentShockWaveId = 0;

    private int numberofPoints = 0;

    private TextView touchPointsView;
    private TextView touchPositionView;
    private TextView touchPressureView;
    float xPos2 = -1;
    float yPos2 = -1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_touch_game_screen);

        clearScreenButton = findViewById(R.id.clearScreenButton_ID);
        clearScreenButton.setOnClickListener(this);

        touchPointsView = findViewById(R.id.view_touchpoints_ID);
        touchPositionView = findViewById(R.id.view_info_touchposition_ID);
        touchPressureView = findViewById(R.id.view_info_pressure_ID);


        touchFrame = findViewById(R.id.touchFrame_ID);
        touchFrame.setOnTouchListener(this);



    }


    /**
     * on click for clear screen button
     * @param v
     */

    @Override
    public void onClick(View v) {
        int i = 0;
        switch (v.getId()){
            case R.id.clearScreenButton_ID:



                touchFrame.clearScreen();
                numberofPoints = 0;

                touchPointsView.setText("");
                touchPositionView.setText("");
                touchPressureView.setText("");


        }  

    }

    /**
     * rounds float to the given places parameter in order to show pressure more appealing
     * @param f float value to convert
     * @param places how many decimales places
     * @return returns new float
     */
    private static float roundFloat(float f, int places) {

        BigDecimal bigDecimal = new BigDecimal(Float.toString(f));
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }

    /**
     * this method contains all neccesarry methods from our touchframe in order to display all animations
     * the right way, we also save duplicate code by calling all methods in this one.
     * @param tempx x coordinate
     * @param tempy y coordinate
     * @param pressure pressure as float
     */
    public void callTouchFrameMethods(float tempx, float tempy, float pressure){

        touchFrame.setAble2Draw(true);

        String coordinates = "[" + (int) tempx + "/" + (int) tempy + "]";


       float temppressure = roundFloat(pressure,3);


        touchPointsView.setText(String.valueOf(numberofPoints));
        touchPositionView.setText(coordinates);
        touchPressureView.setText(String.valueOf(temppressure));


        //set the info needed for circle and create one afterwards.

        touchFrame.createCircle(tempx,tempy, numberofPoints);
        numberofPoints++;
        //touchFrame.scaleWithHandler();
        touchFrame.scaleCircles();
        touchFrame.refreshScreen();
    }


    /**
     * handles on touch for single and multi touch
     * single and multi touch down call the touchframe methods
     * single and multi release create the shockwaves
     * @param v custom view here
     * @param event touchevent
     * @return
     */

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        //use action masked for pointer functionality
        int action = event.getActionMasked();
        // Get the index of the pointer associated with the action.
        int index = event.getActionIndex();
        float pressure;

        float xPos = -1;
        float yPos = -1;


        switch (action) {


            case MotionEvent.ACTION_DOWN:
                System.out.println("Single touch event");
                xPos2 = event.getX(index);
                yPos2 = event.getY(index);
                pressure = event.getPressure();
                callTouchFrameMethods(xPos2, yPos2, pressure);
                break;


            case MotionEvent.ACTION_POINTER_DOWN:


                System.out.println("Multi touch event");
                xPos = event.getX(index);
                yPos = event.getY(index);
                pressure = event.getPressure(index);
                callTouchFrameMethods(xPos, yPos, pressure);



            case MotionEvent.ACTION_UP:


                System.out.println("Single touch event");
                xPos2 = event.getX(index);
                yPos2 = event.getY(index);
                touchFrame.createShockWave(xPos2, yPos2, currentShockWaveId);
                currentShockWaveId++;
                touchFrame.scaleCircles();
                touchFrame.refreshScreen();


                break;
           case MotionEvent.ACTION_POINTER_UP:



                   xPos = event.getX(index);
                   yPos = event.getY(index);
                   System.out.println("Released Finger:" + index);
                   touchFrame.createShockWave(xPos, yPos, currentShockWaveId);
                   currentShockWaveId++;
                   touchFrame.scaleCircles();
                   touchFrame.refreshScreen();


                break;
        }
        return true;
    }



}