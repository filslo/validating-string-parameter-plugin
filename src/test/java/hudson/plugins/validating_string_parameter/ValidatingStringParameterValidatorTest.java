package hudson.plugins.validating_string_parameter;

import hudson.plugins.validating_string_parameter.ValidatingStringParameterValidator;
import hudson.util.FormValidation;
import junit.framework.TestCase;

public class ValidatingStringParameterValidatorTest extends TestCase {

    private ValidatingStringParameterValidator validator;

    @Override
	public void setUp() {
        this.validator = new ValidatingStringParameterValidator();
    }

    public void testValidateRegexShouldGiveOk() {
        FormValidation validation = this.validator.doCheckRegex(".*");
        assertEquals(FormValidation.Kind.OK, validation.kind);
    }
    
    public void testValidateValueAgainstRegexShouldGiveOk() {
        FormValidation validation = this.validator.doValidate("G..R..C..", "Incorrect value", "G00R00C00");
        assertEquals(FormValidation.Kind.OK, validation.kind);
    }
    
   

    public void testValidateValueAgainstRegexShouldGiveError() {
        FormValidation validation =  this.validator.doValidate("G..R..C..", "Incorrect value", "BLABLABLa");
        assertEquals(FormValidation.Kind.ERROR, validation.kind);
    }

}
