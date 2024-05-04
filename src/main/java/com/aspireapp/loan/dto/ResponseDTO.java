package com.aspireapp.loan.dto;

import com.aspireapp.loan.constants.Constants;
import com.aspireapp.loan.constants.ResponseCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ResponseDTO<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2389655837393065314L;

	private Boolean success;
	private String statusCode;
	private String message = null;
	private Integer pageablePages;
	private T data;

	public ResponseDTO() {
		this.success = false;
		this.message = Constants.GENERIC_ERROR_MESSAGE;
	}

	public ResponseDTO(String responseCode, String message, boolean status, T data) {
		this.statusCode = responseCode;
		this.message = message;
		this.success = status;
		this.data = data;
	}
	
	public ResponseDTO(String responseCode, String message, boolean status, Integer pageablePages, T data) {
		this.statusCode = responseCode;
		this.message = message;
		this.success = status;
		this.data = data;
		this.pageablePages = pageablePages;
	}

	public static <T> ResponseDTO<T> success(T data) {
		return new ResponseDTO<T>(ResponseCode.SUCCESS_200, Constants.GENERIC_SUCCESS_MESSAGE, true, data);
	}

	public static ResponseDTO<PaymentGatewayURIResponse> error(String s) {
		return new ResponseDTO<PaymentGatewayURIResponse>(ResponseCode.BAD_REQUEST, s, false, null);
	}


	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Integer getPageablePages() {
		return pageablePages;
	}

	public void setPageablePages(Integer pageablePages) {
		this.pageablePages = pageablePages;
	}

	@Override
	public String toString() {
		return "ResponseDTO [success=" + success + ", statusCode=" + statusCode + ", message=" + message + ", data="
				+ data + "]";
	}

}