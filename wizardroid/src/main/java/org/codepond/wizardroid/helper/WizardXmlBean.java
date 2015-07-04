package org.codepond.wizardroid.helper;

import com.softvelopment.wizardroid.activity.helper.ActivityXmlBean;

import java.io.Serializable;

/**
 * Created by softvelopment on 5/6/15.
 */
 public interface WizardXmlBean extends Serializable{
     void setWizardFlowXmlBean(WizardFlowXmlBean xmlBean);
     WizardFlowXmlBean  getWizardFlowXmlBean();

     void setId(String id);
     String getId();

    void setWidth(int width);
    int getWidth();

    void setHeight(int height);
    int getHeight();
    
    
    
}
