package org.codepond.wizardroid.sample.wizards;

import android.view.View;

import com.softvelopment.wizardroid.activity.helper.ActivityValidateResult;

import org.codepond.wizardroid.WizardFlow;
import org.codepond.wizardroid.layouts.BasicWizardLayout;
import org.codepond.wizardroid.layouts.SoftvelopmentWizardLayout;
import org.codepond.wizardroid.persistence.ContextVariable;
import org.codepond.wizardroid.sample.steps.FormStep1;
import org.codepond.wizardroid.sample.steps.FormStep2;
import org.codepond.wizardroid.sample.steps.FormStep3;

/**
 * A sample to demonstrate a form in multiple steps.
 */
public class FormWizard extends SoftvelopmentWizardLayout {

    /**
     * Tell WizarDroid that these are context variables and set default values.
     * These values will be automatically bound to any field annotated with {@link ContextVariable}.
     * NOTE: Context Variable names are unique and therefore must
     * have the same name and type wherever you wish to use them.
     */
    @ContextVariable
    private String firstname = "WizarDroid";
    @ContextVariable
    private String lastname = "CondPond.org";

    public FormWizard() {
        super();
    }

    @Override
    public String getWizardName() {
        return "form_wizard";
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
        //... Access context variables here before terminating the wizard
        //...
        //String fullname = firstname + lastname;
        //Store the data in the DB or pass it back to the calling activity
        getActivity().finish();     //Terminate the wizard
    }

}
