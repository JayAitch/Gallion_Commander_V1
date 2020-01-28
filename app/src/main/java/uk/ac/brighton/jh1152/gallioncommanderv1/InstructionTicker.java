package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

public class InstructionTicker {

    TextView instructionText;
    ProgressBar progressBar;
    String instructionString;
    BoatConnector boatConnector;
    BarTimer barTimer;

    public InstructionTicker(BoatConnector boatConnector, ProgressBar progressBar, TextView instructionText, long duration){
        this.progressBar = progressBar;
        this.boatConnector = boatConnector;
        this.instructionText = instructionText;
        instructionString = boatConnector.getCurrentInstructionString();
        createTimer(duration);
    }

    private void createTimer(long duration){
        barTimer = new BarTimer(duration);
    }

    private void startTimer(){
        barTimer.start();
    }

    public class BarTimer extends CountDownTimer{

        long milisDuration;

        public BarTimer(long duration){
            super(duration, 200);
            milisDuration = duration;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int barProgress = (int)(milisDuration - millisUntilFinished)  / 100;
            progressBar.setProgress(barProgress);
        }

        @Override
        public void onFinish() {
            boatConnector.instructionTimeOut();
        }
    }
}
