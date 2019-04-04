package com.neotys.actions.jms.connecttomq;

import java.net.URI;
import java.net.URL;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

/**
 * @author ajohnson
 *
 */
public enum ConnectToMQQueueArgumentValidator implements Predicate<String> {

	ALWAYS_VALID("Valid...") {
		@Override
		public boolean apply(final String input) {
			return true;
		}
	},
	NON_EMPTY("Must not be empty.") {
		@Override
		public boolean apply(final String input) {
			return !Strings.isNullOrEmpty(input);
		}
	},
	INTEGER_VALIDATOR("Invalid number.") {
		@Override
		public boolean apply(final String input) {
			try {
				Integer.valueOf(input);
			} catch (final Exception e) {
				return false;
			}
			return true;
		}
	},
	URI_VALIDATOR("Invalid URI.") {
		@Override
		public boolean apply(final String input) {
			try {
				URI.create(input);
			} catch (final Exception e) {
				return false;
			}
			return true;
		}
	},
	URL_VALIDATOR("Invalid URL.") {
		@Override
		public boolean apply(final String input) {
			try {
				new URL(input).toURI();
			} catch (final Exception e) {
				return false;
			}
			return true;
		}
	},
	BOOLEAN_VALIDATOR("Expected TRUE or FALSE.") {
		@Override
		public boolean apply(final String input) {
			return "true".equalsIgnoreCase(input) || "false".equalsIgnoreCase(input);
		}
	},
	PASS_OR_FAIL("Expected PASS or FAIL.") {
		@Override
		public boolean apply(final String input) {
			return "pass".equalsIgnoreCase(input) || "fail".equalsIgnoreCase(input);
		}
	};
	
	/** The error message associated with the validator. */
	private final String errorMessage;
	
	private ConnectToMQQueueArgumentValidator(final String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/** @return the errorMessage */
	public String getErrorMessage() {
		return errorMessage;
	}
}