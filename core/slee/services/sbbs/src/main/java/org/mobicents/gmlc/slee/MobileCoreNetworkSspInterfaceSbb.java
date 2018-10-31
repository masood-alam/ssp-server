/**
 * TeleStax, Open Source Cloud Communications  Copyright 2012. 
 * and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.gmlc.slee;

import java.io.*;

import java.nio.charset.Charset;


import java.util.ArrayList;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.EventContext;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.facilities.Tracer;
import javax.slee.facilities.TimerFacility;

import javax.slee.resource.ResourceAdaptorTypeID;

import net.java.slee.resource.http.events.HttpServletRequestEvent;


import net.java.slee.resource.http.HttpServletRaActivityContextInterfaceFactory;
import net.java.slee.resource.http.HttpServletRaSbbInterface;
import net.java.slee.resource.http.HttpSessionActivity;
import net.java.slee.resource.http.events.HttpServletRequestEvent;

import org.mobicents.gmlc.SspPropertiesManagement;

import org.restcomm.camelgateway.EventsSerializeFactory;
import org.restcomm.camelgateway.XmlCAPDialog;
import org.mobicents.protocols.asn.AsnOutputStream;

import org.mobicents.protocols.ss7.cap.api.CAPDialog;
import org.mobicents.protocols.ss7.cap.api.CAPException;
import org.mobicents.protocols.ss7.cap.api.CAPMessage;
import org.mobicents.protocols.ss7.cap.api.CAPParameterFactory;
import org.mobicents.protocols.ss7.cap.api.CAPProvider;
import org.mobicents.protocols.ss7.cap.api.dialog.CAPUserAbortReason;
import org.mobicents.protocols.ss7.cap.api.errors.CAPErrorMessage;
import org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.ConnectRequest;
import org.mobicents.protocols.ss7.cap.primitives.CAPAsnPrimitive;
import org.mobicents.protocols.ss7.cap.service.circuitSwitchedCall.ApplyChargingReportRequestImpl;
import org.mobicents.protocols.ss7.cap.service.circuitSwitchedCall.ApplyChargingRequestImpl;
import org.mobicents.protocols.ss7.cap.service.circuitSwitchedCall.CancelRequestImpl;
import org.mobicents.protocols.ss7.cap.service.circuitSwitchedCall.ConnectToResourceRequestImpl;
import org.mobicents.protocols.ss7.cap.service.circuitSwitchedCall.ContinueRequestImpl;
import org.mobicents.protocols.ss7.cap.service.circuitSwitchedCall.EventReportBCSMRequestImpl;
import org.mobicents.protocols.ss7.cap.service.circuitSwitchedCall.FurnishChargingInformationRequestImpl;
import org.mobicents.protocols.ss7.cap.service.circuitSwitchedCall.InitialDPRequestImpl;
import org.mobicents.protocols.ss7.cap.service.circuitSwitchedCall.PromptAndCollectUserInformationRequestImpl;
import org.mobicents.protocols.ss7.cap.service.circuitSwitchedCall.ReleaseCallRequestImpl;
import org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.RequestReportBCSMEventRequest;
import org.mobicents.protocols.ss7.cap.service.circuitSwitchedCall.RequestReportBCSMEventRequestImpl;
import org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.DestinationRoutingAddress;
import org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.primitive.EventSpecificInformationBCSM;
import org.mobicents.protocols.ss7.inap.api.primitives.MiscCallInfo;


import org.mobicents.protocols.ss7.cap.api.isup.CalledPartyNumberCap;
import org.mobicents.protocols.ss7.cap.api.isup.CallingPartyNumberCap;
import org.mobicents.protocols.ss7.cap.api.isup.LocationNumberCap;
import org.mobicents.protocols.ss7.cap.api.primitives.BCSMEvent;
import org.mobicents.protocols.ss7.cap.api.primitives.EventTypeBCSM;
import org.mobicents.protocols.ss7.cap.api.primitives.ReceivingSideID;


import org.mobicents.protocols.ss7.cap.api.CAPApplicationContext;


import org.mobicents.protocols.ss7.indicator.RoutingIndicator;
import org.mobicents.protocols.ss7.isup.message.parameter.CalledPartyNumber;
import org.mobicents.protocols.ss7.isup.message.parameter.CallingPartyNumber;
import org.mobicents.protocols.ss7.isup.message.parameter.LocationNumber;
import org.mobicents.protocols.ss7.isup.message.parameter.NAINumber;

import org.mobicents.protocols.ss7.map.api.primitives.AddressNature;
import org.mobicents.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;



import org.mobicents.protocols.ss7.indicator.NatureOfAddress;
//import org.mobicents.protocols.ss7.indicator.NumberingPlan;
//import org.mobicents.protocols.ss7.indicator.RoutingIndicator;

import org.mobicents.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.mobicents.protocols.ss7.sccp.parameter.GlobalTitle;
import org.mobicents.protocols.ss7.sccp.parameter.ParameterFactory;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;
import org.mobicents.protocols.ss7.sccp.parameter.EncodingScheme;

import org.mobicents.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.mobicents.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.mobicents.protocols.ss7.sccp.impl.parameter.GlobalTitle0100Impl;

import org.mobicents.protocols.ss7.map.api.primitives.AddressString;
import org.mobicents.slee.SbbContextExt;


import javolution.util.FastList;
import javolution.xml.stream.XMLStreamException;

import org.mobicents.slee.resource.cap.CAPContextInterfaceFactory;

/**
 * 
 * @author amit bhayani
 * @author sergey vetyutnev
 */
public abstract class MobileCoreNetworkSspInterfaceSbb implements Sbb {

	private static final int EVENT_SUSPEND_TIMEOUT = 1000 * 60 * 3;
	private static final String CONTENT_MAIN_TYPE = "text";
	private static final String CONTENT_SUB_TYPE = "xml";
	private static final String CONTENT_TYPE = CONTENT_MAIN_TYPE + "/" + CONTENT_SUB_TYPE;
	
	protected SbbContextExt sbbContext;

	private Tracer logger;

	protected static final ResourceAdaptorTypeID capRATypeID = 
		new ResourceAdaptorTypeID("CAPResourceAdaptorType",
		"org.mobicents", "2.0");
	protected static final String capRaLink = "CAPRA";
	protected CAPContextInterfaceFactory capAcif;
	protected CAPProvider capProvider;
	protected CAPParameterFactory capParameterFactory;

	protected EventsSerializeFactory eventsSerializeFactory = null;

	protected ParameterFactory sccpParameterFact;

	protected TimerFacility timerFacility = null;

	 private CAPDialogCircuitSwitchedCall currentCapDialog;
	 private CallContent cc;
	 ArrayList<BCSMEvent> bcsmEventList = new ArrayList<BCSMEvent>();	

	// -------------------------------------------------------------
	// HTTP Server RA STUFF
	// -------------------------------------------------------------
	protected static final ResourceAdaptorTypeID httpServerRATypeID = new ResourceAdaptorTypeID(
			"HttpServletResourceAdaptorType", "org.mobicents", "1.0");
	protected static final String httpServerRaLink = "HttpServletRA";

	protected HttpServletRaSbbInterface httpServletProvider;
	protected HttpServletRaActivityContextInterfaceFactory httpServletRaActivityContextInterfaceFactory;



	private static final SspPropertiesManagement mscPropertiesManagement = SspPropertiesManagement.getInstance();

	private SccpAddress gmlcSCCPAddress = null;

	
    /**
     * HTTP Request Types (GET)
     */
    private enum HttpRequestType {
        REST("rest"),
        UNSUPPORTED("404");

        private String path;

        HttpRequestType(String path) {
            this.path = path;
        }

        public String getPath() {
            return String.format("/ssp/%s", path);
        }

        public static HttpRequestType fromPath(String path) {
            for (HttpRequestType type: values()) {
                if (path.equals(type.getPath())) {
                    return type;
                }
            }

            return UNSUPPORTED;
        }
    }

    /**
     * Request
     */
    private class HttpRequest implements Serializable {
        HttpRequestType type;
        String msisdn;

        public HttpRequest(HttpRequestType type, String msisdn) {
            this.type = type;
            this.msisdn = msisdn;
        }

        public HttpRequest(HttpRequestType type) {
            this(type, "");
        }
    }



	/** Creates a new instance of CallSbb */
	public MobileCoreNetworkSspInterfaceSbb() {
	}

	protected EventsSerializeFactory getEventsSerializeFactory() {
		if (this.eventsSerializeFactory == null) {
			this.eventsSerializeFactory = new EventsSerializeFactory();
		}
		return this.eventsSerializeFactory;
	}


    public void sendInitialDP() throws CAPException {


  	EncodingScheme es = new BCDEvenEncodingScheme();
   	GlobalTitle GTSSF = new GlobalTitle0100Impl(
    			"923330053111", 0, es,
    			org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
    		    NatureOfAddress.INTERNATIONAL );
        SccpAddress origAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, GTSSF,  1, 146);
     	GlobalTitle GTSCF = new GlobalTitle0100Impl(
    			"923330051123", 0, es,
    			org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
    		    NatureOfAddress.INTERNATIONAL );

        SccpAddress remoteAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, GTSCF, 2, 146);


        int serviceKey = 1;

        CalledPartyNumber cdpa = this.capProvider.getISUPParameterFactory().createCalledPartyNumber();
        cdpa.setAddress("552348762");
        cdpa.setNatureOfAddresIndicator(NAINumber._NAI_INTERNATIONAL_NUMBER);
        cdpa.setNumberingPlanIndicator(CalledPartyNumber._NPI_ISDN);
        cdpa.setInternalNetworkNumberIndicator(CalledPartyNumber._INN_ROUTING_ALLOWED);
        CalledPartyNumberCap calledPartyNumber=null;
		try {
			calledPartyNumber = this.capProvider.getCAPParameterFactory()
			        .createCalledPartyNumberCap(cdpa);
		} catch (CAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        CallingPartyNumber cgpa = this.capProvider.getISUPParameterFactory().createCallingPartyNumber();
        cgpa.setAddress("55998223");
        cgpa.setNatureOfAddresIndicator(NAINumber._NAI_INTERNATIONAL_NUMBER);
        cgpa.setNumberingPlanIndicator(CalledPartyNumber._NPI_ISDN);
        cgpa.setAddressRepresentationREstrictedIndicator(CallingPartyNumber._APRI_ALLOWED);
        cgpa.setScreeningIndicator(CallingPartyNumber._SI_NETWORK_PROVIDED);
        CallingPartyNumberCap callingPartyNumber = null;
		try {
			callingPartyNumber = this.capProvider.getCAPParameterFactory()
			        .createCallingPartyNumberCap(cgpa);
		} catch (CAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


        LocationNumber locNum = this.capProvider.getISUPParameterFactory()
				.createLocationNumber();
        locNum.setAddress("55200001");
        locNum.setNatureOfAddresIndicator(NAINumber._NAI_INTERNATIONAL_NUMBER);
        locNum.setNumberingPlanIndicator(LocationNumber._NPI_ISDN);
        locNum.setAddressRepresentationRestrictedIndicator(LocationNumber._APRI_ALLOWED);
        locNum.setScreeningIndicator(LocationNumber._SI_NETWORK_PROVIDED);
        locNum.setInternalNetworkNumberIndicator(LocationNumber._INN_ROUTING_ALLOWED);
        LocationNumberCap locationNumber=null;
		try {
			locationNumber = this.capProvider.getCAPParameterFactory()
			        .createLocationNumberCap(locNum);
		} catch (CAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        ISDNAddressString vlrNumber = this.capProvider.getMAPParameterFactory()
                .createISDNAddressString(AddressNature.international_number, NumberingPlan.ISDN, "552000002");
        LocationInformation locationInformation = this
                .capProvider
                .getMAPParameterFactory()
                .createLocationInformation(10, null, vlrNumber, null, null, null, null, vlrNumber, null, false, false, null,
                        null);


	EventTypeBCSM eventTypeBCSM = EventTypeBCSM.collectedInfo;

        // First create Dialog

        CAPApplicationContext acn = CAPApplicationContext.CapV2_gsmSSF_to_gsmSCF;
        CAPDialogCircuitSwitchedCall capDialog = this.capProvider.getCAPServiceCircuitSwitchedCall()
        		.createNewDialog(acn, origAddress, remoteAddress);
        capDialog = capProvider.getCAPServiceCircuitSwitchedCall().createNewDialog(acn, origAddress, remoteAddress);

        capDialog.addInitialDPRequest(serviceKey, calledPartyNumber, callingPartyNumber, null, null, null,
                locationNumber, null, null, null, null, null, eventTypeBCSM, null, null, null, null, null, null, null, false,
                null, null, locationInformation, null, null, null, null, null, false, null);

	if (this.currentCapDialog == null)
	{
	 this.currentCapDialog = capDialog;
	 this.cc = new CallContent();
	 this.cc.step = Step.initialDPSent;
	 this.cc.calledPartyNumber = calledPartyNumber;
	 this.cc.callingPartyNumber = callingPartyNumber;
         this.capAcif.getActivityContextInterface(capDialog).attach(this.sbbContext.getSbbLocalObject());

         // This will initiate the TC-BEGIN with INVOKE component
         capDialog.send();
	}


   }    



	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = (SbbContextExt) sbbContext;
		this.logger = 	sbbContext
		.getTracer(MobileCoreNetworkSspInterfaceSbb.class.getSimpleName());

		this.eventsSerializeFactory = new EventsSerializeFactory();

		// CAP RA
		this.capAcif = (CAPContextInterfaceFactory) this.sbbContext.getActivityContextInterfaceFactory(capRATypeID);
		this.capProvider = (CAPProvider) this.sbbContext.getResourceAdaptorInterface(capRATypeID, capRaLink);
		this.capParameterFactory = this.capProvider.getCAPParameterFactory();


		this.sccpParameterFact = new ParameterFactoryImpl();

		this.timerFacility = this.sbbContext.getTimerFacility();
		//this.httpServletRaActivityContextInterfaceFactory = 
		//(HttpServletRaActivityContextInterfaceFactory) this.sbbContext
		//.getActivityContextInterfaceFactory(httpServerRATypeID);
		//this.httpServletProvider = (HttpServletRaSbbInterface) this.sbbContext
		//.getResourceAdaptorInterface(httpServerRATypeID, httpServerRaLink);
	

		//this.logger.info("setSbbContext() complete");
	}

	public void unsetSbbContext() {
		this.sbbContext = null;
		this.logger = null;
	}

	public void sbbCreate() throws CreateException {
		if (this.logger.isFineEnabled()) {
			this.logger.fine("Created KnowledgeBase");
		}
	}

	public void sbbPostCreate() throws CreateException {

	}

	public void sbbActivate() {
	}

	public void sbbPassivate() {
	}

	public void sbbLoad() {
	}

	public void sbbStore() {
	}

	public void sbbRemove() {
	}

	public void sbbExceptionThrown(Exception exception, 
		Object object, ActivityContextInterface activityContextInterface) {
	}

	public void sbbRolledBack(RolledBackContext rolledBackContext) {
	}


	public void onServiceStartedEvent(javax.slee.serviceactivity.ServiceStartedEvent event, ActivityContextInterface aci/*, EventContext eventContext*/) {
		logger.info("SSP Service started");
		this.currentCapDialog = null;
	}


	public void onDIALOG_ACCEPT(org.mobicents.slee.resource.cap.events.DialogAccept event, 
		ActivityContextInterface aci) {
	}



	public void onDIALOG_CLOSE(org.mobicents.slee.resource.cap.events.DialogClose event, 
		ActivityContextInterface aci) {

		logger.info("onDialogClose");

		/*        
		XmlCAPDialog dialog = this.getXmlCAPDialog();
        	dialog.setTCAPMessageType(event.getCAPDialog().getTCAPMessageType());

        	try {
            		byte[] data = this.getEventsSerializeFactory().serialize(dialog);
            		this.sendXmlPayload(data);
        	} catch (Exception e) {
            		logger.severe(String.format("Exception while sending onDIALOG_CLOSE to an application", event), e);
            		this.endHttpClientActivity();
            		return;
        	}
        	dialog.reset();
        	this.setXmlCAPDialog(dialog);
        	this.setCapDialogClosed(true);
        	this.updateDialogTime();
		*/

	}

    public void onDIALOG_DELIMITER(org.mobicents.slee.resource.cap.events.DialogDelimiter event, 
		ActivityContextInterface aci) {
    
		logger.info("onDIALOG_DELIMTER");
		 if (this.currentCapDialog != null && this.cc != null && this.cc.step != Step.disconnected) {
	            if (this.cc.activityTestInvokeId != null) {
	                try {
	                    currentCapDialog.addActivityTestResponse(this.cc.activityTestInvokeId);
	                    this.cc.activityTestInvokeId = null;
	                    currentCapDialog.send();
	                } catch (CAPException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            }
	        }



		/*    
		XmlCAPDialog dialog = this.getXmlCAPDialog();
        	dialog.setTCAPMessageType(event.getCAPDialog().getTCAPMessageType());
        	this.resetTimer(aci);
        
        	try {
            		byte[] data = this.getEventsSerializeFactory().serialize(dialog);
            		this.sendXmlPayload(data);
        	} catch (Exception e) {
            		logger.severe(String.format("Exception while sending onDIALOG_DELIMITER to an application", 
			event), e);
            		// TODO Abort DIalog?
        	}
        	dialog.reset();
        	this.setXmlCAPDialog(dialog);
		*/
    }


	public void onDIALOG_NOTICE(org.mobicents.slee.resource.cap.events.DialogNotice event, ActivityContextInterface aci) {
		logger.warning(String.format("onDIALOG_NOTICE for Dialog=%s", event.getCAPDialog()));
	}


	public void onDIALOG_PROVIDERABORT(org.mobicents.slee.resource.cap.events.DialogProviderAbort event,
			ActivityContextInterface aci) {

		logger.warning(String.format("onDIALOG_PROVIDERABORT for Dialog=%s", event.getCAPDialog()));

	/*
        XmlCAPDialog dialog = this.getXmlCAPDialog();
        dialog.reset();
        dialog.setPAbortCauseType(event.getPAbortCauseType());
        dialog.setTCAPMessageType(event.getCAPDialog().getTCAPMessageType());

        try {
            byte[] data = this.getEventsSerializeFactory().serialize(dialog);
            this.sendXmlPayload(data);
        } catch (Exception e) {
            logger.severe(String.format("Exception while sending onDIALOG_PROVIDERABORT to an application", event), e);
            this.endHttpClientActivity();
        }

        this.setCapDialogClosed(true);
		*/

	}


	public void onDIALOG_RELEASE(org.mobicents.slee.resource.cap.events.DialogRelease event,
			ActivityContextInterface aci) {

		logger.info("onDIALOG_RELEASE");
		this.currentCapDialog = null;
		//this.cc = null;

		//just to be safe
		//this.cancelTimer();
	}


	public void onINITIAL_DP_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.InitialDPRequest event,
			ActivityContextInterface aci) {

	
		/*
		XmlCAPDialog dialog = this.getXmlCAPDialog();
		dialog.addCAPMessage(((CAPEvent) event).getWrappedEvent());
		this.setXmlCAPDialog(dialog);

        	camelStatAggregator.updateMessagesRecieved();
        	camelStatAggregator.updateMessagesAll();
		*/
	}


	public void onINITIATE_CALL_ATTEMPT_REQUEST(
		org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.InitiateCallAttemptRequest event,
			ActivityContextInterface aci) {
	}


	public void onINITIATE_CALL_ATTEMPT_RESPONSE(
		org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.InitiateCallAttemptResponse event,
			ActivityContextInterface aci) {
	}


	public void onMOVE_LEG_REQUEST(
		org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.MoveLegRequest event, 
			ActivityContextInterface aci) {
	}

	public void onMOVE_LEG_RESPONSE(
		org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.MoveLegResponse event, 
			ActivityContextInterface aci) {
	}


	public void onACTIVITY_TEST_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.ActivityTestRequest event,
			ActivityContextInterface aci) {
		logger.info("onACTIVITY_TEST_REQUEST");
	 if (this.currentCapDialog != null && this.cc != null && this.cc.step != Step.disconnected) {
	            this.cc.activityTestInvokeId = event.getInvokeId();
	        }

	}

	public void onACTIVITY_TEST_RESPONSE(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.ActivityTestResponse event,
			ActivityContextInterface aci) {
	}


	public void onAPPLY_CHARGING_REPORT_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.ApplyChargingReportRequest event,
			ActivityContextInterface aci) {
	/*
        XmlCAPDialog dialog = this.getXmlCAPDialog();
        dialog.addCAPMessage(((CAPEvent) event).getWrappedEvent());
        this.setXmlCAPDialog(dialog);

        camelStatAggregator.updateMessagesRecieved();
        camelStatAggregator.updateMessagesAll();
	*/

	}

	public void onAPPLY_CHARGING_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.ApplyChargingRequest event,
			ActivityContextInterface aci) {
	}


	public void onDIALOG_REQUEST(org.mobicents.slee.resource.cap.events.DialogRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {


		logger.info("onDIALOG_REQUEST");
		/*
		CAPDialog capDialog = event.getCAPDialog();
		XmlCAPDialog dialog = new XmlCAPDialog(capDialog.getApplicationContext(), capDialog.getLocalAddress(),
				capDialog.getRemoteAddress(), capDialog.getLocalDialogId(), capDialog.getRemoteDialogId());
		int networkId = capDialog.getNetworkId();
		dialog.setNetworkId(networkId);
		this.setXmlCAPDialog(dialog);

		NetworkRoutingRule networkRoutingRule = networkRoutingRuleManagement.getNetworkRoutingRule(networkId);

		if (networkRoutingRule == null) {
			String route = camlePropertiesManagement.getRoute();
			if (logger.isFineEnabled()) {
				logger.fine("No NetworkRoutingRule configured for network-id " + networkId + " Using the default one "
						+ route);
			}
			networkRoutingRule = new NetworkRoutingRule();
			networkRoutingRule.setRuleUrl(route);
		}

		this.setNetworkRoutingRule(networkRoutingRule);

		if (networkRoutingRule.getRuleUrl() == null) {
			logger.warning("No routing rule defined for networkId " + networkId
					+ " Disconnecting from ACI, no new messages will be processed");
			aci.detach(this.sbbContext.getSbbLocalObject());

		}

        camelStatAggregator.updateDialogsAllEstablished();
        this.setStartDialogTime(System.currentTimeMillis());
		*/
	}


	public void onDIALOG_TIMEOUT(org.mobicents.slee.resource.cap.events.DialogTimeout event,
			ActivityContextInterface aci) {
//        if (logger.isFineEnabled()) {
            logger.info(String.format("onDIALOG_TIMEOUT for Dialog=%s Calling keepAlive", event.getCAPDialog()));
        

		event.getCAPDialog().keepAlive();
	}


	public void onDIALOG_USERABORT(org.mobicents.slee.resource.cap.events.DialogUserAbort event,
			ActivityContextInterface aci) {
		logger.warning(String.format("onDIALOG_USERABORT for Dialog=%s", event.getCAPDialog()));

	/*
        XmlCAPDialog dialog = this.getXmlCAPDialog();
        dialog.reset();
        try {
            dialog.abort(event.getUserReason());
        } catch (CAPException e1) {
            // This can not occur
            e1.printStackTrace();
        }
        dialog.setTCAPMessageType(event.getCAPDialog().getTCAPMessageType());

        try {
            byte[] data = this.getEventsSerializeFactory().serialize(dialog);
            this.sendXmlPayload(data);
        } catch (Exception e) {
            logger.severe(String.format("Exception while sending onDIALOG_USERABORT to an application", event), e);
            this.endHttpClientActivity();
        }

        this.setCapDialogClosed(true);
	*/

	}


	public void onASSIST_REQUEST_INSTRUCTIONS_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.AssistRequestInstructionsRequest event,
			ActivityContextInterface aci) {
	}

	public void onCALL_INFORMATION_REPORT_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.CallInformationReportRequest event,
			ActivityContextInterface aci) {
	}

	public void onCALL_INFORMATION_REQUEST_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.CallInformationRequestRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}

	public void onCANCEL_REQUEST(org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.CancelRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}

	public void onCONNECT_REQUEST(org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.ConnectRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}

	public void onCONNECT_TO_RESOURCE_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.ConnectToResourceRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}

	public void onCONTINUE_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.ContinueRequest event,
			ActivityContextInterface aci) {
	  logger.info("onCONTINUE_REQUEST");
	if (this.cc != null)
			this.cc.step = Step.callAllowed;
		else
			logger.info("cc = null\r\n\r\n");
        event.getCAPDialog().processInvokeWithoutAnswer(event.getInvokeId());
    
	}

	public void onDISCONNECT_FORWARD_CONNECTION_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.DisconnectForwardConnectionRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}

	public void onESTABLISH_TEMPORARY_CONNECTION_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.EstablishTemporaryConnectionRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}


	public void onEVENT_REPORT_BCSM_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.EventReportBCSMRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {

	logger.info("onEVENT_REPORT_BCSM_REQUEST");
	/*
        XmlCAPDialog dialog = this.getXmlCAPDialog();
        dialog.addCAPMessage(((CAPEvent) event).getWrappedEvent());
        this.setXmlCAPDialog(dialog);

        camelStatAggregator.updateMessagesRecieved();
        camelStatAggregator.updateMessagesAll();
	*/
	}


	public void onREQUEST_REPORT_BCSM_EVENT_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.RequestReportBCSMEventRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {

	   logger.info("onREQUEST_REPORT_BCSM_EVENT_REQEST");
	if (this.currentCapDialog != null && this.cc != null && this.cc.step != Step.disconnected) {
		this.cc.requestReportBCSMEventRequest = event;
            	this.bcsmEventList = event.getBCSMEventList();
	}
        event.getCAPDialog().processInvokeWithoutAnswer(event.getInvokeId());	

	}


	public void onFURNISH_CHARGING_INFORMATION_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.FurnishChargingInformationRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}

	public void onPLAY_ANNOUNCEMENT_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.PlayAnnouncementRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}

	public void onRELEASE_CALL_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.ReleaseCallRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}

	public void onPROMPT_AND_COLLECT_USER_INFORMATION_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.PromptAndCollectUserInformationRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}

    public void onPROMPT_AND_COLLECT_USER_INFORMATION_RESPONSE(
            org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.PromptAndCollectUserInformationResponse event,
            ActivityContextInterface aci/* , EventContext eventContext */) {

	/*
        XmlCAPDialog dialog = this.getXmlCAPDialog();
        dialog.addCAPMessage(((CAPEvent) event).getWrappedEvent());
        this.setXmlCAPDialog(dialog);

        camelStatAggregator.updateMessagesRecieved();
        camelStatAggregator.updateMessagesAll();
	*/
    }

	public void onRESET_TIMER_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.ResetTimerRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}

	public void onSEND_CHARGING_INFORMATION_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.SendChargingInformationRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}

	public void onSPECIALIZED_RESOURCE_REPORT_REQUEST(
			org.mobicents.protocols.ss7.cap.api.service.circuitSwitchedCall.SpecializedResourceReportRequest event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	}


	public void onINVOKE_TIMEOUT(org.mobicents.slee.resource.cap.events.InvokeTimeout event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
		logger.info("onINVOKE_TIMEOUT");
	}

	public void onERROR_COMPONENT(org.mobicents.slee.resource.cap.events.ErrorComponent event,
			ActivityContextInterface aci/* , EventContext eventContext */) {
	 logger.info("onERROR_COMPONENT");

	/*
        XmlCAPDialog dialog = this.getXmlCAPDialog();
        try {
            dialog.sendErrorComponent(event.getInvokeId(), event.getCAPErrorMessage());
        } catch (CAPException e) {
            // This never occur
            e.printStackTrace();
        }
        this.setXmlCAPDialog(dialog);
	*/
	}

	public void onREJECT_COMPONENT(org.mobicents.slee.resource.cap.events.RejectComponent event,
			ActivityContextInterface aci/* , EventContext eventContext */) {

	logger.info("onREJECT_COMPONENT");
	/*
        XmlCAPDialog dialog = this.getXmlCAPDialog();
        try {
            dialog.sendRejectComponent(event.getInvokeId(), event.getProblem());
        } catch (CAPException e) {
            // This never occur
            e.printStackTrace();
        }
        this.setXmlCAPDialog(dialog);
	*/
	}








    /**
     * Handle HTTP POST request
     * @param event
     * @param aci
     * @param eventContext
     */
    public void onPost(net.java.slee.resource.http.events.HttpServletRequestEvent event, ActivityContextInterface aci, EventContext eventContext) {

	logger.info("onPost");

        setEventContextCMP(eventContext);
 

     //   onRequest(event, aci, eventContext);
    }


	public void onSessionPost(HttpServletRequestEvent event, ActivityContextInterface aci, EventContext eventContext) {
		if (logger.isInfoEnabled())
			logger.info("Received session POST");
 
		if (this.getEventContextCMP() != null) {
			if (logger.isSevereEnabled())
				logger.severe("Detected previous event context: " + getEventContextCMP());
			// TODO: send error
			return;
		}

   }

    /**
     * Handle HTTP GET request
     * @param event
     * @param aci
     * @param eventContext
     */
	public void onGet(net.java.slee.resource.http.events.HttpServletRequestEvent event, ActivityContextInterface aci,
			EventContext eventContext) {
	logger.info("onGet");
        onRequest(event, aci, eventContext);
	}

    /**
     * Entry point for all location lookups
     * Assigns a protocol handler to the request based on the path
     */
    private void onRequest(net.java.slee.resource.http.events.HttpServletRequestEvent event, ActivityContextInterface aci,
                           EventContext eventContext) {
        setEventContextCMP(eventContext);
        HttpServletRequest httpServletRequest = event.getRequest();
        HttpRequestType httpRequestType = HttpRequestType.fromPath(httpServletRequest.getPathInfo());

//	logger.info("httpServletRequest.getPathInfo()="+httpServletRequest.getPathInfo());
        setHttpRequest(new HttpRequest(httpRequestType));



	try {
	java.io.BufferedReader rd = httpServletRequest.getReader();
 	logger.info("rd=" + rd);
	
	String rline = "";
	  while ((rline = rd.readLine()) != null) {
        	logger.info(rline);
              } 
	} catch (Exception e) {
	  logger.severe("Exception while getting params ", e);
	}

	logger.info("request=" + httpServletRequest);
	logger.info("httpRequestType="+httpRequestType);
        String idp = null;
	String busy=null;
	logger.info("httpServletRequest.getQueryString()="+httpServletRequest.getQueryString());
        switch (httpRequestType) {
            case REST:
	        idp = httpServletRequest.getParameter("idp");
		if (idp == null)
			busy = httpServletRequest.getParameter("busy");
                break;
            default:
                sendHTTPResult(HttpServletResponse.SC_NOT_FOUND, "Request URI unsupported");
                return;
        }


        setHttpRequest(new HttpRequest(httpRequestType, null));
        if (logger.isInfoEnabled()){
            logger.info(String.format("Handling %s request, operation: %s", httpRequestType.name().toUpperCase(), idp+busy));
        }

        if (idp != null || busy != null) {
	    switch(httpRequestType) {
		case REST:
	            //eventContext.suspendDelivery();
	            sendCamelOperation(idp, busy);
		    break;
	    }
        } else {
            logger.info("MSISDN is null, sending back -1 for Global Cell Identity");
            handleLocationResponse( "Invalid MSISDN specified", null);
        }
    }

	/**
	 * CMP
	 */

	public abstract void setXmlCAPDialog(XmlCAPDialog dialog);

	public abstract XmlCAPDialog getXmlCAPDialog();


	public abstract void setEventContextCMP(EventContext eventContext);

	public abstract EventContext getEventContextCMP();



    public abstract void setHttpRequest(HttpRequest httpRequest);

    public abstract HttpRequest getHttpRequest();

  
	/*
	protected SccpAddress getGmlcSccpAddress() {
		if (this.gmlcSCCPAddress == null) {
            GlobalTitle gt = sccpParameterFact.createGlobalTitle(mscPropertiesManagement.getMscGt(), 0,
                    NumberingPlan.ISDN_TELEPHONY, null, NatureOfAddress.INTERNATIONAL);
            this.gmlcSCCPAddress = sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE,
                    gt, 0, mscPropertiesManagement.getGmlcSsn());

//			GlobalTitle0100 gt = new GlobalTitle0100Impl(gmlcPropertiesManagement.getGmlcGt(),0,BCDEvenEncodingScheme.INSTANCE,NumberingPlan.ISDN_TELEPHONY,NatureOfAddress.INTERNATIONAL);
//			this.serviceCenterSCCPAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0, gmlcPropertiesManagement.getGmlcSsn());
		}
		return this.gmlcSCCPAddress;
	}

	private SccpAddress getHlrSCCPAddress(String address) {
        GlobalTitle gt = sccpParameterFact.createGlobalTitle(address, 0, NumberingPlan.ISDN_TELEPHONY, null,
                NatureOfAddress.INTERNATIONAL);
        return sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0,
                mscPropertiesManagement.getHlrSsn());

//	    GlobalTitle0100 gt = new GlobalTitle0100Impl(address, 0, BCDEvenEncodingScheme.INSTANCE,NumberingPlan.ISDN_TELEPHONY, NatureOfAddress.INTERNATIONAL);
//		return new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0, gmlcPropertiesManagement.getHlrSsn());
	}

	*/

	private void sendCamelOperation(String idp, String busy) {


   		
        try {
		this.sendInitialDP();
		} catch (CAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}





    /**
     * Handle generating the appropriate HTTP response
     * We're making use of the MLPResponse class for both GET/POST requests for convenience and
     * because eventually the GET method will likely be removed
     * @param mlpResultType OK or error type to return to client
     * @param response CGIResponse on location attempt
     * @param mlpClientErrorMessage Error message to send to client
     */
    private void handleLocationResponse( String response, String mlpClientErrorMessage) {
        HttpRequest request = getHttpRequest();

                	StringBuilder getResponse = new StringBuilder();

	//		getResponse.append("text=");
			if (response != null)
			getResponse.append(response);


                    this.sendHTTPResult(HttpServletResponse.SC_OK, getResponse.toString());
    }

    /**
     * Return the specified response data to the HTTP client
     * @param responseData Response data to send to client
     */
	private void sendHTTPResult(int statusCode, String responseData) {
		try {
			EventContext ctx = this.getEventContextCMP();
            if (ctx == null) {
                if (logger.isWarningEnabled()) {
                    logger.warning("When responding to HTTP no pending HTTP request is found, responseData=" + responseData);
                    return;
                }
            }

	        HttpServletRequestEvent event = (HttpServletRequestEvent) ctx.getEvent();

			HttpServletResponse response = event.getResponse();
                        response.setStatus(statusCode);
            PrintWriter w = null;
            w = response.getWriter();
            w.print(responseData);
			w.flush();
			response.flushBuffer();

			if (ctx.isSuspended()) {
				ctx.resumeDelivery();
			}

			if (logger.isInfoEnabled()){
			    logger.info("HTTP Request received and response sent, responseData=" + responseData);
			}

			// getNullActivity().endActivity();
		} catch (Exception e) {
			logger.severe("Error while sending back HTTP response", e);
		}
	}



    private void endHttpSessionActivity() {
        HttpSessionActivity httpSessionActivity = this.getHttpSessionActivity();
        if (httpSessionActivity != null) {
            httpSessionActivity.endActivity();
        }
    }

    private HttpSessionActivity getHttpSessionActivity() {
        ActivityContextInterface[] acis = this.sbbContext.getActivities();
        for (ActivityContextInterface aci : acis) {
            Object activity = aci.getActivity();
            if (activity instanceof HttpSessionActivity) {
                return (HttpSessionActivity) activity;
            }
        }
        return null;
    }






  
  

  private EventContext resumeHttpEventContext() {
        EventContext httpEventContext = getEventContextCMP();

        if (httpEventContext == null) {
            logger.severe("No HTTP event context, can not resume ");
            return null;
        }

        httpEventContext.resumeDelivery();
        return httpEventContext;
    }


private void sendHttpResponse() {
        try {
            if (logger.isFineEnabled())
                logger.fine("About to send HTTP response.");

            XmlCAPDialog dialog = getXmlCAPDialog();
            byte[] data = getEventsSerializeFactory().serialize(dialog);

            if (logger.isFineEnabled()) {
                logger.fine("Sending HTTP Response Payload = \n" + new String(data));
            }

            EventContext httpEventContext = this.resumeHttpEventContext();

            if (httpEventContext == null) {
                 // TODO: terminate dialog?
                logger.severe("No HTTP event context, can not deliver response for MapXmlDialog: " + dialog);
                return;
            }

            HttpServletRequestEvent httpRequest = (HttpServletRequestEvent) httpEventContext.getEvent();
            HttpServletResponse response = httpRequest.getResponse();
            response.setStatus(HttpServletResponse.SC_OK);
            try {
                response.getOutputStream().write(data);
                response.getOutputStream().flush();
            } catch (NullPointerException npe) {
                logger.warning(  "Probably HTTPResponse already sent by HTTP-Servlet-RA. Increase HTTP_REQUEST_TIMEOUT in deploy-config.xml of RA to be greater than TCAP Dialog timeout", npe);
            }

        } catch (XMLStreamException xmle) {
            logger.severe("Failed to serialize dialog", xmle);
        } catch (IOException e) {
            logger.severe("Failed to send answer!", e);
        }

    }

 
	public enum Step {
        initialDPSent, callAllowed, answered, disconnected, notanswered, calledbusy, oabandon;
    }

    public class CallContent {
        public Step step;
        public Long activityTestInvokeId;

        public CalledPartyNumberCap calledPartyNumber;
        public CallingPartyNumberCap callingPartyNumber;
        public RequestReportBCSMEventRequest requestReportBCSMEventRequest;
        public DestinationRoutingAddress destinationRoutingAddress;
    }
	
          

}
