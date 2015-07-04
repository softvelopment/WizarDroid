package org.codepond.wizardroid.helper.impl;

import com.softvelopment.wizardroid.activity.helper.impl.ActivityXmlBeanImpl;

import org.codepond.wizardroid.helper.WizardFlowXmlBean;
import org.codepond.wizardroid.helper.WizardXmlBean;

/**
 * Created by softvelopment on 5/6/15.
 */
public class WizardXmlBeanImpl implements WizardXmlBean {
    private String id;
    private WizardFlowXmlBean xmlWizardFlow = new WizardFlowXmlBeanImpl();
    private int width;
    private int height;

    @Override
    public void setWizardFlowXmlBean(WizardFlowXmlBean xmlBean) {
        this.xmlWizardFlow = xmlBean;
    }

    @Override
    public WizardFlowXmlBean getWizardFlowXmlBean() {
        return this.xmlWizardFlow;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
