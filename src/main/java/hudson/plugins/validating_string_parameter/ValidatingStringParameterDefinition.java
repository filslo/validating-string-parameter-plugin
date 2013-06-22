/*
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Luca Domenico Milanesio, Tom Huybrechts
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

import hudson.Extension;
import hudson.Util;
import hudson.model.Hudson;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.ParameterDefinition.ParameterDescriptor;
import hudson.util.FormValidation;


import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * String based parameter that supports setting a regular expression to validate the
 * user's entered value, giving real-time feedback on the value.
 * 
 * @author Peter Hayes
 * @since 1.0
 * @see {@link ParameterDefinition}
 */
public class ValidatingStringParameterDefinition extends ParameterDefinition {

    private static final long serialVersionUID = 2L;
    private String defaultValue;
    private String regex;
    private String failedValidationMessage;

    @DataBoundConstructor
    public ValidatingStringParameterDefinition(String name, String defaultValue, String regex, String failedValidationMessage, String description) {
        super(name, description);
        this.defaultValue = defaultValue;
        this.regex = regex;
        this.failedValidationMessage = failedValidationMessage;
    }

    public ValidatingStringParameterDefinition(String name, String defaultValue, String regex, String failedValidationMessage) {
        this(name, defaultValue, regex, failedValidationMessage, null);
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public String getRegex() {
        return this.regex;
    }

    public String getJsEncodedRegex() {
        return this.regex.replace("\\", "\\\\");
    }

    public String getFailedValidationMessage() {
        return this.failedValidationMessage;
    }

    public String getRootUrl() {
        return Hudson.getInstance().getRootUrl();
    }

    @Override
    public ValidatingStringParameterValue getDefaultParameterValue() {
        ValidatingStringParameterValue v = new ValidatingStringParameterValue(getName(), this.defaultValue, getRegex(), getDescription());
        return v;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        ValidatingStringParameterValue value = req.bindJSON(ValidatingStringParameterValue.class, jo);
        value.setDescription(getDescription());
        value.setRegex(this.regex);
        return value;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req) {
        String[] valueAsArray = req.getParameterValues(getName());
        if (valueAsArray == null || valueAsArray.length < 1) {
            return getDefaultParameterValue();
        } 
        String value = Util.removeTrailingSlash(Util.fixNull(valueAsArray[0]).trim());
        return new ValidatingStringParameterValue(getName(), value, this.regex, getDescription());
    }
    
    @Extension
    public static class ValidatingStringParameterDescriptor extends ParameterDescriptor {

    	  /**
         * The form validator.
         */
        private ValidatingStringParameterValidator mValidator;
    	
        /**
         * Constructs {@link SiteMonitorDescriptor}.
         */
        public ValidatingStringParameterDescriptor() {
            super();
            this.load();
            this.mValidator = new ValidatingStringParameterValidator();
        }

        
        @Override
        public String getDisplayName() {
            return "Validating String Parameter";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/validating-string-parameter/help.html";
        }

        /**
         * Check the regular expression entered by the user
         */
        public FormValidation doCheckRegex(@QueryParameter final String value) {
        	return this.mValidator.doCheckRegex(value);
        }

        /**
         * Called to validate the passed user entered value against the configured regular expression.
         */
        public FormValidation doValidate(@QueryParameter("regex") String regex,
                @QueryParameter("failedValidationMessage") final String failedValidationMessage,
                @QueryParameter("value") final String value) {
        	return this.mValidator.doValidate(regex, failedValidationMessage, value);
        }
    }   

}
