package org.bz.app.mspeople.exceptions;

import java.io.Serial;

public class PatternEmailException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -4753386595346116814L;

	public PatternEmailException(String message) {
		super("The Email does not meet the required pattern. "
				.concat(message));
	}


}
