package gov.nist.healthcare.validation.example;

import hl7.v2.profile.Profile;
import hl7.v2.profile.XMLDeserializer;
import hl7.v2.validation.SyncHL7Validator;
import hl7.v2.validation.content.ConformanceContext;
import gov.nist.validation.report.Report;
import hl7.v2.validation.content.DefaultConformanceContext;
import hl7.v2.validation.vs.ValueSetLibrary;
import hl7.v2.validation.vs.ValueSetLibraryImpl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationExample {

    public static void main(String[] args) throws Exception {

        InputStream profileXML = ValidationExample.class.getResourceAsStream("/export/Profile.xml");
        InputStream constraintsXML = ValidationExample.class.getResourceAsStream("/export/Constraints.xml");
        InputStream vsLibraryXML = ValidationExample.class.getResourceAsStream("/export/ValueSets.xml");

        String message = new BufferedReader(new InputStreamReader(ValidationExample.class.getResourceAsStream("/message.er7"))).lines().collect(Collectors.joining("\n"));

        // ID of the message profile from the Profile.xml file
        String ID = "5d1a2e8484ae07947e957897";
        Report report = validate(
                profileXML,
                Arrays.asList(constraintsXML),
                vsLibraryXML,
                message,
                ID
        );

        System.out.println(report.toJson());

    }

    public static Report validate(InputStream profileXML, List<InputStream> constraintsXML, InputStream vsLibraryXML, String message, String id) throws Exception {
        Profile profile = getProfile(profileXML);
        ConformanceContext constraints = getConformanceContext(constraintsXML);
        ValueSetLibrary vsLibrary = getValueSetLibrary(vsLibraryXML);

        return new SyncHL7Validator(profile, vsLibrary, constraints).check(message, id);
    }

    static Profile getProfile(InputStream profile) {
        return XMLDeserializer.deserialize(profile).get();
    }


    static ConformanceContext getConformanceContext(List<InputStream> constraints) {
        return DefaultConformanceContext.apply(constraints).get();
    }


    static ValueSetLibrary getValueSetLibrary(InputStream vsLibrary) {
        return ValueSetLibraryImpl.apply(vsLibrary).get();
    }
}
