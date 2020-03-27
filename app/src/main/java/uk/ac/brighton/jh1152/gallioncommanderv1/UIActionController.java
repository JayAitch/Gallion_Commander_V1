package uk.ac.brighton.jh1152.gallioncommanderv1;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class UIActionController {

    int currentValue;
    Boat mBoat;
    BoatAction mAction;
    ICustomControl mControl;

    //https://stackoverflow.com/questions/3671649/java-newinstance-of-class-that-has-no-default-constructor
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



    public UIActionController(Activity activity, Boat boat, BoatAction boatAction) {
        mBoat = boat;
        mAction = boatAction;

        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.activitiesGrid);


        try {
            mControl =  instantiateControl(activity, boatAction);
            layout.addView((LinearLayout) mControl);
            mControl.setControlListener(new IControlListener() {
                @Override
                public void onControlChange(int value) {
                    currentValue = value;
                    mControl.setCurrentValue(currentValue);
                }

                @Override
                public void onControlStopTouch() {
                    setBoatAction();
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

    }


    public void setBoatAction() {
        mBoat.setActionValue(mAction.documentReference, currentValue);

    }


    public void updateDisplay() {
        mControl.setCurrentValue(mAction.actionCurrent);
    }
}
