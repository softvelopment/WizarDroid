/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softvelopment.wizardroid.activity.helper;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.validator.EmailValidator;
import org.apache.commons.validator.routines.AbstractNumberValidator;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.DoubleValidator;
import org.apache.commons.validator.routines.IntegerValidator;

/**
 *
 * @author softvelopment
 */
public class FieldFormatValidator {

    public static final String EMAIL_FORMAT = "emailFormat";
    public static final String NUMBER_FORMAT = "numberFormat";
    public static final String DATE_FORMAT = "dateFormat";
    public static final String MAX_LENGTH = "maxlength";
    public static final String MIN_LENGTH = "minlength";

    public static ValidationResult isDataValid(String... args) {
        ValidationResult result = new ValidationResult();

        if (args.length >= 2) {
            String format = args[0];
            String data = args[1];

            if (format.toLowerCase().equalsIgnoreCase(EMAIL_FORMAT.toLowerCase())) {
                result.setValid(EmailValidator.getInstance().isValid(data));
            } else if (format.toLowerCase().equalsIgnoreCase(NUMBER_FORMAT.toLowerCase())) {
                result.setValid(((IntegerValidator.getInstance().isValid(data)) || (DoubleValidator.getInstance().isValid(data))));
            } else if (format.toLowerCase().equalsIgnoreCase(DATE_FORMAT.toLowerCase())) {
                result.setValid(DateValidator.getInstance().isValid(data));
            } else if ((format.toLowerCase().startsWith(MAX_LENGTH.toLowerCase()))
                    || (format.startsWith(MIN_LENGTH.toLowerCase()))) {
                int formatSepIndex = format.indexOf(":");
                if (formatSepIndex > 0) {
                    String formatChecker = format.substring(0, formatSepIndex);
                    String stringLen = format.substring(formatSepIndex + 1, format.length());
                    int length;
                    try {
                        length = Integer.parseInt(stringLen);

                        if (formatChecker.toLowerCase().equalsIgnoreCase(MAX_LENGTH)) {
                            result.setValid(!(data.length() > length));

                        } else if (formatChecker.toLowerCase().equalsIgnoreCase(MIN_LENGTH)) {
                            result.setValid(!(data.length() < length));

                        }
                    } catch (NumberFormatException nfe) {
                        Logger.getLogger(FieldFormatValidator.class.getName()).log(Level.WARNING, null, nfe);
                    }
                }//close out if(formatSepIndex...
            }
        }

        //finally compare the format to the data  because they value should be the same
        return result;
    }

   
}
