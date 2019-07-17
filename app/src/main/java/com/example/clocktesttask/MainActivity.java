package com.example.clocktesttask;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView hourArrow;
    private ImageView minArrow;
    private TextView holderText, timeText;
    private Button submitButton;
    private static Bitmap imageOriginalH, imageOriginalM, imageScaledH, imageScaledM;
    private static Matrix matrixH;
    private static Matrix matrixM;
    private int hourHeight, hourWidth;
    private int minuteHeight, minuteWidth;
    private double hourCurAngle, minuteCurAngle;
    private double dX, dY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (imageOriginalH == null || imageOriginalM == null) {
            imageOriginalH = BitmapFactory.decodeResource(getResources(), R.drawable.hour_hand);
            imageOriginalM = BitmapFactory.decodeResource(getResources(), R.drawable.min_hand);
        }

        if (matrixH == null || matrixM == null) {
            matrixH = new Matrix();
            matrixM = new Matrix();
        } else {
            matrixH.reset();
            matrixM.reset();
        }

        submitButton = findViewById(R.id.submitButton);
        holderText = findViewById(R.id.textHolder);
        timeText = findViewById(R.id.textTime);
        hourArrow = findViewById(R.id.hourArrowImageView);
        minArrow = findViewById(R.id.minuteArrowImageView);
        hourArrow.setOnTouchListener(new HourHandOnTouchListener());
        minArrow.setOnTouchListener(new MinHandOnTouchListener());
        hourArrow.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (hourHeight == 0 || hourWidth == 0) {
                    hourHeight = hourArrow.getHeight();
                    hourWidth = hourArrow.getWidth();

                    // resize
                    Matrix resize = new Matrix();
                    //resize.postScale((float)Math.min(dialerWidth, dialerHeight) / (float)imageOriginal.getWidth(), (float)Math.min(dialerWidth, dialerHeight) / (float)imageOriginal.getHeight());
                    imageScaledH = Bitmap.createBitmap(imageOriginalH, 0, 0, imageOriginalH.getWidth(), imageOriginalH.getHeight(), resize, false);
                }
            }
        });
        minArrow.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (minuteHeight == 0 || minuteWidth == 0) {
                    minuteHeight = hourArrow.getHeight();
                    minuteWidth = hourArrow.getWidth();

                    // resize
                    Matrix resize = new Matrix();
                    //resize.postScale((float)Math.min(dialerWidth, dialerHeight) / (float)imageOriginal.getWidth(), (float)Math.min(dialerWidth, dialerHeight) / (float)imageOriginal.getHeight());
                    imageScaledM = Bitmap.createBitmap(imageOriginalM, 0, 0, imageOriginalM.getWidth(), imageOriginalM.getHeight(), resize, false);
                }
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("angleHour",String.valueOf(hourCurAngle));
                Log.d("angleMinute",String.valueOf(minuteCurAngle));
                String time = String.valueOf(timeText.getText());
                String[] separated = time.split(":");
                String hour = separated[0], minute=separated[1];
                boolean correct;
                int hourValue, minuteValue;
                if(hour.startsWith("0")) {
                    hourValue = Integer.valueOf(hour.substring(1));
                }
                else{
                    hourValue = Integer.valueOf(hour);
                }
                if(minute.startsWith("0")) {
                    minuteValue = Integer.valueOf(minute.substring(1));
                }
                else{
                    minuteValue = Integer.valueOf(minute);
                }

                if(((hourCurAngle%360)/30 >= hourValue-0.2)&&((minuteCurAngle%360)/6 >= minuteValue-0.5)&&((hourCurAngle%360)/30 <= hourValue+0.2)&&((minuteCurAngle%360)/6 <= minuteValue+0.5)){
                    holderText.setText(R.string.text_holder_good);
                    correct=true;
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holderText.setText(R.string.text_holder);
                            Random r = new Random();
                            int hour = r.nextInt(12);
                            Random r1 = new Random();
                            int minute = r1.nextInt(60);
                            String displayHour, displayMinute, displayedTime;
                            if (hour<10){
                                displayHour="0"+String.valueOf(hour);
                            }
                            else{
                                displayHour = String.valueOf(hour);
                            }
                            if (minute<10){
                                displayMinute="0"+String.valueOf(minute);
                            }
                            else{
                                displayMinute = String.valueOf(minute);
                            }
                            displayedTime = displayHour + ":" + displayMinute;
                            timeText.setText(String.valueOf(displayedTime));
                        }
                    }, 5000);
                }
                else{
                    holderText.setText(R.string.text_holder_bad);
                    correct = false;
                }

            }
        });
    }

    private class MinHandOnTouchListener implements View.OnTouchListener{
        private double startAngle;
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    startAngle = getAngleM(motionEvent.getX(), motionEvent.getY());
                    break;

                case MotionEvent.ACTION_MOVE:
                    double currentAngle = getAngleM(motionEvent.getX(), motionEvent.getY());
                    minuteCurAngle = (currentAngle)*(-1)+360;
                    if(minuteCurAngle>270){
                        minuteCurAngle = minuteCurAngle-270;
                    }
                    else{
                        minuteCurAngle = minuteCurAngle+90;
                    }
                    rotateMinuteHand((float) (startAngle - currentAngle));
                    startAngle = currentAngle;
                    break;

                case MotionEvent.ACTION_UP:

                    break;
            }

            return true;
        }
    }

    private class HourHandOnTouchListener implements View.OnTouchListener{
        private double startAngle;
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    startAngle = getAngleH(motionEvent.getX(), motionEvent.getY());
                    break;

                case MotionEvent.ACTION_MOVE:
                    double currentAngle = getAngleH(motionEvent.getX(), motionEvent.getY());
                    hourCurAngle = (currentAngle)*(-1)+360;
                    if(hourCurAngle>270){
                        hourCurAngle = hourCurAngle-270;
                    }
                    else{
                        hourCurAngle = hourCurAngle+90;
                    }
                    rotateHourHand((float) (startAngle - currentAngle));
                    startAngle = currentAngle;
                    break;

                case MotionEvent.ACTION_UP:

                    break;
            }

            return true;
        }
    }

    private double getAngleH(double xTouch, double yTouch) {
        double x = xTouch - (hourWidth / 2d);
        double y = hourHeight - yTouch - (hourHeight / 2d);

        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }

    private double getAngleM(double xTouch, double yTouch) {
        double x = xTouch - (minuteWidth / 2d);
        double y = minuteHeight - yTouch - (minuteHeight / 2d);

        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }

    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    private void rotateHourHand(float degrees) {
        matrixH.postRotate(degrees);
        hourArrow.setImageBitmap(Bitmap.createBitmap(imageScaledH, 0, 0, imageScaledH.getWidth(), imageScaledH.getHeight(), matrixH, true));
    }

    private void rotateMinuteHand(float degrees) {
        matrixM.postRotate(degrees);
        minArrow.setImageBitmap(Bitmap.createBitmap(imageScaledM, 0, 0, imageScaledM.getWidth(), imageScaledM.getHeight(), matrixM, true));
    }
}
