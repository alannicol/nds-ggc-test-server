package org.nds.ggc.provider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.parser.IParser;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class DVANotifProviderTest {

    private static final String DVA_NOTIF_R_RESOURCE="src/test/resources/DvaNotifR.json";

    private FhirContext fhirContext;
    private DVANotifProvider dvaNotifProvider;

    @Before
    public void setup() {
        fhirContext = FhirContext.forDstu2();
        dvaNotifProvider = new DVANotifProvider();
    }

    @Test
    public void testNewDVANotifProvider() throws Exception {
        Bundle bundle;

        bundle = dvaNotifProvider.processMessage("true",createBundle());

        assertNotNull(bundle);
    }

    private Bundle createBundle() throws Exception {
        IParser parser;
        Reader reader;
        Bundle bundle;

        parser = fhirContext.newJsonParser();
        reader = new FileReader(new File(DVA_NOTIF_R_RESOURCE));
        bundle = parser.parseResource(Bundle.class, reader);

        return bundle;
    }
}