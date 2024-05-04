package com.aspireapp.loan.controller;

import com.aspireapp.loan.constants.ResponseCode;
import com.aspireapp.loan.dto.PGRequestDTO;
import com.aspireapp.loan.dto.PaymentGatewayURIResponse;
import com.aspireapp.loan.dto.ResponseDTO;
import com.aspireapp.loan.entities.User;
import com.aspireapp.loan.manager.RepaymentManager;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/api/payment/**")
public class PaymentController {

    @Autowired
    private RepaymentManager repaymentManager;

    private static final String RESPONSE_LOGGER = "Returning response : {}";

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "token_value")
    })
    @PostMapping("initiate")
    public ResponseDTO<PaymentGatewayURIResponse> initiateAddPaymentGateway(@RequestAttribute User user, HttpServletResponse response, @RequestBody PGRequestDTO request) {
        log.info("PG add money request : {}", request);
        if (request.getAmount() == null) {
            log.info("Invalid request params for request : {}", request);
            response.setStatus(Integer.parseInt(ResponseCode.BAD_REQUEST));
            return null;
        }
        ResponseDTO<PaymentGatewayURIResponse> responseBean = repaymentManager.initiatePaymentGateway(user, request);
        log.info(RESPONSE_LOGGER, responseBean);
        return responseBean;
    }

}
