/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity;

/**
 *
 * @author softvelopment
 */
public interface ControllableActivity extends ActivityViewable {
   
    public String getActivityName();
    public void moveActivityFoward();
    public void moveActivityBackward();
    public void promoteToCurrentActivity();
    public void startActivity();
    public void stopActivity();
    public String getWhen();
}
