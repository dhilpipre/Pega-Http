package com.pega.apache.commons.httpclient;

import java.util.logging.Level;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.pega.httpclient.PegaHttpHeaders;

@Weave
public abstract class HttpClient {

	@Trace(leaf = true)
	public int executeMethod(HostConfiguration hostconfig, HttpMethod method, HttpState state) {
		PegaHttpHeaders headers = new PegaHttpHeaders(method);
		Transaction transaction = NewRelic.getAgent().getTransaction();
		transaction.insertDistributedTraceHeaders(headers);
			String protocol = hostconfig.getProtocol().getScheme();
			String host = hostconfig.getHost();
			int port = hostconfig.getPort();
			String path = method.getPath();
			String uriString;
			if(port > 0) {
				uriString = protocol + "://"+host+":"+port+"/"+path;
			} else {
				uriString = protocol + "://"+host+"/"+path;
			}
			java.net.URI uri = java.net.URI.create(uriString);
			NewRelic.getAgent().getLogger().log(Level.FINE, "Created URI: {0} from string: {1}", uri,uriString);
			HttpParameters params = HttpParameters.library("Pega-HttpCommons").uri(uri).procedure("executeMethod").extendedInboundHeaders(null).build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		
		int result =  Weaver.callOriginal();
		transaction.acceptDistributedTraceHeaders(TransportType.HTTP, headers);
		dumpHeaders(method);
		return result;
	}

	private void dumpHeaders(HttpMethod method) {
		Header[] requestHeaders = method.getRequestHeaders();
		Logger logger = NewRelic.getAgent().getLogger();
		if(requestHeaders != null && requestHeaders.length > 0) {
			logger.log(Level.FINE, "Request Headers found");
			for(Header header : requestHeaders)  {
				logger.log(Level.FINE, "Header: {0} - {1}", header.getName(),header.getValue());
			}
		} else {
			logger.log(Level.FINE, "No request headers found");
		}
		Header[] responseHeaders = method.getResponseHeaders();
		if(responseHeaders != null && responseHeaders.length > 0) {
			logger.log(Level.FINE, "Response Headers found");
			for(Header header : responseHeaders)  {
				logger.log(Level.FINE, "Header: {0} - {1}", header.getName(),header.getValue());
			}
		} else {
			logger.log(Level.FINE, "No response headers found");
		}
		
	}
}
