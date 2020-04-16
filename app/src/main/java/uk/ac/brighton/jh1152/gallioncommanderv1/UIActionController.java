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

    // reflect control classes by generating an instructor and calling it
    private ICustomControl instantiateControl(Activity activity, BoatAction boatAction)
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Class controlClass = getActionClass(boatAction.controlType);
        Constructor constructor = controlClass.getConstructor(Context.class, String.class, String[].class, int.class);
        return (ICustomControl) constructor.newInstance(activity, boatAction.actionName, boatAction.states, boatAction.actionCurrent);
    }

    // which class reprisents the control class
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


    // on construction create UI control object
    public UIActionController(Activity activity, Boat boat, BoatAction boatAction) {
        mBoat = boat;
        mAction = boatAction;
          // find the action clayout container
        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.activitiesGrid);


        try {
            // instantiate the control as the class defined by the object
            mControl =  instantiateControl(activity, boatAction);
            // add it to the view
            layout.addView((LinearLayout) mControl);

            // build listener interface for actions
            mControl.setControlListener(new IControlListener() {
                @Override
                public void onControlChange(int value) {
                    currentValue = value;
                    // respond locally to control actions
                    mControl.setCurrentValue(currentValue);
                }
                // update the document of changes
                @Override
                public void onControlStopTouch() {
                    setBoatAction();
                }

                @Override
                public void onControlStartTouch() {

                }
            });
            //  catch class instatiation errors
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

    // Update document of changes
    public void setBoatAction() {
        mBoat.setActionValue(mAction.documentReference, currentValue);

    }

    // display external document changes
    public void updateDisplay() {
        mControl.setCurrentValue(mAction.actionCurrent);
    }
}
