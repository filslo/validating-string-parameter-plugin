/**
 * Copyright (c) 2009 Cliffano Subagio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.validating_string_parameter;

import hudson.util.FormValidation;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;

/**
 * This class provides validation functions.
 * 
 * @author cliffano
 */
public class ValidatingStringParameterValidator {

  
	  /**
     * Check the regular expression entered by the user
     * 
     *   * @param value
     *            the regex
     * @return false when regex is malformed, true otherwise
     */
    public FormValidation doCheckRegex(final String value) {
    	  FormValidation validation = FormValidation.ok();
          if (StringUtils.isNotBlank(value)) {
             try {
                  Pattern.compile(value);
             } catch (PatternSyntaxException pse) {
                  validation = FormValidation.error("Invalid regular expression: " + pse.getDescription());
                  //FormValidation.error(e, e.getLocalizedMessage());
             }
          }
          
          return validation;
    }

    /**
     * Called to validate the passed user entered value against the configured regular expression.
     */
    public FormValidation doValidate(String regex,
            final String failedValidationMessage,
            final String value) {
    	
    	FormValidation validation = FormValidation.ok();

		if (StringUtils.isNotBlank(value)) {
			if (value.contains("$")) {
				// since this is used to disable projects, be conservative
//				LOGGER.fine("Location could be expanded on build '" + build
//						+ "' parameters values:");
			} else {
				if (!Pattern.matches(regex, value)) {
					try {
					    return failedValidationMessage == null || "".equals(failedValidationMessage)
		                        ? FormValidation.error("Value entered does not match regular expression: " + regex)
		                        : FormValidation.error(failedValidationMessage);

				    } catch (PatternSyntaxException pse) {
				    	validation = FormValidation.error("Invalid regular expression [" + regex + "]: " + pse.getDescription());
			        }
				} 
				//	validation = FormValidation.error(Messages
					//		.SiteMonitor_Error_PrefixOfURL());
				
			}
		}
		
		return validation;
    }
}
