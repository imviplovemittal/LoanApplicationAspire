package com.aspireapp.loan.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentGatewayURIResponse {
    private String redirectURI;
    private String paymentURI;

    public String getRedirectURI() {
        return redirectURI;
    }

    public void setRedirectURI(String redirectURI) {
        this.redirectURI = redirectURI;
    }

    public String getPaymentURI() {
        return paymentURI;
    }

    public void setPaymentURI(String paymentURI) {
        this.paymentURI = paymentURI;
    }

    @Override
    public String toString() {
        return "PaymentGatewayURIResponse{" +
                "redirectURI='" + redirectURI + '\'' +
                ", paymentURI='" + paymentURI + '\'' +
                '}';
    }
}
