package uk.ac.brighton.jh1152.gallioncommanderv1;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class ControlKnob extends LinearLayout implements ICustomControl {

    ImageView controlKnob;
    TextView nameText;
    String name;
    String[] stateNames;
    Point[] stateLabelPos;
    int currentValue;
    float angleOffset = 45;

    public ControlKnob(Context context) {
        super(context);
    }

    public ControlKnob(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlKnob(Context context, @Nullable AttributeSet attrs, int defStyleAttr, String name, String[] stateNames, int currentValue) {
        super(context, attrs, defStyleAttr);

    }

    public ControlKnob(Context context, String name, String[] stateNames, int currentValue) {
        super(context);
        this.name = name;
        this.stateNames = stateNames;
        this.currentValue = currentValue;
        stateLabelPos = new Point[stateNames.length];
        createViewObjects();
        setCurrentValue(currentValue);
    }

    private void drawAllLabels(){
        for(int stateLabelsInc = 0; stateLabelsInc < stateNames.length; stateLabelsInc++){
        //    drawerTextAt(getStatePosition(stateLabelsInc), stateNames[stateLabelsInc]);
        }
}

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        for(int stateLabelsInc = 0; stateLabelsInc < stateNames.length; stateLabelsInc++){
            Point newPosition = getStatePosition(stateLabelsInc);
            drawerTextAt(canvas, newPosition, stateNames[stateLabelsInc]);
            stateLabelPos[stateLabelsInc] = newPosition;
        }
    }

    private void drawerTextAt(Canvas canvas,Point pos, String text){
        Paint paint = new Paint();
        Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.anton);
        paint.setTextSize(32);
        paint.setTypeface(typeface);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);

        int xPos = pos.x + (int)(paint.measureText(text,0,text.length())/2) + (int)(paint.getTextSize()/2);
        int yPos = (int)(pos.y  + Math.abs(paint.ascent() + Math.abs(paint.descent())) + paint.getTextSize()/2);
        canvas.drawText(text, xPos, yPos, paint);

    }

    private void createViewObjects(){
        LayoutInflater.from(getContext()).inflate(R.layout.control_knob, this);
        nameText = (TextView) findViewById(R.id.nameText);
        //stateText = (TextView) findViewById(R.id.stateText);
        controlKnob =(ImageView) findViewById(R.id.control_knob);
        nameText.setText(name);
        drawAllLabels();

    }

    private void setRotation(int currentValue){
        float progress = ((float)currentValue / (float) stateNames.length);
        float angle =  progress * 360 + angleOffset;
        controlKnob.setRotation(angle);
    }

// thios doesnt work in layout

    private Point getStatePosition(int stateValue) {


        float radius = (controlKnob.getWidth() / 2);
        Double startAngle = Math.PI * (9 / 2d);
        Double angle =  startAngle + (stateValue * ( (2 * Math.PI) / stateNames.length));

        float xOrigin = controlKnob.getX() + (controlKnob.getWidth() / 2);
        float yOrigin = controlKnob.getY() + (controlKnob.getHeight() / 2);

        int posX  = (int) ((radius * Math.cos(angle)) + xOrigin);
        int posY  = (int) ((radius * Math.sin(angle)) + yOrigin);
        return new Point(posX, posY);
    }


    //https://gamedev.stackexchange.com/questions/9607/moving-an-object-in-a-circular-path
//
//    private Point getStatePosition(int stateValue){
//
//        float radius = controlKnob.getWidth() / 2;
//        float angle = ((float)stateValue / (float) stateNames.length);
//        float xOrigin = controlKnob.getX() + (controlKnob.getWidth() / 2);
//        float yOrigin = controlKnob.getY() + (controlKnob.getHeight() / 2);
//        int xPos = (int)(xOrigin + Math.cos((double) angle) * radius);
//        int yPos = (int)(yOrigin + Math.sin((double) angle) * radius);
//        return new Point(xPos,yPos);
//    }

    private void snapToClosestState(){
        float currentRotation = controlKnob.getRotation();
        float closestRotation = -1;
        float rotationPerState = 360 / (float) stateNames.length;
        float previouseDistance = 1000;

        for(int stateIterator = 0; stateNames.length > stateIterator; stateIterator++){
            float stateRotation = stateIterator * rotationPerState;
            float rotationMod = Math.abs(currentRotation % 360);
            float distance = Math.abs(stateRotation - rotationMod);


            Log.d("iter<<<", "istance:  " + distance);
            Log.d("iter<<<", "stateRotation:  " + stateRotation);
            Log.d("iter<<<", "currnt rotation:  " + rotationMod);
            Log.d("iter<<<", "is closer:  " + (distance < previouseDistance));

            if(distance < previouseDistance){
                closestRotation = stateRotation;
                previouseDistance = distance;
            }

        }

        Log.d("rotationsnap<<<", "snaping to:  " + closestRotation);
        Log.d("rotationsnap<<<", "previous distance:  " + previouseDistance);
        controlKnob.setRotation(closestRotation);
    }

    public void rotateKnob(float amount){
        float rotation = controlKnob.getRotation();
        rotation = rotation + (amount * 5);
        controlKnob.setRotation(rotation % 360);
    }
//https://www.pocketmagic.net/custom-rotary-knob-control-for-android/
    public void setCurrentValue(int statePosition){
    //    stateText.setText(stateNames[statePosition]);
    //    seekBar.setProgress(statePosition);
        setRotation(statePosition);
    }

    public int cloestPoint(Point location){

        double closetDistance = 10000;
        int closetPoint = 0;
        int incrementor = 0;
        for(Point point : stateLabelPos){
            double distanceBetween = distance(location, point);
            Log.d("new distances", "<<<<<" + distanceBetween);
            if(distanceBetween < closetDistance){
                closetDistance = distanceBetween;
                closetPoint = incrementor;
            }

            incrementor++;
        }
        return closetPoint;
    }


//https://stackoverflow.com/questions/11534323/android-distance-between-two-points
    private double distance(Point a, Point b){
        double distance = Math.sqrt(Math.pow(a.x - b.x,2) + Math.pow(a.y - b.y, 2));
        return distance;
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        String DEBUG_TAG = "<<<<<<<<<<<<<";
//        int action = MotionEventCompat.getActionMasked(event);
//
//        switch (action) {
//            case (MotionEvent.ACTION_DOWN):
////                Log.d(DEBUG_TAG, "Action was DOWN");
////                currentValue--;
////                if(currentValue < 0){
////                    currentValue = 0;
////                }
////                setCurrentValue(currentValue);
//                return true;
//            case (MotionEvent.ACTION_MOVE):
//
//
//
//                int historySize = event.getHistorySize();
//                float xd = 0;
//                float yd = 0;
//                float td = 0;
//                //float speed = 10;
//
//                if(historySize > 0) {
//                    xd = event.getX() - event.getHistoricalX(historySize - 1);
//                    yd = (event.getY() - event.getHistoricalY(historySize - 1)) * -1;
//                    td = xd + yd;
//                }
//                rotateKnob(td);
//                if(currentValue > stateNames.length){
//                   // currentValue = 0;
//                }
//                Log.d(DEBUG_TAG, "Action was MOVE" + currentValue);
//             //   setCurrentValue(currentValue);
//                return true;
//            case (MotionEvent.ACTION_UP):
//             //   snapToClosestState();
////                Log.d(DEBUG_TAG, "Action was UP");
////                currentValue++;
////                if(currentValue > stateNames.length){
////                    currentValue = 0;
////                }
////                setCurrentValue(currentValue);
//                return true;
//            case (MotionEvent.ACTION_CANCEL):
//                Log.d(DEBUG_TAG, "Action was CANCEL");
//                return true;
//            case (MotionEvent.ACTION_OUTSIDE):
//                Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
//                        "of current screen element");
//                return true;
//            default:
//
//        }
//        return  false;
//    }


    @Override
    public void setControlListener(final IControlListener controlListener) {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
             public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                Point contact = new Point((int)event.getX(), (int)event.getY());
                int closestState = cloestPoint(contact);
                Log.d("<<<<<<<<", "state chosen" +closestState);
                Log.d("<<<<<<<<", "state chosen" +closestState);
                setCurrentValue(closestState);
                controlListener.onControlChange(closestState);
                controlListener.onControlStopTouch();
        switch (action) {
            case (MotionEvent.ACTION_DOWN):

                return true;
            case (MotionEvent.ACTION_MOVE):
//                int historySize = event.getHistorySize();
//                float xd = 0;
//                float yd = 0;
//                float td = 0;
//                //float speed = 10;
//
//                if(historySize > 0) {
//                    xd = event.getX() - event.getHistoricalX(historySize - 1);
//                    yd = (event.getY() - event.getHistoricalY(historySize - 1)) * -1;
//                    td = xd + yd;
//                }
//                rotateKnob(td);
//                Point contact = new Point((int)event.getX(), (int)event.getY());
//                int closestState = cloestPoint(contact);
//                Log.d("<<<<<<<<", "state chosen" +closestState);
//                Log.d("<<<<<<<<", "state chosen" +closestState);
//                setCurrentValue(closestState);
                return true;
            case (MotionEvent.ACTION_UP):
                return true;
            default:
                return false;

        }
             //   getParent().requestDisallowInterceptTouchEvent(true);
               // return false;
            }
        });
//
//            this.setOnScrollChangeListener(new OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    Log.d("<<<<<<<<", "x: " +scrollX + " Y: "+ scrollY);
//                }
//            });
//
//            this.setOnDragListener(new OnDragListener() {
//                @Override
//                public boolean onDrag(View v, DragEvent event) {
//                    Log.d("<<<<<<<<", "x: " +event.getX() + " Y: "+ event.getY());
//                    return false;
//                }
//            });

            /**
            this.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                     Log.d("<<<<<<<<", "x: " +event.getX() + " Y: "+ event.getY() + event.getAction());
                    return false;
                }
            });
             **/
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener){
    //    seekBar.setOnSeekBarChangeListener(listener);
    }


    public float cartesianToPolar(float x, float y){
        return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
    }


}
