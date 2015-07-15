package org.codepond.wizardroid.layouts;

import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.softvelopment.wizardroid.activity.ActivityConstants;
import com.softvelopment.wizardroid.activity.helper.ActivityValidateResult;

import org.codepond.wizardroid.R;
import org.codepond.wizardroid.ValidatableWizard;
import org.codepond.wizardroid.WizardFlow;
import org.codepond.wizardroid.WizardStep;
import org.codepond.wizardroid.WizardStepValidatable;
import org.codepond.wizardroid.helper.WizardStepXmlBean;
import org.codepond.wizardroid.helper.WizardXmlBean;
import org.codepond.wizardroid.helper.impl.WizardStepXmlBeanImpl;
import org.codepond.wizardroid.helper.impl.WizardXmlBeanImpl;
import org.codepond.wizardroid.helper.util.WizardStepXmlBeanComparator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by softvelopment on 7/3/15.
 *
 *  This class builds on the BasicWizardLayout class with the following
 *  improvements
 *
 *  Child claases that inherit from this should not have to implement/override
 *  onSetup() but should just override 2 methods
 *
 *  getWizardName  The name of the wizard in the wizard xml file
 *  getWizardFileResourceId  The id of the wizard file resource
 *
 */
public abstract class SoftvelopmentWizardLayout extends BasicWizardLayout implements ValidatableWizard {

    private static final String TAG = SoftvelopmentWizardLayout.class.getSimpleName();

    public abstract String getWizardName();
    public abstract int getWizardFileResourceId();

    protected int width=0;
    protected int height=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View wizardLayout = super.onCreateView(inflater,container,savedInstanceState);
        if((width > 0) &&(height >0 ))
        {
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(width, height);
            wizardLayout.setLayoutParams(p);
            wizardLayout.requestLayout();
        }
        return wizardLayout;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.wizard_next_button) {
            WizardStep wizardStep = wizard.getCurrentStep();
            if(wizardStep instanceof WizardStepValidatable)
            {
                //validate before going next
                WizardStepValidatable validatableWizardStep = ( WizardStepValidatable)wizardStep;
                ActivityValidateResult validationResult = validatableWizardStep.validateWizardStep(getView());
                if(validationResult.isValid())
                {
                    //validated now we can go next
                    wizard.goNext();
                }
                else
                {
                    //just bind the errors
                    validatableWizardStep.bindFormErrors(getView(), validationResult);
                }
            }
            else {
                //Tell the wizard to go to next step
                wizard.goNext();
            }
        }
        else if (v.getId() == R.id.wizard_previous_button) {
            //Tell the wizard to go back one step
            wizard.goBack();
        }
    }

    @Override
    public WizardFlow onSetup()
    {
        if(this.getActivity() != null)
        {
            try {
                return buildWizard(getWizardName(), getActivity().getResources().openRawResource(getWizardFileResourceId()));
            }
            catch(XmlPullParserException xppe)
            {
                Log.e(TAG, "Unable to create wizard with name " + getWizardName(),xppe);
            }
            catch(IOException ioe)
            {
                Log.e(TAG, "Unable to create wizard with name " + getWizardName(),ioe);
            }
        }
        return null;
    }

    public WizardFlow buildWizard(String wizardName, InputStream instream)throws IOException, XmlPullParserException
    {
        WizardXmlBean wizardXmlBean = loadWizardFromXml(wizardName, instream);
        WizardFlow wizardFlow = null;
        if(wizardXmlBean != null)
        {

            WizardFlow.Builder wizardFlowBuilder = new WizardFlow.Builder();
            if((wizardXmlBean.getWizardFlowXmlBean().getWizardSteps() != null) &&(wizardXmlBean.getWizardFlowXmlBean().getWizardSteps().size()>0))
            {
                try {
                    //sort the wizard steps
                    Collections.sort(wizardXmlBean.getWizardFlowXmlBean().getWizardSteps(), new WizardStepXmlBeanComparator());
                    for(WizardStepXmlBean stepXmlBean:wizardXmlBean.getWizardFlowXmlBean().getWizardSteps())
                    {

                        wizardFlowBuilder.addStep((Class<? extends WizardStep>)Class.forName(stepXmlBean.getClassName()));

                    }
                }
                catch(ClassNotFoundException cnfe)
                {
                    Log.w(TAG, "Cannot find class for ",cnfe);
                }
                catch(ClassCastException cce)
                {
                    Log.w(TAG, "Cannot cast class to Wizard Step Class", cce);
                }
                wizardFlow = wizardFlowBuilder.create();

            }
        }


        return wizardFlow;
    }


    private WizardXmlBean loadWizardFromXml(String wizardName, InputStream instream) throws IOException, XmlPullParserException {

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(instream, ActivityConstants.UTF_8_ENCODING);

            int eventType = parser.getEventType();

            while (eventType != XmlResourceParser.END_DOCUMENT) {
                if (eventType == XmlResourceParser.START_TAG) {
                    if (parser.getName().equalsIgnoreCase(ActivityConstants.WIZARD_NAME))
                    {
                        if(wizardName.equalsIgnoreCase(parser.getAttributeValue(null, ActivityConstants.ID_NAME)))
                        {
                            return readWizardFromXML(wizardName, parser);
                        }
                    }
                }
                eventType = parser.next();
                }
            return null;

        } catch (IOException ioe) {
            Log.e(TAG, "unable to load wizard from wizard xml", ioe);
            throw ioe;

        } catch (XmlPullParserException xppe) {
            Log.e(TAG, "unable to load wizard from wizard xml", xppe);
            throw xppe;
        } finally {
            instream.close();
        }

    }


    private WizardXmlBean readWizardFromXML(String wizardName, XmlPullParser parser) throws IOException, XmlPullParserException {

        WizardXmlBean wizardXmlBean = new WizardXmlBeanImpl();

        parser.require(XmlPullParser.START_TAG, null, ActivityConstants.WIZARD_NAME);
        String tag = parser.getName();
        if ((tag.equalsIgnoreCase(ActivityConstants.WIZARD_NAME)) &&(wizardName.equalsIgnoreCase(parser.getAttributeValue(null, ActivityConstants.ID_NAME)))) {
            wizardXmlBean.setId(parser.getAttributeValue(null, ActivityConstants.ID_NAME));
            try
            {
                wizardXmlBean.setHeight(parser.getAttributeValue(null, ActivityConstants.HEIGHT_NAME) == null ? 0 : Integer.parseInt(parser.getAttributeValue(null, ActivityConstants.HEIGHT_NAME)));
                wizardXmlBean.setWidth(parser.getAttributeValue(null, ActivityConstants.WIDTH_NAME)==null?0:Integer.parseInt(parser.getAttributeValue(null, ActivityConstants.WIDTH_NAME)));
                width = wizardXmlBean.getWidth();
                height = wizardXmlBean.getHeight();
            }
            catch(NumberFormatException nfe)
            {
                Log.w(TAG, "unable to set dimensions for wizard " + wizardName + " using normal dimensions");
            }
            Log.d(TAG, wizardName + " -" + parser.getName());
            while (parser.nextTag() != XmlPullParser.END_TAG) {
                if (parser.getName()
                        .equalsIgnoreCase(ActivityConstants.FRAGMENT_ACTIVITY_NAME)) {
                    wizardXmlBean.getWizardFlowXmlBean().getWizardSteps().add(readWizardStepsFromXML(parser));
                } else {
                    skip(parser);
                }
            }
        }
        return wizardXmlBean;


    }

    private WizardStepXmlBean readWizardStepsFromXML(XmlPullParser parser) throws IOException, XmlPullParserException {
        WizardStepXmlBean wizardStepXmlBean = null;
        parser.require(XmlPullParser.START_TAG, null, ActivityConstants.FRAGMENT_ACTIVITY_NAME);
        String tag = parser.getName();
        if (tag.equalsIgnoreCase(ActivityConstants.FRAGMENT_ACTIVITY_NAME)){
            wizardStepXmlBean = new WizardStepXmlBeanImpl();
            wizardStepXmlBean.setId(parser.getAttributeValue(null, ActivityConstants.ID_NAME));
            wizardStepXmlBean.setClassName(parser.getAttributeValue(null, ActivityConstants.CLASS_NAME));
            boolean bRequired = true;
           String requiredString =  parser.getAttributeValue(null, ActivityConstants.REQUIRED);
            if((requiredString != null)&&(requiredString.equalsIgnoreCase(Boolean.FALSE.toString())))
            {
                bRequired = false;
            }
            wizardStepXmlBean.setIsRequired(bRequired);
            String requiredFields =parser.getAttributeValue(null, ActivityConstants.REQUIRED_FIELDS);
            if((requiredFields != null) &&(requiredFields.length()>0))
            {
                wizardStepXmlBean.setRequiredFields(findRequiredFieldsFromAttrString(requiredFields));
            }
            try
            {
                wizardStepXmlBean.setStepNumber(Integer.parseInt(parser.getAttributeValue(null, ActivityConstants.STEP_NAME)));
                wizardStepXmlBean.setHasPreviousControl(Boolean.parseBoolean(parser.getAttributeValue(null, ActivityConstants.HAS_PREV_NAME)));
            }
            catch(NumberFormatException nfe)
            {
                Log.w(TAG, "Unable to convert value " + parser.getAttributeValue(null, ActivityConstants.STEP_NAME) +"to a number", nfe);
            }
            parser.nextTag();
        }
        return wizardStepXmlBean;
    }


    //@see http://developer.android.com/training/basics/network-ops/xml.html#analyze
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Map<String, String> findRequiredFieldsFromAttrString(String requiredFieldsString)
    {
        Map<String, String> resultMap = new HashMap<String, String>();
        String [] requiredFieldsList = requiredFieldsString.split(",");
        for(String requiredField : requiredFieldsList)
        {
            String [] requiredFieldInfo = requiredField.split(":");
            if(requiredFieldInfo.length ==2 )
            {
                resultMap.put(requiredFieldInfo[0],requiredFieldInfo[1]);
            }
        }
        return resultMap;
    }
}
