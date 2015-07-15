package org.codepond.wizardroid.helper.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by softvelopment on 5/7/15.
 */
public class WizardStepXmlBeanImpl implements org.codepond.wizardroid.helper.WizardStepXmlBean {

    private int stepNumber;
    private String id;
    private boolean hasPrevious;
    private String className;
    private boolean required;
    private Map<String, String> requiredFields = new HashMap<String, String>();


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

    @Override
    public void setIsRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public Map<String, String> getRequiredFields() {
        return requiredFields;
    }

    @Override
    public void setRequiredFields(Map<String, String> fields) {
        requiredFields = fields;
    }
}
