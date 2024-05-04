package com.aspireapp.loan.service;

import com.aspireapp.loan.constants.ResponseCode;
import com.aspireapp.loan.dao.AccountDao;
import com.aspireapp.loan.dao.LoanDao;
import com.aspireapp.loan.dao.LoanRepaymentsDao;
import com.aspireapp.loan.dto.*;
import com.aspireapp.loan.entities.Account;
import com.aspireapp.loan.entities.Loan;
import com.aspireapp.loan.entities.LoanRepayment;
import com.aspireapp.loan.entities.User;
import com.aspireapp.loan.enums.LoanAction;
import com.aspireapp.loan.enums.LoanStatus;
import com.aspireapp.loan.manager.LoanManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class LoanService {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private LoanDao loanDao;

    @Autowired
    private LoanManager loanManager;

    @Autowired
    private LoanRepaymentsDao loanRepaymentsDao;

    public ResponseEntity<?> requestLoan(User user, LoanRequest loanRequest) {

        Account account = accountDao.findByUserId(user.getId());

        if (Objects.isNull(account)) {
            log.error("Account not found for user: {}", user.getId());
            return ResponseEntity.badRequest().body(new ResponseDTO(
                    ResponseCode.BAD_REQUEST,
                    "Account not found for user: " + user.getId(),
                    false,
                    null
            ));
        }

        log.info("Account found for user: {}, account: {}", user.getId(), account.getId());

        // check amount > 0
        if (loanRequest.getLoanAmount() <= 0) {
            log.error("Invalid loan amount for user: {}, amount: {}", user.getId(), loanRequest.getLoanAmount());
            return ResponseEntity.badRequest().body(new ResponseDTO(
                    ResponseCode.BAD_REQUEST,
                    "Invalid loan amount",
                    false,
                    null
            ));
        }

        Loan loan = new Loan();
        loan.setAmountRequired(BigDecimal.valueOf(loanRequest.getLoanAmount()));
        loan.setLoanTerm(loanRequest.getTenure());
        loan.setPurpose(loanRequest.getReason());
        loan.setUserId(user.getId());
        loan.setAccountId(account.getId());
        loan.setStatus(LoanStatus.PENDING);

        loan = loanDao.save(loan);

        log.info("Loan request submitted successfully for user: {}, account: {}, loan: {}", user.getId(), account.getId(), loan.getId());

        return ResponseEntity.ok(new ResponseDTO(
                ResponseCode.SUCCESS,
                "Loan request submitted successfully",
                true,
                loan
        ));

    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    // to handle transactional operations, always create new transaction in db
    public ResponseEntity<?> actionLoan(User user, LoanActionRequest loanActionRequest) {

        Loan loan = loanDao.findById(loanActionRequest.getLoanId()).orElse(null);

        if (Objects.isNull(loan)) {
            log.error("Loan not found for id: {}", loanActionRequest.getLoanId());
            return ResponseEntity.badRequest().body(new ResponseDTO(
                    ResponseCode.BAD_REQUEST,
                    "Loan not found for id: " + loanActionRequest.getLoanId(),
                    false,
                    null
            ));
        }

        if (loan.getStatus() != LoanStatus.PENDING) {
            log.error("Loan status is not pending for id: {}", loanActionRequest.getLoanId());
            return ResponseEntity.badRequest().body(new ResponseDTO(
                    ResponseCode.BAD_REQUEST,
                    "Loan status is not pending for id: " + loanActionRequest.getLoanId(),
                    false,
                    null
            ));
        }


        if (loanActionRequest.getAction().equals(LoanAction.APPROVE)) {
            log.info("Approving loan for user: {}, loan: {}", user.getId(), loan.getId());

            loan.setStatus(LoanStatus.APPROVED);
            loan.setPayableAmount(loan.getAmountRequired());
            loan.setPayablePrincipal(loan.getAmountRequired());
            loan = loanDao.save(loan);

            log.info("Loan action performed successfully for user: {}, loan: {}", user.getId(), loan.getId());

            loanManager.updateUserAccountForNewLoan(loan);

            log.info("Creating loan repayment schedule for loan: {}", loan.getId());

            try {
                loanManager.createRepaymentSchedule(loan);
            } catch (Exception e) {
                log.error("Error creating repayment schedule for loan: {}", loan.getId(), e);
                return ResponseEntity.badRequest().body(new ResponseDTO(
                        ResponseCode.SOMETHING_WENT_WRONG,
                        "Error creating repayment schedule for loan: " + loan.getId(),
                        false,
                        null
                ));
            }
        } else {
            log.info("Rejecting loan for user: {}, loan: {}", user.getId(), loan.getId());

            loan.setStatus(LoanStatus.REJECTED);

            if (Objects.nonNull(loanActionRequest.getReason())) {
                loan.setRemarks(loanActionRequest.getReason());
            }

            loan = loanDao.save(loan);

            log.info("Loan action performed successfully for user: {}, loan: {}", user.getId(), loan.getId());
        }

        return ResponseEntity.ok(new ResponseDTO(
                ResponseCode.SUCCESS,
                "Loan action performed successfully",
                true,
                loan
        ));
    }

    public ResponseEntity<?> getLoans(User user) {

        Account account = accountDao.findByUserId(user.getId());

        if (Objects.isNull(account)) {
            log.error("Account not found for user: {}", user.getId());
            return ResponseEntity.badRequest().body(new ResponseDTO(
                    ResponseCode.BAD_REQUEST,
                    "Account not found for user: " + user.getId(),
                    false,
                    null
            ));
        }

        log.info("Account found for user: {}, account: {}", user.getId(), account.getId());

        List<Loan> loans = loanDao.findByAccountId(account.getId());

        log.info("Loans fetched successfully for user: {}, account: {}, size: {}", user.getId(), account.getId(), loans.size());


        LoanAccountResponseDTO loanAccountResponseDTO = LoanAccountResponseDTO.fromEntity(account, loans);

        return ResponseEntity.ok(new ResponseDTO(
                ResponseCode.SUCCESS,
                "Loans fetched successfully",
                true,
                loanAccountResponseDTO
        ));
    }

    public ResponseEntity<?> getLoan(User user, Long loanId) {

        Loan loan = loanDao.findById(loanId).orElse(null);

        if (Objects.isNull(loan)) {
            log.error("Loan not found for id: {}", loanId);
            return ResponseEntity.badRequest().body(new ResponseDTO(
                    ResponseCode.BAD_REQUEST,
                    "Loan not found for id: " + loanId,
                    false,
                    null
            ));
        }

        log.info("Loan found for id: {}", loanId);

        // check loan belongs to user

        if (!loan.getUserId().equals(user.getId())) {
            log.error("Loan does not belong to user: {}, loan: {}", user.getId(), loanId);
            return ResponseEntity.badRequest().body(new ResponseDTO(
                    ResponseCode.BAD_REQUEST,
                    "Loan does not belong to user: " + user.getId(),
                    false,
                    null
            ));
        }

        log.info("Loan fetched successfully for user: {}, loan: {}", user.getId(), loanId);

        List<LoanRepayment> loanRepayments = loanRepaymentsDao.findByLoanId(loanId);

        return ResponseEntity.ok(new ResponseDTO(
                ResponseCode.SUCCESS,
                "Loan fetched successfully",
                true,
                LoanDetailDTO.fromEntity(loan, loanRepayments)
        ));
    }
}
