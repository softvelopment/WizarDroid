package org.codepond.wizardroid.helper.util;

import org.codepond.wizardroid.helper.WizardStepXmlBean;

import java.util.Comparator;

/**
 * Created by softvelopment on 5/9/15.
 */
public class WizardStepXmlBeanComparator implements Comparator<WizardStepXmlBean>{
    @Override
    public int compare(WizardStepXmlBean lhs, WizardStepXmlBean rhs) {
        return lhs.getStepNumber() - rhs.getStepNumber();

    }
}
