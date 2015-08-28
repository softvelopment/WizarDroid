/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.softvelopment.wizardroid.activity.helper.ActivityDirection;
import com.softvelopment.wizardroid.activity.helper.ActivityXmlBean;
import com.softvelopment.wizardroid.activity.helper.ActivityDirectionType;
import com.softvelopment.wizardroid.activity.helper.impl.ActivityDirectionImpl;
import com.softvelopment.wizardroid.activity.helper.impl.ActivityXmlBeanImpl;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 *
 * @author softvelopment
 */
public class ActivityManagerController {

    private static final String ACTIVITY_ELEMENT_NAME = "activity";
    private static final String FORWARD_ACTIVITY_ELEMENT_NAME = "forward-activity";
    private static final String BACKWARD_ACTIVITY_ELEMENT_NAME = "backward-activity";
    private static final String ID_ATTRIBUTE_NAME = "id";
    private static final String NAME_ATTRIBUTE_NAME = "name";
    private static final String CLASS_ATTRIBUTE_NAME = "class";
    private static final String REF_ATTRIBUTE_NAME = "ref";
    private static final String WHEN_ATTRIBUTE_NAME = "when";
    private static final String REQUIRED_ATTRIBUTE_NAME = "required";
    private static final String FORMAT_ATTRIBUTE_NAME = "format";
    private static final String INITIAL_ACTIVITY_ATTRIBUTE_NAME = "initialActivity";
    private static final String IS_FRAGMENT_ATTRIBUTE_NAME = "isFragment";
    private static final String YES_VALUE = "yes";
    public static final String WILDCARD_CHAR = "*";
    public static final String ACTIVITY_MANAGER_CONTROLLER_FILENAME_KEY ="controller_filename";


    private Map<String, ActivityXmlBean> classes = null;
    private boolean isStarted = false;
    private static ActivityManagerController _instance = null;

    private  ActivityManagerController() {
        classes = new Hashtable<String, ActivityXmlBean>();

    }

    public static synchronized ActivityManagerController getInstance()
    {
        if(_instance == null)
        {
            _instance = new ActivityManagerController();
        }

        return _instance;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setIsStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }



    public void loadClassesMap(InputStream instream) throws IOException, XmlPullParserException {
        try {
                           XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(instream, ActivityConstants.UTF_8_ENCODING);

                int eventType = parser.getEventType();

                ActivityXmlBean tmpBean = null;

                while (eventType != XmlResourceParser.END_DOCUMENT) {
                    if (eventType == XmlResourceParser.START_TAG) {
                        if (parser.getName().equalsIgnoreCase(ACTIVITY_ELEMENT_NAME)) {
                            tmpBean = (ActivityXmlBean) new ActivityXmlBeanImpl();
                            tmpBean.setId(parser.getAttributeValue(null, ID_ATTRIBUTE_NAME));
                            tmpBean.setClassName(parser.getAttributeValue(null, CLASS_ATTRIBUTE_NAME));
                            String startSceneValue = parser.getAttributeValue(null, INITIAL_ACTIVITY_ATTRIBUTE_NAME);
                            if ((startSceneValue != null) && (startSceneValue.equalsIgnoreCase(YES_VALUE))) {
                                tmpBean.setIsStartActivity(true);
                            } else {
                                tmpBean.setIsStartActivity(false);
                            }
                            
                             String isFragmentValue = parser.getAttributeValue(null, IS_FRAGMENT_ATTRIBUTE_NAME);
                             if ((isFragmentValue != null) && (isFragmentValue.equalsIgnoreCase(YES_VALUE))) {
                                tmpBean.setIsActivityFragment(true);
                            } else {
                                tmpBean.setIsActivityFragment(false);
                            }

                        } else if ((parser.getName().equalsIgnoreCase(FORWARD_ACTIVITY_ELEMENT_NAME))
                                || (parser.getName().equalsIgnoreCase(BACKWARD_ACTIVITY_ELEMENT_NAME))) {
                            ActivityDirection direction = new ActivityDirectionImpl();
                            direction.setWhen(parser.getAttributeValue(null, WHEN_ATTRIBUTE_NAME));
                            if (parser.getName().equalsIgnoreCase(FORWARD_ACTIVITY_ELEMENT_NAME)) {
                                direction.setActivityDirectionType(ActivityDirectionType.forwardActivity);
                            } else {
                                direction.setActivityDirectionType(ActivityDirectionType.backwardActivity);
                            }

                            String tmpSceneName = null;
                            if (parser.getAttributeValue(null, REF_ATTRIBUTE_NAME) != null) {
                                tmpSceneName = parser.getAttributeValue(null, REF_ATTRIBUTE_NAME);
                            } else if (parser.getAttributeValue(null, NAME_ATTRIBUTE_NAME) != null) {
                                tmpSceneName = parser.getAttributeValue(null, NAME_ATTRIBUTE_NAME);
                            }
                            direction.setActivityName(tmpSceneName);

                            if (parser.getAttributeValue(null, REQUIRED_ATTRIBUTE_NAME) != null) {
                                //parse comma sepearted names for value
                                String requiredFields = parser.getAttributeValue(null, REQUIRED_ATTRIBUTE_NAME);
                                String[] requiredFieldsList = requiredFields.split(",");
                                //here add values to the list;
                                for (String requiredField : requiredFieldsList) {
                                    direction.getRequiredFieldIds().add(requiredField);
                                }//close out for(String require...
                            }

                            if (parser.getAttributeValue(null, FORMAT_ATTRIBUTE_NAME) != null) {
                                //parse comma sepearted names for value
                                String formatFields = parser.getAttributeValue(null, FORMAT_ATTRIBUTE_NAME);
                                String[] formatFieldsList = formatFields.split(",");
                                //here add values to the list;
                                for (String formatField : formatFieldsList) {
                                    //only add if format is field=formatType
                                    String[] fieldFormatData = formatField.split("=");
                                    if (fieldFormatData.length == 2) {
                                        direction.getFieldFormats().put(fieldFormatData[0], fieldFormatData[1]);
                                    }
                                }//close out for(String require...
                            }

                            if ((parser.getName().equalsIgnoreCase(FORWARD_ACTIVITY_ELEMENT_NAME))
                                    || (parser.getName().equalsIgnoreCase(BACKWARD_ACTIVITY_ELEMENT_NAME))) {
                                tmpBean.getScenesByType(parser.getName()).add(direction);
                            }

                          

                        }
                    } else if (eventType == XmlResourceParser.END_TAG) {
                        if (parser.getName().equalsIgnoreCase(ACTIVITY_ELEMENT_NAME)) {
                            if (tmpBean != null) {
                                if (tmpBean.getId() != null) {
                                    classes.put(tmpBean.getId(), tmpBean);
                                }
                            }
                        }//close out if(parser.getName...

                    }
                    eventType = parser.next();
                }
                instream.close();

        } catch (IOException ioe) {
            Log.e(getClass().getName(), "unable to load activity map, app is unusable", ioe);
            throw ioe;

        } catch (XmlPullParserException xppe) {
           Log.e(getClass().getName(),"unable to load activity map, app is unusable", xppe);
            throw xppe;

        }


    }

    public ActivityXmlBean deteremineNextActivityToRun(String activityId, String direction, String reason) {
        ActivityXmlBean beanToRun = null;

        ActivityXmlBean currentBean = classes.get(activityId);
        if (currentBean != null) {

            List<ActivityDirection> beans = currentBean.getScenesByType(direction);

            for (ActivityDirection sceneDirection : beans) {
                if ((sceneDirection.getWhen().equalsIgnoreCase(reason))
                        || (sceneDirection.getWhen().equalsIgnoreCase(WILDCARD_CHAR))) {
                    beanToRun = this.classes.get(sceneDirection.getActivityName());

                    break;
                }
            }//close out for

        }//close if currrentBean != null
        return beanToRun;
    }

    private ActivityXmlBean determineIntialActivity() {
        //find the first object with isIntialized = true;

        if (this.classes != null) {
            Iterator<Entry<String, ActivityXmlBean>> iterator = classes.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, ActivityXmlBean> tmpEntry = iterator.next();
                ActivityXmlBean tmpBean = tmpEntry.getValue();
                if (tmpBean.isStartActivity() == true) {
                    return tmpBean;
                }
            }

        }
        return null;
    }

    public ActivityXmlBean getActivity(String id) {
        if (this.classes != null) {
            return this.classes.get(id);
        }
        return null;
    }

   public Map<String, ActivityXmlBean> getClasses()
   {
       return classes;
   }
}
