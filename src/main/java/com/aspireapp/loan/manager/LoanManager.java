package com.aspireapp.loan.manager;

import com.aspireapp.loan.dao.AccountDao;
import com.aspireapp.loan.dao.LoanDao;
import com.aspireapp.loan.dao.LoanRepaymentsDao;
import com.aspireapp.loan.entities.Account;
import com.aspireapp.loan.entities.Loan;
import com.aspireapp.loan.entities.LoanPayment;
import com.aspireapp.loan.entities.LoanRepayment;
import com.aspireapp.loan.enums.LoanStatus;
import com.aspireapp.loan.enums.PaymentStatus;
import com.aspireapp.loan.enums.RepaymentStatus;
import com.aspireapp.loan.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static java.lang.Math.min;

@Service
@Slf4j
public class LoanManager {

    @Autowired
    LoanDao loanDao;

    @Autowired
    LoanRepaymentsDao loanRepaymentsDao;

    @Autowired
    AccountDao accountDao;

    public void createRepaymentSchedule(Loan loan) {
        log.info("Creating repayment schedule for loan: {}", loan.getId());

        // check loan in approved state
        if (!loan.getStatus().equals(LoanStatus.APPROVED)) {
            log.error("Loan is not in approved state, cannot create repayment schedule");
            throw new RuntimeException("Loan is not in approved state, cannot create repayment schedule");
        }

        // check if repayment schedule already exists
        if (!loanRepaymentsDao.findByLoanId(loan.getId()).isEmpty()) {
            log.error("Repayment schedule already exists for loan: {}", loan.getId());
            throw new RuntimeException("Repayment schedule already exists for loan: " + loan.getId());
        }

        // create repayment schedule
        BigDecimal loanAmount = loan.getAmountRequired();
        int tenure = loan.getLoanTerm();

        BigDecimal emi = loanAmount.divide(BigDecimal.valueOf(tenure), 2, BigDecimal.ROUND_HALF_UP);

        for (int i = 1; i <= tenure; i++) {

            if (i == tenure) {
                emi = loanAmount.subtract(emi.multiply(BigDecimal.valueOf(tenure - 1))).setScale(2, BigDecimal.ROUND_HALF_UP); // to handle last EMI decimal value
            }

            Date repaymentDate = TimeUtil.getDatePlusXdays(loan.getCreatedAt(), i * 7);

            loanRepaymentsDao.save(new LoanRepayment(
                    loan.getId(),
                    emi,
                    repaymentDate,
                    null,
                    RepaymentStatus.PENDING,
                    BigDecimal.ZERO,
                    null)
            );
        }
    }

    public void updateUserAccountForNewLoan(Loan loan) {
        log.info("Updating user account for new loan: {}", loan.getId());

        // check loan in approved state
        if (!loan.getStatus().equals(LoanStatus.APPROVED)) {
            log.error("Loan is not in approved state, cannot update user account");
            throw new RuntimeException("Loan is not in approved state, cannot update user account");
        }

        // update user account
        Account account = accountDao.findById(loan.getAccountId()).get();

        int updated = accountDao.addNewLoanToAccount(account.getId(), loan.getAmountRequired());

        if (updated == 0) {
            log.error("Failed to update user account for new loan: {}", loan.getId());
            throw new RuntimeException("Failed to update user account for new loan: " + loan.getId());
        }

    }

    public void handleLoanPayment(LoanPayment payment, Loan loan) {
        log.info("Handling loan payment: {}", payment.getId());

        // check loan in approved state
        if (!loan.getStatus().equals(LoanStatus.APPROVED)) {
            log.error("Loan is not in approved state, cannot handle payment");
            throw new RuntimeException("Loan is not in approved state, cannot handle payment");
        }

        // check payment status
        if (!payment.getStatus().equals(PaymentStatus.SUCCESS)) {
            log.error("Payment status is not success, cannot handle payment");
            throw new RuntimeException("Payment status is not success, cannot handle payment");
        }

        Account account = accountDao.findById(loan.getAccountId()).get();

        BigDecimal amountToAdjustInPrincipal = payment.getAmount();

        Double interest = loan.getInterest().doubleValue();

        if (interest > 0) {
            // TODO: handle interest adjustment and payment breakups

            log.info("Interest to pay: {}", interest);

            Double interestToPay = min(amountToAdjustInPrincipal.doubleValue(), interest);

            amountToAdjustInPrincipal = amountToAdjustInPrincipal.subtract(BigDecimal.valueOf(interestToPay));

            account.setInterest(account.getInterest().subtract(BigDecimal.valueOf(interestToPay)));
            account.setPayableAmount(account.getPayableAmount().subtract(BigDecimal.valueOf(interestToPay)));
            account.setTotalAmountPaid(account.getTotalAmountPaid().add(BigDecimal.valueOf(interestToPay)));

            loan.setInterest(loan.getInterest().subtract(BigDecimal.valueOf(interestToPay)));
            loan.setPayableAmount(loan.getPayableAmount().subtract(BigDecimal.valueOf(interestToPay)));
            loan.setAmountPaid(loan.getAmountPaid().add(BigDecimal.valueOf(interestToPay)));

            log.info("Interest paid: {}", interestToPay);

            accountDao.save(account);
            loanDao.save(loan);

            if (amountToAdjustInPrincipal.equals(BigDecimal.ZERO)) {
                return;
            }
        }

        log.info("Amount adjusted in principal: {}", amountToAdjustInPrincipal);

        loan.setAmountPaid(loan.getAmountPaid().add(amountToAdjustInPrincipal));
        loan.setPayablePrincipal(loan.getPayablePrincipal().subtract(amountToAdjustInPrincipal));
        loan.setPayableAmount(loan.getPayableAmount().subtract(amountToAdjustInPrincipal));

        if (loan.getPayableAmount().equals(BigDecimal.ZERO)) {
            loan.setStatus(LoanStatus.PAID);
        }
        loanDao.save(loan);

        account.setLoanBalance(account.getLoanBalance().subtract(amountToAdjustInPrincipal));
        account.setTotalAmountPaid(account.getTotalAmountPaid().add(amountToAdjustInPrincipal));
        account.setPayableAmount(account.getPayableAmount().subtract(amountToAdjustInPrincipal));

        accountDao.save(account);

        List<LoanRepayment> pendingRepayments = loanRepaymentsDao.findByLoanIdAndStatusOrderByRepaymentDateAsc(loan.getId(), RepaymentStatus.PENDING);

        log.info("Pending repayments: {}", pendingRepayments.size());

        for (LoanRepayment repayment : pendingRepayments) {

            BigDecimal amountToPay = repayment.getRepaymentAmount().subtract(repayment.getAmountPaid());

            log.info("Amount to pay for repaymentId: {}: {}", repayment.getId(), amountToPay);

            if (amountToAdjustInPrincipal.compareTo(amountToPay) >= 0) {
                log.info("Amount to adjust in principal is greater than amount to pay");
                repayment.setStatus(RepaymentStatus.PAID);
                repayment.setPaymentDate(new Date());
                repayment.setAmountPaid(repayment.getRepaymentAmount());
                repayment = loanRepaymentsDao.save(repayment);
                amountToAdjustInPrincipal = amountToAdjustInPrincipal.subtract(amountToPay);
            } else {
                log.info("Amount to adjust in principal is less than amount to pay");
                repayment.setAmountPaid(repayment.getAmountPaid().add(amountToAdjustInPrincipal));
                repayment = loanRepaymentsDao.save(repayment);
                amountToAdjustInPrincipal = BigDecimal.ZERO;
                break;
            }
        }
    }
}
