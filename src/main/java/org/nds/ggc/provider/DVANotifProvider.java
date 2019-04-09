package org.nds.ggc.provider;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DVANotifProvider implements IResourceProvider {

    private static Logger logger = LoggerFactory.getLogger(DVANotifProvider.class);

    public DVANotifProvider() {
    }

    @Create()
    public MethodOutcome newDVANotifProvider(@ResourceParam Bundle bundle) {

        return new MethodOutcome();
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return null;
    }
}