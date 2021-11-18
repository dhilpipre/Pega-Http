package com.pega.apache.axis2.transport.http;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.pega.apache.axis2.context.MessageContext;

import java.net.URL;

@Weave
public abstract class HTTPSender {

    @Trace
    public void send(MessageContext msgContext, URL url, String soapActionString) {
    	
        Weaver.callOriginal();
    }

}
