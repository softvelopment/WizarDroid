/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity.helper;

/**
 *
 * @author softvelopment
 */
public class ValidationResult {
    private boolean valid= true;
    public int errorMessageId;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean validationResult) {
        this.valid = validationResult;
    }

    public int getErrorMessageId() {
        return errorMessageId;
    }

    public void setErrorMessageId(int errorMessage) {
        this.errorMessageId = errorMessage;
    }
  
}
