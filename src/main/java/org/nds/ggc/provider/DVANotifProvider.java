package org.nds.ggc.provider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.*;
import ca.uhn.fhir.model.dstu2.valueset.*;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.*;

public class DVANotifProvider implements IResourceProvider {

    private static Logger logger = LoggerFactory.getLogger(DVANotifProvider.class);

    private FhirContext context;
    private IGenericClient client;
    private ScheduledExecutorService executor;

    public DVANotifProvider() {
        context = FhirContext.forDstu2();

        executor = Executors.newSingleThreadScheduledExecutor();
        client = context.newRestfulGenericClient("https://dvp-tomcat.azurewebsites.net/nds-dvp-server/fhir");
    }

    @Transaction()
    public Bundle newDVANotifProvider(@TransactionParam Bundle bundle) {

        Bundle returnBundle;

        logger.info("POST DVA Notif");
        logger.info("Server Received {}",prettyPrint(bundle));

        executor.schedule(() -> {
            respond();
        }, 2000, TimeUnit.MILLISECONDS);

        logger.info("Server Responding");

        returnBundle = new Bundle();
        returnBundle.setId(bundle.getId());
        returnBundle.setType(BundleTypeEnum.MESSAGE);

        return returnBundle;
    }

    private String prettyPrint(IBaseResource resource) {
        return context.newJsonParser().setPrettyPrint(true).encodeResourceToString(resource);
    }

    private void respond() {
        Bundle bundle;

        bundle = createBundle();
        client.transaction().withBundle(bundle).execute();

        logger.info("Server Sent {}", prettyPrint(bundle));
    }

    private Bundle createBundle() {
        Bundle bundle;

        bundle = new Bundle();
        bundle.setId(new IdDt("32e975e8-1002-4ece-9b50-48b62fac106a"));
        bundle.setType(BundleTypeEnum.MESSAGE);

        bundle
                .addEntry()
                .setFullUrl("urn:uuid:50cce60c-00b6-499e-84f0-3ee085d6f75d")
                .setResource(createMessageHeader());
        bundle
                .addEntry()
                .setFullUrl("urn:uuid:c66ea949-b215-4631-970d-9d04626abb26")
                .setResource(createDocumentReference());
        bundle
                .addEntry()
                .setFullUrl("urn:uuid:3953ad6d-07e5-4e79-9446-1eeadd150836")
                .setResource(createAppointment());
        bundle
                .addEntry()
                .setFullUrl("urn:uuid:5e4901f7-0439-4c02-8c1a-2a10f6182836")
                .setResource(createPatient());
        bundle
                .addEntry()
                .setFullUrl("urn:uuid:016356f8-2ef2-4daa-bd01-a7410d841bb2")
                .setResource(createOrganization());

        return bundle;
    }

    private MessageHeader createMessageHeader() {
        MessageHeader messageHeader;

        messageHeader = new MessageHeader();
        messageHeader.setId(new IdDt("50cce60c-00b6-499e-84f0-3ee085d6f75d"));
        messageHeader.setTimestamp(new InstantDt(Calendar.getInstance()));
        messageHeader.setEvent(createEvent());
        messageHeader.setResponse(createResponse());
        messageHeader.setSource(createSource());
        messageHeader.setDestination(createDestinationList());
        messageHeader.setData(createDataList());

        return messageHeader;
    }

    private DocumentReference createDocumentReference() {
        DocumentReference documentReference;

        documentReference = new DocumentReference();
        documentReference.setId(new IdDt("c66ea949-b215-4631-970d-9d04626abb26"));
        documentReference.setCreated(new DateTimeDt());
        documentReference.setIndexed(new InstantDt(Calendar.getInstance()));
        documentReference.setStatus(DocumentReferenceStatusEnum.CURRENT);
        documentReference.setDescription("Consultant Report");
        documentReference.setContent(createContent());

        return documentReference;
    }

    private Appointment createAppointment() {
        Appointment appointment;

        appointment = new Appointment();
        appointment.setId(new IdDt("3953ad6d-07e5-4e79-9446-1eeadd150836"));
        appointment.setStatus(AppointmentStatusEnum.BOOKED);
        appointment.setType(createAppointmentType());
        appointment.setDescription("A Dermatology virtual appointment has been scheduled for you.");
        appointment.setStart(new InstantDt(Calendar.getInstance()));
        appointment.setEnd(new InstantDt(Calendar.getInstance()));
        appointment.setParticipant(createParticipants());


        return appointment;
    }

    private Patient createPatient() {
        Patient patient;

        patient = new Patient();
        patient.setId(new IdDt("5e4901f7-0439-4c02-8c1a-2a10f6182836"));
        patient.setIdentifier(createIdentifiers());
        patient.setName(createNameList());
        patient.setContact(createContactList());
        patient.setCareProvider(createCareProvider());

        return patient;
    }

    private List<IdentifierDt> createIdentifiers() {
        List<IdentifierDt> identifiers;

        identifiers = new ArrayList<>();
        IdentifierDt identifierDt = new IdentifierDt();
        identifierDt.setSystem("https://phfapi.digitalhealthplatform.net/fhir/chinumber");
        identifierDt.setValue("0805860000");
        identifiers.add(identifierDt);

        return identifiers;
    }

    private List<ResourceReferenceDt> createCareProvider() {
        List<ResourceReferenceDt> resourceReferenceDts;

        resourceReferenceDts = new ArrayList<>();
        ResourceReferenceDt resourceReferenceDt = new ResourceReferenceDt();
        resourceReferenceDt.setReference("urn:uuid:016356f8-2ef2-4daa-bd01-a7410d841bb2");
        resourceReferenceDts.add(resourceReferenceDt);


        return resourceReferenceDts;
    }

    private Organization createOrganization() {
        Organization organization;

        organization = new Organization();
        organization.setId(new IdDt("016356f8-2ef2-4daa-bd01-a7410d841bb2"));
        organization.setIdentifier(createOrganizationIdentifiers());
        organization.setName("Alba House");

        return organization;
    }

    private List<IdentifierDt> createOrganizationIdentifiers() {
        List<IdentifierDt> identifiers;

        identifiers = new ArrayList<>();
        IdentifierDt identifierDt = new IdentifierDt();
        identifierDt.setSystem("http://fhir.scot.nhs.uk/DSTU2/Id/national/organisationId");
        identifierDt.setValue("99999");
        identifiers.add(identifierDt);

        return identifiers;
    }

    private CodingDt createEvent() {
        CodingDt codingDt;

        codingDt = new CodingDt();
        codingDt.setSystem("https://digitalhealthplatform.scot/fhir/messagetypes");
        codingDt.setCode("DvaNotif_Response");

        return codingDt;
    }

    private MessageHeader.Response createResponse() {
        MessageHeader.Response response;

        response = new MessageHeader.Response();
        response.setIdentifier("4f360965-1e11-4066-9e76-53aa38e1c00");
        response.setCode(ResponseTypeEnum.OK);

        return response;
    }

    private MessageHeader.Source createSource() {
        MessageHeader.Source source;

        source = new MessageHeader.Source();
        source.setName("DHP");

        return source;
    }

    private List<MessageHeader.Destination> createDestinationList() {
        List<MessageHeader.Destination> destinationList;

        destinationList = new ArrayList<>();
        MessageHeader.Destination destination = new MessageHeader.Destination();
        destination.setName("NSS HUB");
        destinationList.add(destination);

        return destinationList;
    }

    private List<ResourceReferenceDt> createDataList() {
        List<ResourceReferenceDt> dataList;

        dataList = new ArrayList<>();
        dataList.add(new ResourceReferenceDt("urn:uuid:3953ad6d-07e5-4e79-9446-1eeadd150836"));
        dataList.add(new ResourceReferenceDt("urn:uuid:5e4901f7-0439-4c02-8c1a-2a10f6182836"));

        return dataList;
    }

    private List<DocumentReference.Content> createContent() {
        List<DocumentReference.Content> documents;
        DocumentReference.Content content;

        documents = new ArrayList<>();
        content = new DocumentReference.Content();
        content.setAttachment(createAttachment());

        documents.add(content);

        return documents;
    }

    private AttachmentDt createAttachment() {
        AttachmentDt attachmentDt;

        attachmentDt = new AttachmentDt();
        attachmentDt.setContentType("application/pdf");
        attachmentDt.setData("".getBytes());
        attachmentDt.setTitle("Consultant Report");

        return attachmentDt;
    }

    private CodeableConceptDt createAppointmentType() {
        CodeableConceptDt codeableConceptDt;

        codeableConceptDt = new CodeableConceptDt();
        codeableConceptDt.setText("Dermatology Virtual");

        return codeableConceptDt;
    }

    private List<Appointment.Participant> createParticipants() {
        List<Appointment.Participant> participants;

        participants = new ArrayList<>();
        participants.add(createParticipant());

        return participants;
    }

    private Appointment.Participant createParticipant() {
        Appointment.Participant participant;

        participant = new Appointment.Participant();
        participant.setActor(new ResourceReferenceDt("urn:uuid:5e4901f7-0439-4c02-8c1a-2a10f6182836"));

        return participant;
    }

    private List<HumanNameDt> createNameList() {
        List<HumanNameDt> humanNameList;


        humanNameList = new ArrayList<>();
        HumanNameDt humanNameDt = new HumanNameDt();
        humanNameDt.setFamily(createFamilyList());
        humanNameDt.setGiven(createGivenList());
        humanNameList.add(humanNameDt);

        return humanNameList;
    }

    private List<StringDt> createFamilyList() {
        List<StringDt> familyNameList;

        familyNameList = new ArrayList<>();
        familyNameList.add(new StringDt("User"));

        return familyNameList;
    }

    private List<StringDt> createGivenList() {
        List<StringDt> givenNameList;

        givenNameList = new ArrayList<>();
        givenNameList.add(new StringDt("Jane"));

        return givenNameList;
    }

    private List<Patient.Contact> createContactList() {
        List<Patient.Contact> contactList;

        contactList = new ArrayList<>();
        Patient.Contact contact = new Patient.Contact();
        contact.setTelecom(createContactPointList());
        contactList.add(contact);

        return contactList;
    }

    private List<ContactPointDt> createContactPointList() {
        List<ContactPointDt> contactPointList;

        contactPointList = new ArrayList<>();
        ContactPointDt contactPointDt = new ContactPointDt();
        contactPointDt.setSystem(ContactPointSystemEnum.EMAIL);
        contactPointDt.setValue("HSCPortalTest1@gmail.com");
        contactPointDt.setUse(ContactPointUseEnum.HOME);
        contactPointList.add(contactPointDt);

        return contactPointList;
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Bundle.class;
    }
}