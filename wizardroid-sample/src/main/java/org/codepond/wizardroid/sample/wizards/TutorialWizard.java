package org.codepond.wizardroid.sample.wizards;

import android.view.View;

import com.softvelopment.wizardroid.activity.helper.ActivityValidateResult;

import org.codepond.wizardroid.WizardFlow;
import org.codepond.wizardroid.layouts.BasicWizardLayout;
import org.codepond.wizardroid.layouts.SoftvelopmentWizardLayout;
import org.codepond.wizardroid.sample.steps.TutorialStep1;
import org.codepond.wizardroid.sample.steps.TutorialStep2;

public class TutorialWizard extends SoftvelopmentWizardLayout {

    /**
     * Note that we inherit from {@link android.support.v4.app.Fragment} and therefore must have an empty constructor
     */
    public TutorialWizard() {
        super();
    }

    @Override
    public String getWizardName() {
        return "tutorial_wizard";
    }

    @Override
    public int getWizardFileResourceId() {
        return org.codepond.wizardroid.sample.R.raw.wizards;
    }


    /*
        You'd normally override onWizardComplete to access the wizard context and/or close the wizard
     */
    @Override
    public void onWizardComplete() {
        super.onWizardComplete();   //Make sure to first call the super method before anything else
        getActivity().finish();     //Terminate the wizard
    }

}