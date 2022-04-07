package com.uhi.hsp.customexception;

import lombok.Data;

@Data
public class Error {
	private String type;
	private String code;
	private String path;
	private String message;

}
