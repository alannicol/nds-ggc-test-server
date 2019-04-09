package org.nds.ggc.provider;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.rest.api.MethodOutcome;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DVANotifProviderTest {

    private DVANotifProvider DVANotifProvider;

    @Before
    public void setup() {
        DVANotifProvider = new DVANotifProvider();
    }

    @Test
    public void testNewDVANotifProvider() {
        MethodOutcome methodOutcome;

        methodOutcome = DVANotifProvider.newDVANotifProvider((createBundle()));

    }

    private Bundle createBundle() {

        return null;
    }
}