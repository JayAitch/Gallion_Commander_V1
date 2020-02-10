package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class BoatActionMultiState implements IBaseBoatActionUI {

    int currentValue;
    Boat lBoat;
    BoatAction action;
    ControlSeekBarWithText seekbarWithText; // make generic should be able to have any multivalue actio on here
    ControlKnob testKnob; //temp
    ICustomControl control;
    //https://stackoverflow.com/questions/3671649/java-newinstance-of-class-that-has-no-default-constructor



    private <T>T instantiateControl(Class<T> controlClass, Activity activity, BoatAction boatAction) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            Constructor<T> constructor = controlClass.getConstructor(Context.class, String.class, String[].class, int.class);

            return constructor.newInstance(activity, boatAction.actionName, boatAction.states, boatAction.actionCurrent);

    }

    private ICustomControl instantiateControl(Activity activity, BoatAction boatAction) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Class controlClass = getActionClass(boatAction.controlType);
        Constructor constructor = controlClass.getConstructor(Context.class, String.class, String[].class, int.class);
        return (ICustomControl) constructor.newInstance(activity, boatAction.actionName, boatAction.states, boatAction.actionCurrent);
    }


    private Class getActionClass(BoatActionControlType controlType){
        switch (controlType){
            case KNOB:
                return  ControlKnob.class;

            case SLIDER:
                return  ControlSeekBarWithText.class;

            case TOGGLE:
                return  ControlToggleButton.class;
                default:
                    return ControlToggleButton.class;
        }

    }



    public BoatActionMultiState(Activity activity, Boat boat, BoatAction boatAction) {
        lBoat = boat;
        action = boatAction;

        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.activitiesGrid);


        try {

            control = (ICustomControl) instantiateControl(getActionClass(boatAction.controlType), activity, boatAction);
            layout.addView((LinearLayout)control);
            control.setControlListener(new IControlListener() {
                @Override
                public void onControlChange(int value) {
                    currentValue = value;
                    control.setCurrentValue(currentValue);
                }

                @Override
                public void onControlStopTouch() {
                    SetBoatAction();
                }

                @Override
                public void onControlStartTouch() {

                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }



//temp


//        testKnob = new ControlKnob(activity, action.actionName, action.states, action.actionCurrent);
//        layout.addView(testKnob);
//        testKnob.setControlListener(new IControlListener() {
//            @Override
//            public void onControlChange(int value) {
//
//            }
//
//            @Override
//            public void onControlStopTouch() {
//
//            }
//
//            @Override
//            public void onControlStartTouch() {
//
//            }
//        });



    }


    @Override
    public Boolean SetBoatAction() {
        lBoat.setActionValue(action.documentReference, currentValue);
        return null;
    }

    @Override
    public void valueChangeCallback() {

        control.setCurrentValue(action.actionCurrent);
//        testKnob.setCurrentValue(action.actionCurrent);
    }
}
