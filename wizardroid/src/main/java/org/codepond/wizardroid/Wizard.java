package org.codepond.wizardroid;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import com.softvelopment.wizardroid.activity.ActivityConstants;
import com.softvelopment.wizardroid.activity.helper.ActivityDirection;
import com.softvelopment.wizardroid.activity.helper.ActivityDirectionType;
import com.softvelopment.wizardroid.activity.helper.ActivityXmlBean;
import com.softvelopment.wizardroid.activity.helper.impl.ActivityDirectionImpl;
import com.softvelopment.wizardroid.activity.helper.impl.ActivityXmlBeanImpl;

import org.codepond.wizardroid.helper.WizardFlowXmlBean;
import org.codepond.wizardroid.helper.WizardStepXmlBean;
import org.codepond.wizardroid.helper.WizardXmlBean;
import org.codepond.wizardroid.helper.impl.WizardStepXmlBeanImpl;
import org.codepond.wizardroid.helper.impl.WizardXmlBeanImpl;
import org.codepond.wizardroid.helper.util.WizardStepXmlBeanComparator;
import org.codepond.wizardroid.infrastructure.Bus;
import org.codepond.wizardroid.infrastructure.Disposable;
import org.codepond.wizardroid.infrastructure.Subscriber;
import org.codepond.wizardroid.infrastructure.events.StepCompletedEvent;
import org.codepond.wizardroid.persistence.ContextManager;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * The engine of the Wizard. This class controls the flow of the wizard
 * and is using {@link ViewPager} under the hood. You would normally want to
 * extend {@link WizardFragment} instead of using this class directly and make calls to the wizard API
 * via {@link org.codepond.wizardroid.WizardFragment#wizard} field. Use this
 * class only if you wish to create a custom WizardFragment to control the wizard.
 */
public class Wizard implements Disposable, Subscriber {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Interface for key wizard events. Implement this interface if you wish to create
     * a custom WizardFragment.
     */
    public static interface WizardCallbacks {
        /**
         * Event called when the wizard is completed
         */
        public void onWizardComplete();

        /**
         * Event called after a step was changed
         */
        public void onStepChanged();
    }

	private static final boolean DEBUG = false;
    private static final String TAG = Wizard.class.getSimpleName();
	private final WizardFlow wizardFlow;
    private final ContextManager contextManager;
    private final WizardCallbacks callbacks;
    private final ViewPager mPager;
    private final FragmentManager mFragmentManager;
    private int backStackEntryCount;
	private WizardStep mPreviousStep;
	private int mPreviousPosition;
    private String id;

	/**
     * Constructor for Wizard
     * @param wizardFlow WizardFlow instance. See WizardFlow.Builder for more information on creating WizardFlow objects.
     * @param contextManager ContextManager instance would normally be {@link org.codepond.wizardroid.persistence.ContextManagerImpl}
     * @param callbacks implementation of WizardCallbacks
     * @param activity the hosting activity
     */
	protected Wizard(final WizardFlow wizardFlow,
                  final ContextManager contextManager,
                  final WizardCallbacks callbacks,
                  final FragmentActivity activity) {
		this.wizardFlow = wizardFlow;
        this.contextManager = contextManager;
        this.callbacks = callbacks;
        this.mPager = (ViewPager) activity.findViewById(R.id.step_container);
        this.mFragmentManager = activity.getSupportFragmentManager();

        if (mPager == null) {
            throw new RuntimeException("Cannot initialize Wizard. View with ID: step_container not found!" +
                    " The hosting Activity/Fragment must have a ViewPager in its layout with ID: step_container");
        }

        mPager.setAdapter(new WizardPagerAdapter(activity.getSupportFragmentManager()));

        backStackEntryCount = mFragmentManager.getBackStackEntryCount();
        mFragmentManager.addOnBackStackChangedListener(new OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                backStackEntryCount = mFragmentManager.getBackStackEntryCount();

                //onBackPressed
                if (backStackEntryCount < getCurrentStepPosition()){
                    mPager.setCurrentItem(getCurrentStepPosition() - 1);
                }
            }
        });

        //Implementation of OnPageChangeListener to handle wizard control via user finger slides
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			private int mPreviousState = ViewPager.SCROLL_STATE_IDLE;

			@Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (backStackEntryCount < position){
                    mFragmentManager.beginTransaction().addToBackStack(null).commit();
                }
                else if (backStackEntryCount > position){
                    mFragmentManager.popBackStack();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
				if (DEBUG) Log.v(TAG, "onPageScrollStateChanged " + state);
				switch (state) {
					case ViewPager.SCROLL_STATE_DRAGGING:
						mPreviousPosition = getCurrentStepPosition();
						mPreviousStep = getCurrentStep();
						break;
					case ViewPager.SCROLL_STATE_SETTLING:
						callbacks.onStepChanged();
						break;
					case ViewPager.SCROLL_STATE_IDLE:
						if (mPreviousState == ViewPager.SCROLL_STATE_SETTLING) {
							if (getCurrentStepPosition() > mPreviousPosition) {
								if (DEBUG) Log.v(TAG, "goNext");
								processStepBeforeChange(mPreviousStep, mPreviousPosition);
								mPager.getAdapter().notifyDataSetChanged();
							}
							else {
								if (DEBUG) Log.v(TAG, "goBack");
								mPreviousStep.onExit(WizardStep.EXIT_PREVIOUS);
							}
						}
						break;
				}
				mPreviousState = state;
            }
        });
        Bus.getInstance().register(this, StepCompletedEvent.class);
	}

    @Override
    public void dispose() {
        Bus.getInstance().unregister(this);
    }

    @Override
    public void receive(Object event) {
        StepCompletedEvent stepCompletedEvent = (StepCompletedEvent) event;
        onStepCompleted(stepCompletedEvent.isStepCompleted(), stepCompletedEvent.getStep());
    }

    private void onStepCompleted(boolean isComplete, WizardStep step) {
        if (step != getCurrentStep()) return;
		int stepPosition = getCurrentStepPosition();


        // Check that the step is not already in this state to avoid spamming the viewpager
        if (wizardFlow.isStepCompleted(stepPosition) != isComplete) {
            wizardFlow.setStepCompleted(stepPosition, isComplete);
            mPager.getAdapter().notifyDataSetChanged();
            //Refresh the UI
            callbacks.onStepChanged();
        }
    }

	private void processStepBeforeChange(WizardStep step, int position) {
		step.onExit(WizardStep.EXIT_NEXT);
		wizardFlow.setStepCompleted(position, true);
		contextManager.persistStepContext(step);
	}

    /**
	 * Advance the wizard to the next step
	 */
	public void goNext() {
		if (canGoNext()) {
			if (isLastStep()) {
				processStepBeforeChange(getCurrentStep(), getCurrentStepPosition());
				callbacks.onWizardComplete();
			}
			else {
				mPreviousPosition = getCurrentStepPosition();
				mPreviousStep = getCurrentStep();
				setCurrentStep(mPager.getCurrentItem() + 1);
			}
		}
    }

    /**
	 * Takes the wizard one step back
	 */
	public void goBack() {
		if (!isFirstStep()) {
			setCurrentStep(mPager.getCurrentItem() - 1);
		}
	}
	
	/**
	 * Sets the current step of the wizard
	 * @param stepPosition the position of the step within the WizardFlow
	 */
	public void setCurrentStep(int stepPosition) {
        mPager.setCurrentItem(stepPosition);
	}
	
	/**
	 * Gets the current step position
	 * @return integer representing the position of the step in the WizardFlow
	 */
    public int getCurrentStepPosition() {
		return mPager.getCurrentItem();
	}
	
	/**
	 * Gets the current step
	 * @return WizardStep the current WizardStep instance
	 */
    public WizardStep getCurrentStep() {
        return ((WizardPagerAdapter)mPager.getAdapter()).getPrimaryItem();
	}
	
	/**
	 * Checks if the current step is the last step in the Wizard
	 * @return boolean representing the result of the check
	 */
    public boolean isLastStep() {
		return mPager.getCurrentItem() == wizardFlow.getStepsCount() - 1;
	}
	
	/**
	 * Checks if the step is the first step in the Wizard
	 * @return boolean representing the result of the check
	 */
	public boolean isFirstStep() {
		return mPager.getCurrentItem() == 0;
	}

    /**
     * Check if the wizard can proceed to the next step by verifying that the current step
     * is completed
     */
    public boolean canGoNext() {
        int stepPosition = getCurrentStepPosition();
        if (wizardFlow.isStepRequired(stepPosition)) {
            return wizardFlow.isStepCompleted(stepPosition);
        }
        return true;
    }

    /**
     * Custom adapter for the ViewPager
     */
    public class WizardPagerAdapter extends FragmentStatePagerAdapter {

        private Fragment mPrimaryItem;

        public WizardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            try {
                WizardStep step = wizardFlow.getSteps().get(i).newInstance();
                contextManager.loadStepContext(step);
                return step;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object.equals(mPreviousStep)) {
                return POSITION_UNCHANGED;
            }
            else {
                return POSITION_NONE;
            }
        }

        @Override
        public int getCount() {
            return wizardFlow.getSteps().size();
        }

        public WizardStep getPrimaryItem() {
            return (WizardStep) mPrimaryItem;
        }
    }

    public static class Builder
    {
        private Wizard wizard = null;

        public Wizard buildWizard(String wizardName, InputStream instream)throws IOException, XmlPullParserException
        {
                WizardXmlBean wizardXmlBean = loadWizardFromXml(wizardName, instream);
            if(wizardXmlBean != null)
            {
                WizardFlow wizardFlow = null;
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
                    wizard = new Wizard(wizardFlow, null, null, null);
                }
            }


            return wizard;
        }

        private WizardXmlBean loadWizardFromXml(String wizardName, InputStream instream) throws IOException, XmlPullParserException {

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(instream, ActivityConstants.UTF_8_ENCODING);
                parser.nextTag();
                return readWizardFromXML(wizardName, parser);

            } catch (IOException ioe) {
                Log.e(TAG, "unable to load services from service controller", ioe);
                throw ioe;

            } catch (XmlPullParserException xppe) {
               Log.e(TAG, "unable to load services from service controller", xppe);
                throw xppe;
            } finally {
                instream.close();
            }

    }



        private WizardXmlBean readWizardFromXML(String wizardName, XmlPullParser parser) throws IOException, XmlPullParserException {

            WizardXmlBean wizardXmlBean = new WizardXmlBeanImpl();

            parser.require(XmlPullParser.START_TAG, null,ActivityConstants.WIZARD_NAME);
            String tag = parser.getName();
            if ((tag.equalsIgnoreCase(ActivityConstants.WIZARD_NAME)) &&(wizardName.equalsIgnoreCase(parser.getAttributeValue(null, ActivityConstants.ID_NAME)))) {
                wizardXmlBean.setId(parser.getAttributeValue(null, ActivityConstants.ID_NAME));
                Log.d(TAG, wizardName +" -" + parser.getName());
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
                try
                {
                    wizardStepXmlBean.setStepNumber(Integer.parseInt(parser.getAttributeValue(null, ActivityConstants.STEP_NAME)));
                    wizardStepXmlBean.setHasPreviousControl(Boolean.parseBoolean(parser.getAttributeValue(null, ActivityConstants.HAS_PREV_NAME)));
                }
                catch(NumberFormatException nfe)
                {
                    Log.w(TAG, "Unable to convert value " + parser.getAttributeValue(null, ActivityConstants.STEP_NAME) +"to a number", nfe);
                }

            }
            parser.require(XmlPullParser.END_TAG, null, ActivityConstants.FRAGMENT_ACTIVITY_NAME);
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


    }
}