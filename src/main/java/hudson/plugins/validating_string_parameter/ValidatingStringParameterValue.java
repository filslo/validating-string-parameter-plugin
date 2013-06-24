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

import hudson.AbortException;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.ParameterValue;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.StringParameterValue;
import hudson.tasks.BuildWrapper;
import hudson.util.VariableResolver;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * {@link ParameterValue} created from {@link ValidatingStringParameterDefinition}.
 *
 * @author Peter Hayes
 * @since 1.0
 */
public class ValidatingStringParameterValue extends StringParameterValue {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String regex;
	private String actualValue;

    @DataBoundConstructor
    public ValidatingStringParameterValue(String name, String value) {
        this(name, value, null, null);
        
        
    }

    public ValidatingStringParameterValue(String name, String value, String regex, String description) {
        super(name, value, description);
        this.regex = regex;
       
        this.actualValue = value;
    }

    public String getRegex() {
        return this.regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getValue() {
        return this.value;
    }
    
    /**
     * Exposes the name/value as an environment variable.
     */
    @Override
    public void buildEnvVars(AbstractBuild<?,?> build, EnvVars env) {
        env.put(this.name,this.actualValue);
        env.put(this.name.toUpperCase(Locale.ENGLISH),this.actualValue); // backward compatibility pre 1.345
    }

    @Override
    public VariableResolver<String> createVariableResolver(AbstractBuild<?, ?> build) {
        return new VariableResolver<String>() {
            public String resolve(String name) {
                return ValidatingStringParameterValue.this.name.equals(name) ? ValidatingStringParameterValue.this.actualValue : null;
            }
        };
    }

    @Override
    public BuildWrapper createBuildWrapper(AbstractBuild<?, ?> build) {
    	EnvVars env;
		try {
			env = build.getEnvironment(TaskListener.NULL);
			EnvVarsUtils.overrideAll(env, build.getBuildVariables());
			this.actualValue = env.expand(ValidatingStringParameterValue.this.value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//.getCharacteristicEnvVars();
		
		
		
    	 if (!Pattern.matches(this.regex,this.actualValue)) {
            // abort the build within BuildWrapper
            return new BuildWrapper() {
                @Override
                public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                    throw new AbortException("Invalid value for parameter [" + getName() + "] specified: " + ValidatingStringParameterValue.this.actualValue);
                }
            };
        }
       return null;
        
    }

//    @Override
//	public VariableResolver<String> createVariableResolver(
//			final AbstractBuild<?, ?> build) {
//    	return new VariableResolver<String>() {
//            public String resolve(String name) {
//            	String actualValue =  null;
//            	if( name.equals(name)) {
//            		EnvVars env = build.getCharacteristicEnvVars();
//        			EnvVarsUtils.overrideAll(env, build.getBuildVariables());
//        			
//        			 actualValue = env.expand(ValidatingStringParameterValue.this.value);
//            	}
//                return  actualValue;
//            }
//        };
//    	// deal with environment and build variables
//    		
//		
//	}

	@Override
    public int hashCode() {
        final int prime = 71;
        int result = super.hashCode();
        result = prime * result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (ValidatingStringParameterValue.class != obj.getClass()) {
            return false;
        }
        ValidatingStringParameterValue other = (ValidatingStringParameterValue) obj;
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "(ValidatingStringParameterValue) " + getName() + "='" + this.value + "'";
    }
}
