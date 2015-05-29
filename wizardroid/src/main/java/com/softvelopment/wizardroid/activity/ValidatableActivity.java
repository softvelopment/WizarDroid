/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity;

import android.view.View;
import com.softvelopment.wizardroid.activity.helper.ActivityValidateResult;

/**
 *
 * @author softvelopment
 */
public interface ValidatableActivity {
    
    public ActivityValidateResult validateActivity(View view, String when);
   
}
