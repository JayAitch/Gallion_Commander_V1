package uk.ac.brighton.jh1152.gallioncommanderv1;

public interface IControlListener{
    void onControlChange(int value);
    void onControlStopTouch();
    void onControlStartTouch();
}
