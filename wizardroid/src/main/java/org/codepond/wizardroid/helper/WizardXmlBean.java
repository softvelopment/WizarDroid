package org.codepond.wizardroid.helper;

import com.softvelopment.wizardroid.activity.helper.ActivityXmlBean;

import java.io.Serializable;

/**
 * Created by softvelopment on 5/6/15.
 */
public interface WizardXmlBean extends Serializable{
    public void setWizardFlowXmlBean(WizardFlowXmlBean xmlBean);
    public WizardFlowXmlBean  getWizardFlowXmlBean();

    public void setId(String id);
    public String getId();
}
