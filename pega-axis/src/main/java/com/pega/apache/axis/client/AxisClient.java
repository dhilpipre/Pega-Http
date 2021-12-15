package com.pega.apache.axis.client;

import java.util.logging.Level;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.pega.apache.axis.MessageContext;

@Weave
public abstract class AxisClient {

	public abstract String getName();

	@Trace
	public void invoke(MessageContext msgContext) {
        String handlerName = msgContext.getStrProp(MessageContext.ENGINE_HANDLER);
        if(handlerName != null) {
        	AgentBridge.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","AxisClient","Handler",handlerName});
        } else {
    		String serviceName = null;
			try {
				serviceName = msgContext.getTargetService();
			} catch (Exception e) {
				AgentBridge.getAgent().getLogger().log(Level.FINER, e, "error calling getTargetServie()");
			}
    		
    		if(serviceName == null || serviceName.isEmpty()) {
        			serviceName = "UnknownService";
    		}
        	AgentBridge.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, true, "PegaAxis", new String[] {serviceName});
        }
		Weaver.callOriginal();
	}
}
