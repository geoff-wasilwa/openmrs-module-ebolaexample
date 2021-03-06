package org.openmrs.module.ebolaexample.metadata;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.VisitType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.appframework.AppFrameworkConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationTag;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.personAttributeType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.visitType;

@Component
public class EbolaTestBaseMetadata extends AbstractMetadataBundle {

    @Autowired
    private ConceptService conceptService;

    @Autowired @Qualifier("adminService")
    private AdministrationService administrationService;

    @Override
    public void install() throws Exception {
        VisitType visitType = visitType("Facility Visit", "", uuid());
        install(visitType);
        administrationService.saveGlobalProperty(new GlobalProperty(EmrApiConstants.GP_AT_FACILITY_VISIT_TYPE, visitType.getUuid()));

        EncounterType transfer = encounterType("Transfer", "Transfer between wards", uuid());
        install(transfer);
        administrationService.saveGlobalProperty(new GlobalProperty(EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_ENCOUNTER_TYPE, transfer.getUuid()));

        install(personAttributeType("Test Patient", "", Boolean.class, null, true, 0, EmrApiConstants.TEST_PATIENT_ATTRIBUTE_UUID));

        install(locationTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION, "", uuid()));
        install(locationTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER, "", uuid()));
        install(locationTag("Supports Login", "", AppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN_UUID));

        // We cannot do install(concept(...)) because of: java.lang.UnsupportedOperationException: Concepts can only be fetched for now
        conceptService.saveConcept(concept("Ebola Program", "162637AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "N/A", "Program"));
    }

    private String uuid() {
        return UUID.randomUUID().toString();
    }

    private Concept concept(String name, String uuid, String datatype, String conceptClass) {
        Concept concept = new Concept();
        concept.addName(new ConceptName(name, Locale.ENGLISH));
        concept.setUuid(uuid);
        concept.setDatatype(conceptService.getConceptDatatypeByName(datatype));
        concept.setConceptClass(conceptService.getConceptClassByName(conceptClass));
        return concept;
    }

}
