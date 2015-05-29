/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity.helper;

import com.softvelopment.wizardroid.activity.helper.ActivityDirection;

import java.util.List;

/**
 *
 * @author softvelopment
 */
public interface ActivityXmlBean {
    public String getId();
    public void setId(String id);
    
    public String getClassName();
    public void setClassName(String clazzName);
    
    public boolean isStartActivity();
    public void setIsStartActivity(boolean isStartScene);
   
    public List getScenesByType(String direction);
    
    public ActivityDirection getActivity(String when);
    public ActivityDirection getActivity(String direction, String when);
    
    public boolean isActivityFragment();
    public void setIsActivityFragment(boolean isFragment);
}
