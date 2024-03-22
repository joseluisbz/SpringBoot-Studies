package org.bz.app.mspeople.exceptions;

import java.io.Serial;

public class DefaultException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 3206918744494302928L;

	public DefaultException(String message) {
		super(message);
	}


}
