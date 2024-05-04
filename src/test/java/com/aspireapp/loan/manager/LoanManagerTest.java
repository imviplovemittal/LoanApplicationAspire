package com.aspireapp.loan.manager;

import com.aspireapp.loan.LoanApplication;
import com.aspireapp.loan.dao.AccountDao;
import com.aspireapp.loan.dao.LoanDao;
import com.aspireapp.loan.dao.LoanRepaymentsDao;
import com.aspireapp.loan.dto.LoanActionRequest;
import com.aspireapp.loan.dto.LoanRequest;
import com.aspireapp.loan.entities.*;
import com.aspireapp.loan.enums.LoanAction;
import com.aspireapp.loan.enums.LoanStatus;
import com.aspireapp.loan.enums.UserRole;
import com.aspireapp.loan.manager.LoanManager;
import com.aspireapp.loan.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {LoanApplication.class})
@RunWith(SpringRunner.class)
@Slf4j
@ActiveProfiles("test")
public class LoanManagerTest {

    @InjectMocks
    private LoanManager loanManager;

    @Mock
    private AccountDao accountDao;

    @Mock
    private LoanDao loanDao;

    @Mock
    private LoanRepaymentsDao loanRepaymentsDao;

    @Mock
    private TimeUtil timeUtil;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testNumberOfRepamentSchedule() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmountRequired(BigDecimal.valueOf(1000));
        loan.setLoanTerm(12);
        loan.setStatus(LoanStatus.APPROVED);
        loan.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));

        when(loanRepaymentsDao.findByLoanId(loan.getId())).thenReturn(Collections.emptyList());

        loanManager.createRepaymentSchedule(loan);

        verify(loanRepaymentsDao, times(12)).save(any());
    }

    @Test
    public void testNumberOfRepamentScheduleWithExistingSchedule() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmountRequired(BigDecimal.valueOf(1000));
        loan.setLoanTerm(12);
        loan.setStatus(LoanStatus.APPROVED);
        loan.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));

        when(loanRepaymentsDao.findByLoanId(loan.getId())).thenReturn(Collections.singletonList(new LoanRepayment()));

        try {
            loanManager.createRepaymentSchedule(loan);
        } catch (RuntimeException e) {
            assertEquals("Repayment schedule already exists for loan: " + loan.getId(), e.getMessage());
        }
    }

    @Test
    public void testNumberOfRepamentScheduleWithPendingLoan() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmountRequired(BigDecimal.valueOf(1000));
        loan.setLoanTerm(12);
        loan.setStatus(LoanStatus.PENDING);
        loan.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));

        try {
            loanManager.createRepaymentSchedule(loan);
        } catch (RuntimeException e) {
            assertEquals("Loan is not in approved state, cannot create repayment schedule", e.getMessage());
        }
    }

    @Test
    public void testEqualDivisionOfRepaymentsWhenNoAdjustmentRequired() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmountRequired(BigDecimal.valueOf(1200));
        loan.setLoanTerm(12);
        loan.setStatus(LoanStatus.APPROVED);
        loan.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));

        when(loanRepaymentsDao.findByLoanId(loan.getId())).thenReturn(Collections.emptyList());

        loanManager.createRepaymentSchedule(loan);

        verify(loanRepaymentsDao, times(12)).save(
                argThat(x -> (((LoanRepayment) x).getRepaymentAmount().doubleValue() == 100.0)
                )
        );
    }

    @Test
    public void testSumOfRepaymentsWhenAdjustmentRequired() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmountRequired(BigDecimal.valueOf(1201));
        loan.setLoanTerm(12);
        loan.setStatus(LoanStatus.APPROVED);
        loan.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));

        when(loanRepaymentsDao.findByLoanId(loan.getId())).thenReturn(Collections.emptyList());

        loanManager.createRepaymentSchedule(loan);

        verify(loanRepaymentsDao, times(11)).save(
                argThat(x -> (((LoanRepayment) x).getRepaymentAmount().doubleValue() == 100.08))
        );
    }

    @Test
    public void testUpdateUserAccountForNewLoan() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmountRequired(BigDecimal.valueOf(1000));
        loan.setStatus(LoanStatus.APPROVED);
        loan.setAccountId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        when(accountDao.findById(loan.getAccountId())).thenReturn(Optional.of(account));
        when(accountDao.addNewLoanToAccount(account.getId(), loan.getAmountRequired())).thenReturn(1);

        loanManager.updateUserAccountForNewLoan(loan);

        verify(accountDao, times(1)).addNewLoanToAccount(account.getId(), loan.getAmountRequired());
    }

    @Test
    public void testUpdateUserAccountForNewLoanWithPendingLoan() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmountRequired(BigDecimal.valueOf(1000));
        loan.setStatus(LoanStatus.PENDING);
        loan.setAccountId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        when(accountDao.findById(loan.getAccountId())).thenReturn(Optional.of(account));

        try {
            loanManager.updateUserAccountForNewLoan(loan);
        } catch (RuntimeException e) {
            assertEquals("Loan is not in approved state, cannot update user account", e.getMessage());
        }
    }

    @Test
    public void testUpdateUserAccountForNewLoanWithFailedUpdate() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmountRequired(BigDecimal.valueOf(1000));
        loan.setStatus(LoanStatus.APPROVED);
        loan.setAccountId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        when(accountDao.findById(loan.getAccountId())).thenReturn(Optional.of(account));
        when(accountDao.addNewLoanToAccount(account.getId(), loan.getAmountRequired())).thenReturn(0);

        try {
            loanManager.updateUserAccountForNewLoan(loan);
        } catch (RuntimeException e) {
            assertEquals("Failed to update user account for new loan: " + loan.getId(), e.getMessage());
        }
    }

}
