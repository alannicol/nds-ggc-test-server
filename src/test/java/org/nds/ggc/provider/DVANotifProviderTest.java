package org.nds.ggc.provider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.Appointment;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.MessageHeader;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AppointmentStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.BundleTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointUseEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class DVANotifProviderTest {

    private DVANotifProvider dvaNotifProvider;

    @Before
    public void setup() throws Exception {
        dvaNotifProvider = new DVANotifProvider();
    }

    @Test
    public void testNewDVANotifProvider() throws Exception {
        Bundle bundle;

        bundle = dvaNotifProvider.newDVANotifProvider((createBundle()));

        assertNotNull(bundle);
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
                .setFullUrl("urn:uuid:3953ad6d-07e5-4e79-9446-1eeadd150836")
                .setResource(createAppointment());
        bundle
                .addEntry()
                .setFullUrl("urn:uuid:5e4901f7-0439-4c02-8c1a-2a10f6182836")
                .setResource(createPatient());

        return bundle;
    }

    private MessageHeader createMessageHeader() {
        MessageHeader messageHeader;

        messageHeader = new MessageHeader();
        messageHeader.setId(new IdDt("50cce60c-00b6-499e-84f0-3ee085d6f75d"));
        messageHeader.setTimestamp(new InstantDt(Calendar.getInstance()));
        messageHeader.setEvent(createEvent());
        messageHeader.setSource(createSource());
        messageHeader.setDestination(createDestinationList());
        messageHeader.setData(createDataList());

        return messageHeader;
    }

    private CodingDt createEvent() {
        CodingDt codingDt;

        codingDt = new CodingDt();
        codingDt.setSystem("https://digitalhealthplatform.scot/fhir/messagetypes");
        codingDt.setCode("DvaNotifR");

        return codingDt;
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
        destination.setName("Lenus");
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
        patient.setName(createNameList());
        patient.setContact(createContactList());

        return patient;
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
}