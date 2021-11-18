package com.pega.apache.axis.transport.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.agent.bridge.external.ExternalParameters;
import com.newrelic.agent.bridge.external.ExternalParametersFactory;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.pegaaxis.InboundWrapper;
import com.nr.instrumentation.pegaaxis.OutboundWrapper;
import com.pega.apache.axis.MessageContext;

@Weave
public abstract class CommonsHTTPSender {

	public abstract String getName();

	@Trace
	public void invoke(MessageContext msgContext) {
		String handlerName = getName();
		if(handlerName == null) {
			handlerName = "UnknownHandler";
		}
		AgentBridge.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","CommonsHTTPSender",handlerName});

		SOAPMessage reqMessage = msgContext.getRequestMessage();
		if(reqMessage != null) {
			MimeHeaders mimeHeaders = reqMessage.getMimeHeaders();
			OutboundWrapper outWrapper = new OutboundWrapper(mimeHeaders);
			AgentBridge.getAgent().getTransaction().getCrossProcessState().processOutboundRequestHeaders(outWrapper);
		}
        URL targetURL = null;
        
        try {
        	String targetStr = msgContext.getStrProp(MessageContext.TRANS_URL);
        	if(targetStr != null)
        		targetURL = new URL(msgContext.getStrProp(MessageContext.TRANS_URL));
		} catch (Exception e) {
			AgentBridge.getAgent().getLogger().log(Level.FINEST,e, "Exception getting URL");
		}
        String host = "UnknownHost";
        String uriStr = "UnknownURI";
        if(targetURL != null) {
        	try {
				URI uri = targetURL.toURI();
				uriStr = uri.toASCIIString();
				host = targetURL.getHost();
				ExternalParameters params = ExternalParametersFactory.createForHttp("Axis-"+handlerName, uri, "invoke");
				AgentBridge.getAgent().getTracedMethod().reportAsExternal(params);
			} catch (URISyntaxException e) {
				AgentBridge.getAgent().getLogger().log(Level.FINEST,e, "Exception getting URI from URL");
			}
        }
		Weaver.callOriginal();
		SOAPMessage respMessage = msgContext.getResponseMessage();
		if(respMessage != null) {
			MimeHeaders mimeHeaders = respMessage.getMimeHeaders();
			InboundWrapper inWrapper = new InboundWrapper(mimeHeaders);
			TracedMethod traced = AgentBridge.getAgent().getTracedMethod();
			AgentBridge.getAgent().getTransaction().getCrossProcessState().processInboundResponseHeaders(inWrapper, traced, host, uriStr , false);
		}
	}
}
