package org.codepond.wizardroid.helper.impl;

import org.codepond.wizardroid.helper.*;
import org.codepond.wizardroid.helper.WizardStepXmlBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by softvelopment on 5/7/15.
 */
public class WizardFlowXmlBeanImpl implements WizardFlowXmlBean {
    private List<WizardStepXmlBean> wizardSteps = new ArrayList();

    @Override
    public List<WizardStepXmlBean> getWizardSteps() {
        return wizardSteps;
    }

    @Override
    public void setWizardSteps(List<WizardStepXmlBean> xmlBean) {
        this.wizardSteps = xmlBean;
    }
}
