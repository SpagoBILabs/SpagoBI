package it.eng.spagobi.twitter.analysis.exceptions;

public class TwitterGenericErrorException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -5182419145755437941L;

	public TwitterGenericErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public TwitterGenericErrorException(String message) {
		super(message);
	}

	public TwitterGenericErrorException(Throwable cause) {
		super("", cause);
	}

}
