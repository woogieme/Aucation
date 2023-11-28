package com.example.aucation.common.error;

public class ExistsException extends ApplicationException {
	public ExistsException(ApplicationError error) {
		super(error);
	}
}
