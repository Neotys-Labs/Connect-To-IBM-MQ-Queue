package com.neotys.actions.jms.connecttomq;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.ActionParameter.Type;
import com.neotys.extensions.action.engine.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author srichert
 *
 */
public final class ConnectToMQQueueArguments {

	public enum OptionalRequired {
		Optional,
		Required
	}

	public enum AppearsByDefault {
		True,
		False
	}

	public enum ConnectToQueueOption {
		QueueManager("QueueManager", OptionalRequired.Required, AppearsByDefault.True, Type.TEXT,
				"",
				"Sets the name of the queue manager.",
				ConnectToMQQueueArgumentValidator.NON_EMPTY),

		HostName("HostName", OptionalRequired.Required, AppearsByDefault.True, Type.TEXT,
				"",
				"Sets the name of the host.",
				ConnectToMQQueueArgumentValidator.NON_EMPTY),

		Port("Port", OptionalRequired.Required, AppearsByDefault.True, Type.TEXT,
				"",
				"Sets the port for a client connection.",
				ConnectToMQQueueArgumentValidator.INTEGER_VALIDATOR),

		Channel("Channel", OptionalRequired.Required, AppearsByDefault.True, Type.TEXT,
				"",
				"Sets the name of the channel - applies to client transport mode only.",
				ConnectToMQQueueArgumentValidator.NON_EMPTY),

		QueueName("QueueName", OptionalRequired.Required, AppearsByDefault.True, Type.TEXT,
				"",
				"The name of the Queue.",
				ConnectToMQQueueArgumentValidator.NON_EMPTY),

		QueueSessionTransacted("QueueSessionTransacted", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"A boolean to indicate whether the session is transacted",
				ConnectToMQQueueArgumentValidator.BOOLEAN_VALIDATOR),

		QueueSessionAcknowledgeMode("QueueSessionAcknowledgeMode", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"An integer to indicate whether the consumer or the client will acknowledge any messages it receives; ignored if the session is transacted. Legal values are 1 for AUTO_ACKNOWLEDGE, 2 for CLIENT_ACKNOWLEDGE or 3 for DUPS_OK_ACKNOWLEDGE.",
				ConnectToMQQueueArgumentValidator.INTEGER_VALIDATOR),

		Username("Username", OptionalRequired.Optional, AppearsByDefault.False,  Type.TEXT,
				"",
				"The username used to create the connection.",
				ConnectToMQQueueArgumentValidator.ALWAYS_VALID),

		Password("Password", OptionalRequired.Optional, AppearsByDefault.False, Type.PASSWORD,
				"",
				"The password used to create the connection.",
				ConnectToMQQueueArgumentValidator.ALWAYS_VALID),
		
		CCSID("CCSID", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"Choosing client or server coded character set identifier.",
				ConnectToMQQueueArgumentValidator.INTEGER_VALIDATOR),
		
		TransportProperty("TransportProperty", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"Tansport property. For example 'MQSeries', 'MQSeries Client', 'MQSeries Bindings', or 'MQJD'.",
				ConnectToMQQueueArgumentValidator.NON_EMPTY),
		
		Debug("Debug", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"Debug: true/false",
				ConnectToMQQueueArgumentValidator.BOOLEAN_VALIDATOR),
		
		SslFipsRequired("SslFipsRequired", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"Specify that an SSL or TLS channel must use only FIPS-certified CipherSpecs. true/false",
				ConnectToMQQueueArgumentValidator.BOOLEAN_VALIDATOR), 
		
		UseIBMCipherMappings("UseIBMCipherMappings", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"UseIBMCipherMappingss. true/false",
				ConnectToMQQueueArgumentValidator.BOOLEAN_VALIDATOR), 
		
		PreferTLS("PreferTLS", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"PreferTLS. true/false",
				ConnectToMQQueueArgumentValidator.BOOLEAN_VALIDATOR), 
		
		SslCipherSuite("SslCipherSuite", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"SSL Cipher Suite",
				ConnectToMQQueueArgumentValidator.NON_EMPTY), 
		
		SslPeerName("SslPeerName", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"SSL Peer Name",
				ConnectToMQQueueArgumentValidator.NON_EMPTY),
		
		SslProtocol("SslProtocol", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"SSL protocol to use, e.g. TLSv1, TLSv1.2 or keep value empty for default SSL protocol.",
				ConnectToMQQueueArgumentValidator.NON_EMPTY),

		TrustStorePath("TrustStorePath", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"<path-to-truststore.jks>",
				"Location of the Java keystore file containing the collection of CA certificates trusted by this application process (trust store)",
				ConnectToMQQueueArgumentValidator.NON_EMPTY),

		TrustStorePassword("TrustStorePassword", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"Password to unlock the truststore file.",
				ConnectToMQQueueArgumentValidator.NON_EMPTY),

		KeyStorePath("KeyStorePath", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"<path-to-keystore.jks>",
						"Location of the Java keystore file containing an application process's own certificate and private key.",
					   ConnectToMQQueueArgumentValidator.NON_EMPTY),

		KeyStorePassword("KeyStorePassword", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
						"Password to unlock the keystore file.",
						   ConnectToMQQueueArgumentValidator.NON_EMPTY),

		QueueOperation("QueueOperation", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"CREATE",
				"CREATE: Create queue session then create queue / ACCESS: Access existing queue MQQueueManager.",
				ConnectToMQQueueArgumentValidator.NON_EMPTY),

		OpenOptions("OpenOptions", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"8240",
				"Integer for open option as specified here: https://docs.oracle.com/cd/E19509-01/820-4407/ggjyo/index.html. By default, MQC.MQOO_OUTPUT (16) | MQC.MQOO_INQUIRE (32) | MQC.MQOO_FAIL_IF_QUIESCING (8192) = 8240.",
				ConnectToMQQueueArgumentValidator.INTEGER_VALIDATOR),

		TLSInsecure("tls.insecure", OptionalRequired.Optional, AppearsByDefault.False, Type.TEXT,
				"",
				"By default set to false, it only accept TLS valid certificates. Set to true to ignore TLS certificates issues. Only applicable when parameter " + SslProtocol.getName() + " is set (default SSLContext always performs certificates checking).",
				ConnectToMQQueueArgumentValidator.BOOLEAN_VALIDATOR);

		private final String name;
		private final OptionalRequired optionalRequired;
		private final AppearsByDefault appearsByDefault;
		private final Type type;
		private final String defaultValue;
		private final String description;
		private final ConnectToMQQueueArgumentValidator argumentValidator;

		private ConnectToQueueOption(final String name, final OptionalRequired optionalRequired,
				final AppearsByDefault appearsByDefault,
				final Type type, final String defaultValue, final String description,
				final ConnectToMQQueueArgumentValidator argumentValidator) {
			this.name = name;
			this.optionalRequired = optionalRequired;
			this.appearsByDefault = appearsByDefault;
			this.type = type;
			this.defaultValue = defaultValue;
			this.description = description;
			this.argumentValidator = argumentValidator;

			PARAMETER_NAME_LOOKUP_MAP.put(name, this);
		}

		public String getName() {
			return name;
		}

		public OptionalRequired getOptionalRequired() {
			return optionalRequired;
		}

		public AppearsByDefault getAppearsByDefault() {
			return appearsByDefault;
		}

		public Type getType() {
			return type;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public String getDescription() {
			return description;
		}

		public ConnectToMQQueueArgumentValidator getArgumentValidator() {
			return argumentValidator;
		}
	}

	/** Key is the name and value is the enum. */
	static final Map<String, ConnectToQueueOption> PARAMETER_NAME_LOOKUP_MAP = new HashMap<>();

	/** Custom arguments from the user. */
	private final Map<String, String> additionalParameters = new HashMap<>();

	private final Map<String, Optional<String>> parsedArgs;

	/** Log various messages. */
	private final Logger logger;

	public ConnectToMQQueueArguments(final Logger logger, final List<ActionParameter> parameters) {
		this.logger = logger;
		this.parsedArgs = parseArguments(parameters);
	}

	/** @return the additionalparameters */
	public Map<String, String> getAdditionalparameters() {
		return ImmutableMap.copyOf(additionalParameters);
	}

	/**
	 * @param parameters
	 * @return
	 */
	Map<String, Optional<String>> parseArguments(final List<ActionParameter> parameters) {
		final Map<String, Optional<String>> args = new HashMap<>();
		for (final ActionParameter actionParameter : parameters) {
			args.put(actionParameter.getName(),
					Optional.fromNullable(actionParameter.getValue()));
		}
		validateParameters(args);

		// assure there are no null values in the args map.
		for (final ConnectToQueueOption option : ConnectToQueueOption.values()) {
			if (!args.containsKey(option.name)) {
				final Optional<String> optional = Optional.absent();
				args.put(option.name, optional);
			}
		}

		return args;
	}

	/**
	 * @param args
	 */
	private Map<String, Optional<String>> validateParameters(final Map<String, Optional<String>> args) {
		for (ConnectToQueueOption option : ConnectToQueueOption.values()) {
			validateOneParameter(args, option);
		}

		for (final Entry<String, Optional<String>> entry : args.entrySet()) {
			if (Strings.isNullOrEmpty(entry.getKey())) {
				throw new IllegalArgumentException("At least one argument name is empty. Please verify the parameters.");

			} else if (!PARAMETER_NAME_LOOKUP_MAP.containsKey(entry.getKey())) {
				additionalParameters.put(entry.getKey(), entry.getValue().orNull());
				logger.debug("Additional parameter added: " + entry.getKey());
			}
		}

		return args;
	}

	/**
	 * Validate one parameter
	 * @param args
	 * @param option
	 * @throws IllegalArgumentException if a required parameter is missing or null in args.
	 */
	private static void validateOneParameter(final Map<String, Optional<String>> args, final ConnectToQueueOption option) {
		if (OptionalRequired.Required.equals(option.optionalRequired)) {
			if (!args.containsKey(option.getName())) {
				throw new IllegalArgumentException("Missing argument \"" + option.name + ".\" "
						+ getArgumentDescription(1, option));

			} else if (!args.get(option.getName()).isPresent() || Strings.isNullOrEmpty(args.get(option.getName()).orNull())) {
				throw new IllegalArgumentException("Missing value for argument \"" + option.name + ".\" " +
						getArgumentDescription(1, option));
			}
		}

		if (args.containsKey(option.getName())) {
			final boolean isValid = option.argumentValidator.apply(args.get(option.getName()).orNull());
			if (!isValid) {
				throw new IllegalArgumentException("Invalid argument: " + option.getName() +
						". " + option.argumentValidator.getErrorMessage());
			}
		}
	}

	/**
	 * @return A description of the arguments.
	 */
	public static String getArgumentDescriptions() {
		StringBuilder sb = new StringBuilder();
		int longestArgName = 0;

		for (final ConnectToQueueOption option : ConnectToQueueOption.values()) {
			longestArgName = Math.max(longestArgName, option.name.length());
		}

		sb.append(String.format("%-" + longestArgName + "s", "Argument")).append(" : Description\r\n\r\n");

		for (final ConnectToQueueOption option : ConnectToQueueOption.values()) {
			sb.append(getArgumentDescription(longestArgName, option));
			sb.append("\r\n");
		}

		return sb.toString();
	}

	/**
	 * @param argumentNamePadding
	 * @param option
	 * @return a description of the argument
	 */
	private static String getArgumentDescription(int argumentNamePadding, final ConnectToQueueOption option) {
		final StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-" + Math.max(argumentNamePadding, 1) + "s", option.name)).append(" : ");

		if (OptionalRequired.Required.equals(option.optionalRequired)) {
			sb.append("(required)");
		} else {
			sb.append("(optional)");
		}

		if (!Strings.isNullOrEmpty(option.description)) {
			sb.append(" ").append(option.description);
		}

		if (!Strings.isNullOrEmpty(option.defaultValue)) {
			sb.append(" (Default value: ").append(option.defaultValue).append(")");
		}

		return sb.toString();
	}

	/** A list of all passed in arguments and settings.
	 * @param parsedArgs
	 * @return
	 */
	public static String getArgumentLogString(Map<String, Optional<String>> parsedArgs) {
		final StringBuilder sb = new StringBuilder();

		for (final ConnectToQueueOption option : ConnectToQueueOption.values()) {
			if (sb.length() > 0) {
				sb.append(", ");
			}

			sb.append(option.name).append(": ");
			if (Type.PASSWORD.equals(option.type)) {
				sb.append("XXX");

			} else if (parsedArgs.containsKey(option.name)) {
				sb.append(parsedArgs.get(option.name).orNull());

			} else {
				sb.append("(not present)");
			}
		}

		return sb.toString();
	}

	/** @return the parsedArgs */
	public Map<String, Optional<String>> getParsedArgs() {
		return parsedArgs;
	}
}
