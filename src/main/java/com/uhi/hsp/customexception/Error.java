package com.uhi.hsp.customexception;

import lombok.Data;

@Data
public class Error {
	private String type;
	private int code;
	private String path;
	private String message;

}
