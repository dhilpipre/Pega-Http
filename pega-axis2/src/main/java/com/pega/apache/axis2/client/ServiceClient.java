package com.pega.apache.axis2.client;

import javax.xml.namespace.QName;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.pega.apache.axiom.om.OMElement;
import com.pega.apache.axis2.AxisFault;
import com.pega.apache.axis2.client.async.AxisCallback;

@Weave
public abstract class ServiceClient {

	@Trace(dispatcher = true)
	public OMElement sendReceive(QName operationQName, OMElement xmlPayload) throws AxisFault {
		return Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	public void sendReceiveNonBlocking(QName operation, OMElement elem, AxisCallback callback) {
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	public void sendRobust(QName operation, OMElement elem) {
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	public void fireAndForget(QName operation, OMElement elem) {
		Weaver.callOriginal();
	}
}
