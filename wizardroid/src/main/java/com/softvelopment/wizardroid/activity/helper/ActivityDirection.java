/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity.helper;

import java.util.List;
import java.util.Map;

/**
 *
 * @author softvelopment
 */
public interface ActivityDirection {
    public String getActivityName();
    public void setActivityName(String sceneName);   
    
    public void setWhen(String when);
    public String getWhen();
    
    public List<String>getRequiredFieldIds();
    public void setRequiredFieldIds(List<String> list);
    
    public Map<String, String>getFieldFormats();
    
    public void setActivityDirectionType(ActivityDirectionType directionType);
    public ActivityDirectionType getActivityDirectionType();
}
