package com.aspireapp.loan.handler;

import com.aspireapp.loan.dto.PaymentGatewayURIResponse;
import com.aspireapp.loan.entities.LoanPayment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PGHandler {


    // This method is mocked to call PG API and get the redirect URI
    public PaymentGatewayURIResponse initiatePaymentGateway(LoanPayment loanPayment, String redirectURI) {

        log.info("Initiating payment gateway for loan payment: {}", loanPayment);

        PaymentGatewayURIResponse paymentGatewayURIResponse = new PaymentGatewayURIResponse();
        paymentGatewayURIResponse.setRedirectURI(redirectURI);

        return paymentGatewayURIResponse;
    }
}
