package org.nds.ggc.provider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.*;
import ca.uhn.fhir.model.dstu2.valueset.*;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.nds.ggc.util.ServerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.util.concurrent.*;

public class DVANotifProvider {

    private static Logger logger = LoggerFactory.getLogger(DVANotifProvider.class);

    private static final String DVA_NOTIF_R_RESPONSE_RESOURCE="DvaNotifR_Response.json";

    private FhirContext context;
    private IGenericClient client;
    private ScheduledExecutorService executor;

    public DVANotifProvider() {
        context = FhirContext.forDstu2();

        executor = Executors.newSingleThreadScheduledExecutor();
        client = context.newRestfulGenericClient(ServerProperty.getClientUrl());
    }

    @Operation(name="$process-message")
    public Bundle processMessage(@OperationParam(name="async") String async, @ResourceParam Bundle bundle) {

        logger.info("POST DVA Notif R");
        logger.info("Server Received {}",prettyPrint(bundle));

      //  scheduleResponse(obtainMessageHeaderId(bundle));

        logger.info("Server Response Scheduled");

        return createAckBundle(bundle.getId());
    }

    private String obtainMessageHeaderId(Bundle bundle) {
        return bundle.getEntry().get(0).getResource().getId().getValue();
    }

    private String prettyPrint(IBaseResource resource) {
        return context.newJsonParser().setPrettyPrint(true).encodeResourceToString(resource);
    }

    private void scheduleResponse(String messageHeaderId) {
        executor.schedule(() -> {
            respond(messageHeaderId);
        }, ServerProperty.getServerDelay(), TimeUnit.MILLISECONDS);
    }

    private void respond(String messageHeaderId) {
        Bundle bundle;

        logger.info("Server Responding");

        try {
            bundle = createBundle(messageHeaderId);
            client.transaction().withBundle(bundle).execute();

            logger.info("Server Responded {}", prettyPrint(bundle));
        } catch(Exception exception) {
            logger.error("Server unable to respond to Client", exception);
        }
    }

    private Bundle createBundle(String messageHeaderId) {
        IParser parser;
        Bundle bundle=null;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(ServerProperty.class.getClassLoader().getResourceAsStream(DVA_NOTIF_R_RESPONSE_RESOURCE))) {
            parser = context.newJsonParser();
            bundle = parser.parseResource(Bundle.class, bufferedInputStream);
            ((MessageHeader)bundle.getEntry().get(0).getResource()).setResponse(createResponse(messageHeaderId));
        } catch(Exception exception) {
            logger.error("Cannot obtain response file", exception);
        }

        return bundle;
    }

    private MessageHeader.Response createResponse(String messageHeaderId) {
        MessageHeader.Response response;

        response = new MessageHeader.Response();
        response.setIdentifier(messageHeaderId);
        response.setCode(ResponseTypeEnum.OK);

        return response;
    }

    private Bundle createAckBundle(IdDt theId) {
        Bundle ackBundle;

        ackBundle = new Bundle();
        ackBundle.setId(theId);
        ackBundle.setType(BundleTypeEnum.MESSAGE);

        return ackBundle;
    }
}