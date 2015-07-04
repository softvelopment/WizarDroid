package org.codepond.wizardroid;

import android.app.Instrumentation;
import android.view.View;

import com.softvelopment.wizardroid.activity.helper.ActivityValidateResult;

/**
 * Created by softvelopment on 7/3/15.
 */
public interface WizardValidatable {
     public ActivityValidateResult validateWizardStep(View view);
}
