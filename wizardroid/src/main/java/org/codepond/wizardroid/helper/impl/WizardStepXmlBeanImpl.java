package org.codepond.wizardroid.helper.impl;

/**
 * Created by softvelopment on 5/7/15.
 */
public class WizardStepXmlBeanImpl implements org.codepond.wizardroid.helper.WizardStepXmlBean {

    private int stepNumber;
    private String id;
    private boolean hasPrevious;
    private String className;


    @Override
    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    @Override
    public int getStepNumber() {
        return stepNumber;
    }

    @Override
    public void setHasPreviousControl(boolean hasPreviousControl) {
        this.hasPrevious = hasPreviousControl;
    }

    @Override
    public boolean hasPreviousControl() {
        return hasPrevious;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String getClassName() {
        return className;
    }
}
