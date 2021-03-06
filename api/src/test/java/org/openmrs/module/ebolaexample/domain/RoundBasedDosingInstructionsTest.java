package org.openmrs.module.ebolaexample.domain;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptNameTag;
import org.openmrs.DrugOrder;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.TestDataFactory;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static junit.framework.Assert.assertEquals;

public class RoundBasedDosingInstructionsTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ConceptService conceptService;

    @Before
    public void setUp() throws Exception {
        ConceptNameTag conceptNameTag = new ConceptNameTag("Ebola Preferred", "preferred for ebolaexample");
        conceptNameTag.setUuid(EbolaMetadata._ConceptNameTag.PREFERRED);
        conceptService.saveConceptNameTag(conceptNameTag);
    }

    @Test
    public void getDosingInstructionsAsString_shouldAppendRoundInformationFromDosingInstructions() throws Exception {
        DrugOrder order = TestDataFactory.createValidDrugOrder();
        order.setDuration(null);
        RoundBasedDosingInstructions dosingInstructions =
                (RoundBasedDosingInstructions) new RoundBasedDosingInstructions().getDosingInstructions(order);
        String dosingInstructionsAsString = dosingInstructions.getDosingInstructionsAsString(Context.getLocale());

        assertEquals("10 ml IV each Morning, Evening", dosingInstructionsAsString);
    }

    @Test
    public void getDosingInstructionsAsString_shouldIncludePRNInformation() throws Exception {
        DrugOrder order = TestDataFactory.createValidDrugOrder();
        order.setAsNeeded(true);
        order.setDuration(null);
        order.setAsNeededCondition("Pain");
        RoundBasedDosingInstructions dosingInstructions =
                (RoundBasedDosingInstructions) new RoundBasedDosingInstructions().getDosingInstructions(order);
        String dosingInstructionsAsString = dosingInstructions.getDosingInstructionsAsString(Context.getLocale());

        assertEquals("10 ml IV each Morning, Evening <span class=\"lozenge prn\">PRN Pain</span>", dosingInstructionsAsString);
    }

    @Test
    public void getDosingInstructionsAsString_shouldIncludeDurationInformation() throws Exception {
        DrugOrder order = TestDataFactory.createValidDrugOrder();
        order.setDuration(7);
        order.setDurationUnits(TestDataFactory.createConceptWithName("Days"));
        RoundBasedDosingInstructions dosingInstructions =
                (RoundBasedDosingInstructions) new RoundBasedDosingInstructions().getDosingInstructions(order);
        String dosingInstructionsAsString = dosingInstructions.getDosingInstructionsAsString(Context.getLocale());
        assertEquals("10 ml IV each Morning, Evening for 7 Days", dosingInstructionsAsString);
    }

    @Test
    public void validate_shouldValidateDoseIsNotEmpty() throws Exception {
        DrugOrder order = TestDataFactory.createValidDrugOrder();
        order.setDose(null);
        Errors errors = new BindException(order, "drugOrder");
        new RoundBasedDosingInstructions().validate(order, errors);
        assertEquals(true, errors.hasErrors());
    }

    @Test
    public void validate_shouldValidateRouteIsNotEmpty() throws Exception {
        DrugOrder order = TestDataFactory.createValidDrugOrder();
        order.setRoute(null);
        Errors errors = new BindException(order, "drugOrder");
        new RoundBasedDosingInstructions().validate(order, errors);
        assertEquals(true, errors.hasErrors());
    }

    @Test
    public void validate_shouldValidateDoseUnitsIsNotEmpty() throws Exception {
        DrugOrder order = TestDataFactory.createValidDrugOrder();
        order.setDose(null);
        Errors errors = new BindException(order, "drugOrder");
        new RoundBasedDosingInstructions().validate(order, errors);
        assertEquals(true, errors.hasErrors());
    }

    @Test
    public void validate_shouldValidateDosingInstructions() throws Exception {
        assertValidityOfDosingInstructions("", false);
        assertValidityOfDosingInstructions("Invalid", false);
        assertValidityOfDosingInstructions("Invalid, Morning, Evening", false);
        assertValidityOfDosingInstructions("Morning", true);
        assertValidityOfDosingInstructions("Morning, Evening, Night, Afternoon", true);
    }

    private void assertValidityOfDosingInstructions(String dosingInstructions, boolean expectedToBeValid) {
        DrugOrder order = TestDataFactory.createValidDrugOrder();
        order.setDosingInstructions(dosingInstructions);
        Errors errors = new BindException(order, "drugOrder");
        new RoundBasedDosingInstructions().validate(order, errors);
        assertEquals(!expectedToBeValid, errors.hasErrors());
    }
}
