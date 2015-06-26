/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity;

import android.app.Activity;
import android.content.Intent;
import com.softvelopment.wizardroid.activity.helper.ActivityXmlBean;
import com.softvelopment.wizardroid.activity.ActivityManagerController;
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
            else
            {
                //keep current activity but swtich to fragment
                
            }
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

}
