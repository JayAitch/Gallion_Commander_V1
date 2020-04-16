package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.os.CountDownTimer;
import android.os.Debug;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class InstructionTicker {

    TextView instructionText;
    ProgressBar progressBar;
    BoatConnector boatConnector;
    BarTimer barTimer;

    public InstructionTicker(BoatConnector boatConnector, ProgressBar progressBar, TextView instructionText, long duration){
        this.progressBar = progressBar;
        this.boatConnector = boatConnector;
        this.instructionText = instructionText;
        createTimer(duration);
    }

    // create a new timer and start it
    private void createTimer(long duration){
        barTimer = new BarTimer(duration);
        startTimer();
    }

    // start and stop the timer countdown
    public void startTimer(){
        barTimer.start();
    }
    public void stopTimer(){barTimer.cancel();}

    // trigger instruction timout event to reduce a life on the boat
    private void barFinish(){
        boatConnector.instructionTimeOut();
    }


    // display the current instruction text
    public void displayInstructionText(){
        String text = boatConnector.getCurrentInstructionString();
        instructionText.setText(text);
    }


    // inner class containing extension to trigger timer tick events
    public class BarTimer extends CountDownTimer{

        long milisDuration;

        public BarTimer(long duration){
            super(duration, 200);
            milisDuration = duration;
        }

        // show the bar progress as how far in this timer is in
        @Override
        public void onTick(long millisUntilFinished) {
            int barProgress = (int)(milisDuration - millisUntilFinished)  / 100;
            progressBar.setProgress(barProgress);
        }

        // trigger event when timer has ended
        @Override
        public void onFinish() {
            barFinish();
        }
    }
}
