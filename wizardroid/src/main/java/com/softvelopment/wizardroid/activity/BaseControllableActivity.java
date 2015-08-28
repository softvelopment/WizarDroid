/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.softvelopment.wizardroid.activity.helper.ActivityXmlBean;
import com.softvelopment.wizardroid.activity.ActivityManagerController;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author softvelopment
 */
public abstract class BaseControllableActivity extends Activity implements ControllableActivity {

    public static final String DEFAULT_ACTIVITY_NAME = "baseActivity";
    private String when;

    public BaseControllableActivity() {
        super();
        this.promoteToCurrentBean();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int controllerFileKey = savedInstanceState.getInt(ActivityManagerController.ACTIVITY_MANAGER_CONTROLLER_FILENAME_KEY);
        if( controllerFileKey >0)
        {
            if(ActivityManagerController.getInstance().getClasses() == null)
            {
              loadActivityManagerController(controllerFileKey);
            }

        }
    }


    public String getSceneName() {
        return getClass().getName();
    }

    public void moveActivityForward(String when) {
        moveActivity(ActivityConstants.MOVE_ACTIVITY_FORWARD, when);
    }

    public void moveActivityForward() {
        moveActivityForward("*");
    }

    public void moveActivityBackward(String when) {
        moveActivity(ActivityConstants.MOVE_ACTIVITY_BACKWARD, when);
    }

    public void moveActivityBackward() {
        moveActivityBackward("*");
    }

    private void moveActivity(String direction, String when) {
        ActivityXmlBean xmlBean = ActivityManagerController.getInstance().deteremineNextActivityToRun(
                getActivityName(), direction, when);
        if (xmlBean != null) {
            if (!xmlBean.isActivityFragment()) {
                try {
                    Class<?> clazz = Class.forName(xmlBean.getClassName());
                    Intent intent = new Intent(this, clazz);
                    startActivity(intent);
                } catch (ClassNotFoundException cfe) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to move forward fron " + this.getClass().getName() + "  when " + when, cfe);
                }
            }// close out (if... Fragment)

        }
    }

    public void promoteToCurrentBean() {
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getWhen() {
        return when;
    }

    /*
    * default we dont want the back button used
    *  @See http://stackoverflow.com/questions/4779954/disable-back-button-in-android
    */
    
    @Override
    public void onBackPressed() {
    }

    private void loadActivityManagerController(int controllerResourceId)
    {
        try {
            //automatically load the activity for the resournces
            ActivityManagerController.getInstance().loadClassesMap(getResources().openRawResource(controllerResourceId));
        }

        catch(XmlPullParserException xppe)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Unable to parse controller.xml.  The App will be unuseable", xppe);
        }
        catch(IOException ioe)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "IoException reading controller.xml", ioe);
        }
    }

}
