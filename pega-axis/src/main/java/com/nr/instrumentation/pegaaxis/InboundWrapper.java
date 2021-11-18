package com.nr.instrumentation.pegaaxis;

import javax.xml.soap.MimeHeaders;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;

public class InboundWrapper implements InboundHeaders {

	private MimeHeaders headers;
	
	public InboundWrapper(MimeHeaders h) {
		headers = h;
	}
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public String getHeader(String name) {
		if(headers != null) {
			return headers.getHeader(name)[0];
		}
		return null;
	}

}
