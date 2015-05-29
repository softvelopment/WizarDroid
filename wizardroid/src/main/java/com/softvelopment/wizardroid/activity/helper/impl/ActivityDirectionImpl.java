/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity.helper.impl;

import com.softvelopment.wizardroid.activity.helper.ActivityDirection;
import com.softvelopment.wizardroid.activity.helper.ActivityDirectionType;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author softvelopment
 */
public class ActivityDirectionImpl implements ActivityDirection {
     private String when;
    private String sceneName;
    private ActivityDirectionType direction;
    private List<String> requiredFields = new ArrayList<String>();
    private Map<String, String> formatFields = new HashMap<String, String>();
    
    public String getActivityName() {
        return sceneName;
    }

    public void setActivityName(String sceneName) {
        this.sceneName =sceneName;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getWhen() {
       return when;
    }

    public void setActivityDirectionType(ActivityDirectionType directionType) {
    
        this.direction = directionType;
    }

    public ActivityDirectionType getActivityDirectionType() {
        return direction;
    }
    
    public ActivityDirectionImpl()
    {
        
    }
    
    public ActivityDirectionImpl(String sceneName, String when, ActivityDirectionType direction)
    {
        this.when = when;
        this.sceneName = sceneName;
        this.direction = direction;
    }

    public List<String> getRequiredFieldIds() {
        return requiredFields;
    }

    public void setRequiredFieldIds(List<String> list) {
        this.requiredFields = list;

    }

    public Map<String, String> getFieldFormats() {
        return this.formatFields;
    }
}
