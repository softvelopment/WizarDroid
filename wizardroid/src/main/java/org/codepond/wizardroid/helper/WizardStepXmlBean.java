package org.codepond.wizardroid.helper;

/**
 * Created by softvelopment on 5/7/15.
 */
public interface WizardStepXmlBean {

    public void setStepNumber(int stepNumber);
    public int getStepNumber();

    public void setHasPreviousControl(boolean hasPreviousControl);
    public boolean hasPreviousControl();

    public String getId();
    public void setId(String id);

    public void setClassName(String className);
    public String getClassName();

    public  void setIsRequired(boolean required);
    public boolean isRequired();

}
