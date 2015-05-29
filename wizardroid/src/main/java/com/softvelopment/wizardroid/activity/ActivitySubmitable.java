/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity;

import android.view.View;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;

/**
 *
 * @author softvelopment
 */
public interface ActivitySubmitable extends  RequestCallback, ResponseExtractor {
    public void submitActivity(View view);
    public void performSubmit();
}
