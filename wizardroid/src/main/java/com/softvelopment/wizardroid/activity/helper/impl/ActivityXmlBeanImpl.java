/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity.helper.impl;

import com.softvelopment.wizardroid.activity.helper.ActivityDirection;
import com.softvelopment.wizardroid.activity.helper.ActivityXmlBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author softvelopment
 */
public class ActivityXmlBeanImpl implements ActivityXmlBean{
    
    
   private  String className;
   private  String id;
   private  boolean isStartActivity;
   private boolean isActivityFragment = false;
   private  List<ActivityDirection> backwardScenes = new ArrayList<ActivityDirection>();
   private  List<ActivityDirection> forwardScenes = new ArrayList<ActivityDirection>();
   private Map<String, ArrayList<ActivityDirection>> scenes = new HashMap<String, ArrayList<ActivityDirection>>();
    
 
    public void setClassName(String clazzName) {
       this.className = clazzName;
    }
    
    public String getClassName() {
        return className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("");
        builder.append(" id:" +id);
        builder.append(" classname:" +className);
        builder.append(" isStartActivity:" + isStartActivity);
        builder.append(super.toString());
        return builder.toString();
    }

    public boolean isStartActivity() {
        return isStartActivity;
    }

    public void setIsStartActivity(boolean isStartScene) {
        this.isStartActivity =isStartActivity;
    }

    public ActivityDirection getActivity(String when) {
      
        List<ActivityDirection> allScenesList = new ArrayList<ActivityDirection>(backwardScenes);
      
              for(ActivityDirection directionObj : allScenesList)
            {
                 if(directionObj.getWhen().equalsIgnoreCase(when))
                     return directionObj;
            }
        return null;
     }

    public ActivityDirection getActivity(String direction, String when) {
        ActivityDirection  resultDirection= null;
        List<ActivityDirection> directions = scenes.get(direction);
        
        if(directions != null)
        {
            for(ActivityDirection directionObj : directions)
            {
                 if(directionObj.getWhen().equalsIgnoreCase(when))
                     return directionObj;
            }
        }
           
           
        return resultDirection;
    }

    public List getScenesByType(String direction) {
        if(scenes.get(direction)==null)
            scenes.put(direction, new ArrayList<ActivityDirection>());
        
         return scenes.get(direction);
    }

    public boolean isActivityFragment() {
        return this.isActivityFragment;
    }

    public void setIsActivityFragment(boolean isFragment) {
        this.isActivityFragment = isFragment;
    }

}
