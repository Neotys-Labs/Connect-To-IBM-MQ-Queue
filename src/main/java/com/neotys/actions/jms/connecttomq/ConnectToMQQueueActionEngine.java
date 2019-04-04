package com.neotys.actions.jms.connecttomq;

import static com.neotys.jms.context.ContextUtils.getContext;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.common.base.Optional;
import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.neotys.actions.jms.connecttomq.ConnectToMQQueueArguments.ConnectToQueueOption;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;

public final class ConnectToMQQueueActionEngine implements ActionEngine {

	private static final String STATUS_CODE_INVALID_PARAMETER = "NL-CONNECTTOMQQUEUE-ACTION-01";
	private static final String STATUS_CODE_ERROR_QUEUE_CONNECTION = "NL-CONNECTTOMQQUEUE-ACTION-02";
	private static final String NEOLOAD_KEYSTORE_FORMAT = "PKCS12";
	private static final String NEOLOAD_CERTIFICATE_ALGORITHM = "SunX509";
	
	@SuppressWarnings("deprecation")
	@Override
	public SampleResult execute(final com.neotys.extensions.action.engine.Context context, final List<ActionParameter> parameters) {

		final Logger logger = context.getLogger();

		final SampleResult sampleResult = new SampleResult();
		final ConnectToMQQueueArguments connectToQueueArguments;
		try {
			connectToQueueArguments = new ConnectToMQQueueArguments(logger, parameters);
		} catch (final IllegalArgumentException iae) {
			sampleResult.setError(true);
			sampleResult.setStatusCode(STATUS_CODE_INVALID_PARAMETER);
			sampleResult.setResponseContent("Could not create parse arguments" + iae.toString());
			logger.error("Could not create parse arguments: ", iae);
			return sampleResult;
		}

		final Map<String, Optional<String>> parsedArgs = connectToQueueArguments.getParsedArgs();

		logger.debug("Executing ConnectToQueueActionEngine with parameters: " + ConnectToMQQueueArguments.getArgumentLogString(parsedArgs));
		
		final String queueManager = parsedArgs.get(ConnectToQueueOption.QueueManager.getName()).get();
		final String hostName = parsedArgs.get(ConnectToQueueOption.HostName.getName()).get();
		final String port = parsedArgs.get(ConnectToQueueOption.Port.getName()).get();
		final String channel = parsedArgs.get(ConnectToQueueOption.Channel.getName()).get();
		final String queueName = parsedArgs.get(ConnectToQueueOption.QueueName.getName()).get();
		final Optional<String> ccsid = parsedArgs.get(ConnectToQueueOption.CCSID.getName());
		final Optional<String> transportProperty = parsedArgs.get(ConnectToQueueOption.TransportProperty.getName());
		final Optional<String> debugParam = parsedArgs.get(ConnectToQueueOption.Debug.getName());
		final boolean isDebug = debugParam.isPresent() && "true".equals(debugParam.get());
		final Optional<String> sslFipsRequiredParam = parsedArgs.get(ConnectToQueueOption.SslFipsRequired.getName());
		final boolean isSslFipsRequired = sslFipsRequiredParam.isPresent() && "true".equals(sslFipsRequiredParam.get());
		final Optional<String> useIBMCipherMappings = parsedArgs.get(ConnectToQueueOption.UseIBMCipherMappings.getName());
		final Optional<String> preferTLSParam = parsedArgs.get(ConnectToQueueOption.PreferTLS.getName());
		final boolean isPreferTLS = preferTLSParam.isPresent() && "true".equals(preferTLSParam.get());
		final Optional<String> sslCipherSuite = parsedArgs.get(ConnectToQueueOption.SslCipherSuite.getName());
		final Optional<String> sslPeerName = parsedArgs.get(ConnectToQueueOption.SslPeerName.getName());
		final Optional<String> sslProtocol = parsedArgs.get(ConnectToQueueOption.SslProtocol.getName());		
		
		final MQQueueConnectionFactory mqQueueConnectionFactory;
		try {
			mqQueueConnectionFactory = new MQQueueConnectionFactory();
			mqQueueConnectionFactory.setQueueManager(queueManager);
			mqQueueConnectionFactory.setHostName(hostName);
			mqQueueConnectionFactory.setPort(Integer.parseInt(port));
			mqQueueConnectionFactory.setChannel(channel);			
			mqQueueConnectionFactory.setTransportType(com.ibm.mq.jms.JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);			
			if(ccsid.isPresent()){
				mqQueueConnectionFactory.setCCSID(Integer.parseInt(ccsid.get()));
			}
			if(transportProperty.isPresent()){
				MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, transportProperty.get());
			}
			if(isDebug){
				System.setProperty("javax.net.debug", "true");
			}
			MQEnvironment.sslFipsRequired = isSslFipsRequired;
			mqQueueConnectionFactory.setSSLFipsRequired(isSslFipsRequired);
			if(useIBMCipherMappings.isPresent()){
				System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", useIBMCipherMappings.get());
			}
			System.setProperty("com.ibm.mq.cfg.preferTLS", String.valueOf(isPreferTLS));
			if(isPreferTLS){				
									
				final SSLContext sslContext;
				if(sslProtocol.isPresent()){
					if(isDebug){
						logger.debug("Getting instance of SSL Context on protocol: " + sslProtocol.get());
					}
					sslContext = SSLContext.getInstance(sslProtocol.get());
				} else {
					if(isDebug){
						logger.debug("Getting instance of SSL Context on default protocol");
					}
					sslContext = SSLContext.getDefault();
				}		
				
				final String certificateName = context.getCertificateManager().getCertificateName();
				if (certificateName != null) {
					if(isDebug){
						logger.debug("Use certificate as specified in NeoLoad Certificates Manager.");
					}
					final char[] certificatePassword = context.getCertificateManager().getCertificatePassword().toCharArray();
					final String certificateFolder = context.getCertificateManager().getCertificateFolder();
					final String certificatePath = certificateFolder + File.separator + certificateName;
					final InputStream certificateInputStream = context.getFileManager().getFileInputStream(certificatePath);
					final KeyStore keystore = KeyStore.getInstance(NEOLOAD_KEYSTORE_FORMAT);				
					keystore.load(certificateInputStream, certificatePassword);
					if(isDebug){
						logger.debug("Number of keys on JKS: " + Integer.toString(keystore.size()));
					}
					final KeyManagerFactory kmf = KeyManagerFactory.getInstance(NEOLOAD_CERTIFICATE_ALGORITHM);
					if(isDebug){
						logger.debug("Init key manager");
					}
					kmf.init(keystore, certificatePassword);
					
					final TrustManager[] trustAllCerts = new TrustManager[] {
							new X509TrustManager() {
								@Override
								public X509Certificate[] getAcceptedIssuers() {
									return new X509Certificate[0];
								}
								@Override
								@SuppressWarnings("squid:S4424")
								public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
									// Trust all client certificates
								}
								@Override
								@SuppressWarnings("squid:S4424")
								public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
									// Trust all server certificates
								}
							}
					};
					if(isDebug){
						logger.debug("Init SSL context");
					}
					sslContext.init(kmf.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());	
				}				
								
		        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
		        if(isDebug){
					logger.debug("Set SSL SocketFactory");
				}
		        mqQueueConnectionFactory.setSSLSocketFactory(sslSocketFactory);
			}
			if(sslCipherSuite.isPresent()){
				MQEnvironment.sslCipherSuite = sslCipherSuite.get();
				mqQueueConnectionFactory.setSSLCipherSuite(sslCipherSuite.get());
			}
			if(sslPeerName.isPresent()){
				MQEnvironment.sslPeerName = sslPeerName.get();
				mqQueueConnectionFactory.setSSLPeerName(sslPeerName.get());
			}			
			
		} catch (final Exception exception) {
			handleMqException(logger, sampleResult, exception, "Could not create MQQueueConnectionFactory: ");
			return sampleResult;
		}

		final Optional<String> username = parsedArgs.get(ConnectToQueueOption.Username.getName());
		final Optional<String> password = parsedArgs.get(ConnectToQueueOption.Password.getName());
		final QueueConnection queueConnection;
		try {
			if(username.isPresent() || password.isPresent()){
				if(isDebug){
					logger.debug("Creating queue connection with login/password");
				}
				queueConnection = mqQueueConnectionFactory.createQueueConnection(username.orNull(), password.orNull());
			} else {
				if(isDebug){
					logger.debug("Creating queue connection without login/password");
				}
				queueConnection = mqQueueConnectionFactory.createQueueConnection();
			}
		} catch (final JMSException jmsException) {
			handleMqException(logger, sampleResult, jmsException, "Could not create queue connection: ");
			return sampleResult;
		}

		if(isDebug){
			logger.debug("Creating queue session");
		}
		
		final QueueSession queueSession;
		try {
			queueSession = queueConnection.createQueueSession(isTransacted(parsedArgs), getAcknowledgeMode(parsedArgs));
		} catch (final JMSException jmsException) {
			handleMqException(logger, sampleResult, jmsException, "Could not create queue session: ");
			return sampleResult;
		}

		if(isDebug){
			logger.debug("Starting connection");
		}
		try {
			queueConnection.start();
		} catch (final JMSException jmsException) {
			handleMqException(logger, sampleResult, jmsException, "Could not start connection: ");
			return sampleResult;
		}

		if(isDebug){
			logger.debug("Creating queue");
		}
		final Queue queue;
		try {
			queue = queueSession.createQueue(queueName);
			if(queue instanceof MQQueue && isDebug){
				logger.debug("Target Client: "+((MQQueue)queue).getTargetClient());
			}
		} catch (final JMSException jmsException) {
			sampleResult.setError(true);
			sampleResult.setResponseContent(STATUS_CODE_ERROR_QUEUE_CONNECTION);
			sampleResult.setResponseContent("Could not create queue: " + jmsException.toString());
			logger.error("Could not create queue: ", jmsException);
			return sampleResult;
		}

		// Put Connection and Destination in the context of the VU, so it could be retrieved further to handle message
		if(isDebug){
			logger.debug("Putting JmsQueueContext in the context of the VU (name=" + queueName + ")");
		}
		context.getCurrentVirtualUser().put(queueName,
				getContext(logger, queueConnection, queue, isTransacted(parsedArgs), getAcknowledgeMode(parsedArgs)));		
		sampleResult.setResponseContent("Connection to queue " + queueName + " successful.");
		if(isDebug){
			logger.debug("Connection to queue " + queueName + " successful.");
		}
		return sampleResult;
	}

	private static void handleMqException(final Logger logger, final SampleResult sampleResult,
										  final Exception exception, final String errorMessage) {
		sampleResult.setError(true);
		sampleResult.setStatusCode(STATUS_CODE_ERROR_QUEUE_CONNECTION);
		sampleResult.setResponseContent(errorMessage + exception.toString());
		logger.error(errorMessage, exception);
		if(exception instanceof JMSException){
			final Exception linkedException = ((JMSException) exception).getLinkedException();
			if (linkedException != null) {
				logger.error(errorMessage + "(linked exception) ", linkedException);
			} else {
				logger.error(errorMessage + "(exception) ", exception);
			}
		} else {
			logger.error(errorMessage + "(exception) ", exception);
		}
	}

	private static int getAcknowledgeMode(Map<String, Optional<String>> parsedArgs) {
		if (parsedArgs.get(ConnectToQueueOption.QueueSessionAcknowledgeMode.getName()).isPresent()) {
			return Integer.parseInt(parsedArgs.get(ConnectToQueueOption.QueueSessionAcknowledgeMode.getName()).get());
		}
		return Session.AUTO_ACKNOWLEDGE;
	}

	private static boolean isTransacted(final Map<String, Optional<String>> parsedArgs) {
		return parsedArgs.get(ConnectToQueueOption.QueueSessionTransacted.getName()).isPresent()
				&& Boolean.parseBoolean(parsedArgs.get(ConnectToQueueOption.QueueSessionTransacted.getName()).get());
	}

	@Override
	public void stopExecute() {
		// Not implemented
	}

}
