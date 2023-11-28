package com.example.aucation.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StreetResponse {

	private String city;
	private String zipcode;
	private String street;
}
