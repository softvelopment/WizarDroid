/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.softvelopment.wizardroid.activity.helper.ActivityDirection;
import com.softvelopment.wizardroid.activity.helper.ActivityValidateResult;
import com.softvelopment.wizardroid.activity.helper.ActivityXmlBean;
import com.softvelopment.wizardroid.activity.helper.FieldFormatValidator;
import com.softvelopment.wizardroid.activity.helper.ValidationResult;
import java.io.IOException;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 *
 * @author softvelopment
 */
public abstract class FormControllableActivity extends BaseControllableActivity implements ValidatableActivity, ActivitySubmitable {


    public final ActivityValidateResult validateActivity(View view, String when) {
        ActivityValidateResult result = new ActivityValidateResult();
        result.setIsValid(true);

        ActivityXmlBean xmlBean = ActivityManagerController.getInstance().getActivity(getActivityName());

        if (xmlBean != null) {
            ActivityDirection direction = xmlBean.getActivity(ActivityConstants.MOVE_ACTIVITY_FORWARD, when);

            if (direction != null) {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View control = viewGroup.getChildAt(i);
                    //note controls have to have a tag to be able to update errors
                    if ((control.getTag() != null) && (control instanceof EditText)) {
                        EditText editTextControl = (EditText) control;
                        for (String requiredControlTag : direction.getRequiredFieldIds()) {
                            if (editTextControl.getTag().toString().equalsIgnoreCase(requiredControlTag)) {
                                String controlText = editTextControl.getText().toString();
                                String controlHint = editTextControl.getHint().toString();
                                if ((controlText.equalsIgnoreCase(controlHint)) || ( controlText.isEmpty())){
                                    //editTextControl.setError(getResources().getString(com.softvelopment.accessinternetready.R.string.error_text));
                                    result.setIsValid(false);
                                }
                            }
                             }//for(String....
                            
                            //check th format
                              String formatType =     direction.getFieldFormats().get(control.getTag().toString());
                              if(formatType != null)
                              {
                                  ValidationResult validationResult = FieldFormatValidator.isDataValid(formatType, editTextControl.getText().toString());
                                    if(!validationResult.isValid())
                                    {
                                        editTextControl.setError(getResources().getString(validationResult.getErrorMessageId()));
                                        result.setIsValid(false);

                                    }
                              }//close if(formatType != null..
                    }//close out if((control.getTag... EditText))
                }//close out for(int i=0...
            }//close out if(direction !=null...
        }
        return result;
    }

    public void submitActivity(View view) {
        ActivityValidateResult result = this.validateActivity(view, getWhen());
        if (result.isValid()) {
            performSubmit();
        }

    }

    public abstract void performSubmit();
    
    @Override
    public void doWithRequest(ClientHttpRequest chr) throws IOException
    {
	    
    }
    
    @Override
    public Object  extractData(ClientHttpResponse chr) throws IOException
    {
	    return null;
    }

}
