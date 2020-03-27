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


    public ControlKnob(Context context, String name, String[] stateNames, int currentValue) {
        super(context);
        this.name = name;
        this.stateNames = stateNames;
        this.currentValue = currentValue;
        stateLabelPos = new Point[stateNames.length];
        createViewObjects();
        setCurrentValue(currentValue);
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
        int yPos = (int)(pos.y  + Math.pow(Math.abs((2 * paint.ascent())) + (2 * Math.abs(paint.descent())), 0.5) + paint.getTextSize()*1.5);
        canvas.drawText(text, xPos, yPos, paint);
    }

    private void createViewObjects(){
        LayoutInflater.from(getContext()).inflate(R.layout.control_knob, this);
        nameText = (TextView) findViewById(R.id.nameText);
        //stateText = (TextView) findViewById(R.id.stateText);
        controlKnob =(ImageView) findViewById(R.id.control_knob);
        nameText.setText(name);
    }

    private void setRotation(int currentValue){
        float progress = ((float)currentValue / (float) stateNames.length);
        float angle =  progress * 360 + angleOffset;
        controlKnob.setRotation(angle);
    }


    //https://gamedev.stackexchange.com/questions/9607/moving-an-object-in-a-circular-path
    private Point getStatePosition(int stateValue) {

        float offset = 22;

        //float radius = (360 / 2) + offset;
        double rough = Math.pow(controlKnob.getHeight() * controlKnob.getWidth(), 0.5);
        float radius = (float)(rough / 2) + offset;
        Double startAngle = Math.PI * (9 / 2d);
        Double angle =  startAngle + (stateValue * ( (2 * Math.PI) / stateNames.length));

        float xOrigin = controlKnob.getX() + (controlKnob.getWidth() / 2);
        float yOrigin = controlKnob.getY() + (controlKnob.getHeight() / 2);

        int posX  = (int) ((radius * Math.cos(angle)) + xOrigin);
        int posY  = (int) ((radius * Math.sin(angle)) + yOrigin);
        return new Point(posX, posY);
    }



//https://www.pocketmagic.net/custom-rotary-knob-control-for-android/
    public void setCurrentValue(int statePosition){
        setRotation(statePosition);
    }

    private int closestPoint(Point location){

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


    @Override
    public void setControlListener(final IControlListener controlListener) {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
             public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                Point contact = new Point((int)event.getX(), (int)event.getY());
                int closestState = closestPoint(contact);
                setCurrentValue(closestState);
                controlListener.onControlChange(closestState);
                controlListener.onControlStopTouch();
                return true;

            }
        });

    }

}
