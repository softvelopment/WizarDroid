package org.codepond.wizardroid.helper;

import java.util.Map;

/**
 * Created by softvelopment on 5/7/15.
 */
public interface WizardStepXmlBean {

    void setStepNumber(int stepNumber);
    int getStepNumber();

    void setHasPreviousControl(boolean hasPreviousControl);
    boolean hasPreviousControl();

    String getId();
    void setId(String id);

    void setClassName(String className);
    String getClassName();

     void setIsRequired(boolean required);
    boolean isRequired();

    Map<String, String> getRequiredFields();
    void setRequiredFields(Map<String, String> fields);

}
