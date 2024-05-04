package com.aspireapp.loan.manager;

import com.aspireapp.loan.dao.AccountDao;
import com.aspireapp.loan.dao.LoanDao;
import com.aspireapp.loan.dao.LoanPaymentDao;
import com.aspireapp.loan.dto.PGCallback;
import com.aspireapp.loan.dto.PGRequestDTO;
import com.aspireapp.loan.dto.PaymentGatewayURIResponse;
import com.aspireapp.loan.dto.ResponseDTO;
import com.aspireapp.loan.entities.Account;
import com.aspireapp.loan.entities.Loan;
import com.aspireapp.loan.entities.LoanPayment;
import com.aspireapp.loan.entities.User;
import com.aspireapp.loan.enums.PaymentStatus;
import com.aspireapp.loan.handler.PGHandler;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class RepaymentManager {

    @Autowired
    private LoanManager loanManager;

    @Autowired
    private PGHandler pgHandler;

    @Autowired
    AccountDao accountDao;

    @Autowired
    LoanDao loanDao;

    @Autowired
    LoanPaymentDao loanPaymentDao;

    public ResponseDTO<PaymentGatewayURIResponse> initiatePaymentGateway(User user, PGRequestDTO request) {

        log.info("PG add money request : {}", request);

        Account account = accountDao.findByUserId(user.getId());

        if (account == null) {
            log.error("Account not found for user: {}", user.getId());
            return ResponseDTO.error("Account not found for user: " + user.getId());
        }

        Loan loan = loanDao.findById(request.getLoanId()).orElse(null);

        if (loan == null) {
            log.error("Loan not found for id: {}", request.getLoanId());
            return ResponseDTO.error("Loan not found for id: " + request.getLoanId());
        }

        if (!loan.getUserId().equals(user.getId())) {
            log.error("Loan does not belong to user: {}", user.getId());
            return ResponseDTO.error("Loan does not belong to user: " + user.getId());
        }

        Double loanAmountPending = loan.getPayableAmount().doubleValue();

        if (request.getAmount() > loanAmountPending) {
            log.error("Amount requested is greater than pending loan amount");
            return ResponseDTO.error("Amount requested is greater than pending loan amount");
        }

        String orderId = "order-" + System.currentTimeMillis();
        LoanPayment loanPayment = new LoanPayment();
        loanPayment.setLoanId(loan.getId());
        loanPayment.setAmount(BigDecimal.valueOf(request.getAmount()));
        loanPayment.setOrderId(orderId);
        loanPayment.setStatus(PaymentStatus.INIT);
        loanPayment.setAccountId(account.getId());
        loanPayment = loanPaymentDao.save(loanPayment);

        try {
            PaymentGatewayURIResponse response = pgHandler.initiatePaymentGateway(loanPayment, request.getRedirectURI());

            loanPayment.setStatus(PaymentStatus.PENDING);
            loanPaymentDao.save(loanPayment);

            mockPGCallbackSuccess(loanPayment);

            return ResponseDTO.success(response);
        } catch (Exception e) {
            log.error("Error initiating payment gateway for orderId: {}", orderId, e);
            return new ResponseDTO();
        }

    }

    private void mockPGCallbackSuccess(LoanPayment loanPayment) {

        PGCallback pgCallback = new PGCallback();
        pgCallback.setOrderId(loanPayment.getOrderId());
        pgCallback.setAmount(loanPayment.getAmount().doubleValue());
        pgCallback.setRrn(new RandomString(12).nextString());
        pgCallback.setStatus("SUCCESS");
        pgCallback.setPaymentMode("DC"); // Debit Card

        handleSuccessPaymentCallback(pgCallback);
    }

    private void handleSuccessPaymentCallback(PGCallback pgCallback) {

        log.info("Handling success payment callback for orderId: {}", pgCallback.getOrderId());

        LoanPayment payment = loanPaymentDao.findByOrderId(pgCallback.getOrderId());

        // make sure payment status is pending
        if (!payment.getStatus().equals(PaymentStatus.PENDING)) {
            log.error("Payment status is not pending for orderId: {}", pgCallback.getOrderId());
            return;
        }

        // check payment amount = callback amount
        if (payment.getAmount().doubleValue() != pgCallback.getAmount()) {
            log.error("Payment amount mismatch for orderId: {}", pgCallback.getOrderId());
            return;
        }

        payment.setBankRefNo(pgCallback.getRrn());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setSource(pgCallback.getPaymentMode());
        loanPaymentDao.save(payment);

        Loan loan = loanDao.findById(payment.getLoanId()).orElse(null);

        loanManager.handleLoanPayment(payment, loan);
    }
}
