package com.aspireapp.loan.service;

import com.aspireapp.loan.LoanApplication;
import com.aspireapp.loan.dao.AccountDao;
import com.aspireapp.loan.dao.LoanDao;
import com.aspireapp.loan.dto.LoanActionRequest;
import com.aspireapp.loan.dto.LoanRequest;
import com.aspireapp.loan.entities.Account;
import com.aspireapp.loan.entities.Loan;
import com.aspireapp.loan.entities.User;
import com.aspireapp.loan.enums.LoanAction;
import com.aspireapp.loan.enums.LoanStatus;
import com.aspireapp.loan.enums.UserRole;
import com.aspireapp.loan.manager.LoanManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {LoanApplication.class})
@RunWith(SpringRunner.class)
@Slf4j
@ActiveProfiles("test")
public class LoanServiceTest {

    @InjectMocks
    private LoanService loanService;

    @Mock
    private AccountDao accountDao;

    @Mock
    private LoanDao loanDao;

    @Mock
    private LoanManager loanManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRequestLoan() {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(1000.0);
        loanRequest.setTenure(12);
        loanRequest.setReason("Test");

        when(accountDao.findByUserId(any(Long.class))).thenReturn(account);
        when(loanDao.save(any(Loan.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = loanService.requestLoan(user, loanRequest);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testRequestLoanAccountNotFound() {
        User user = new User();
        user.setId(1L);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(1000.0);
        loanRequest.setTenure(12);
        loanRequest.setReason("Test");

        when(accountDao.findByUserId(any(Long.class))).thenReturn(null);

        ResponseEntity<?> response = loanService.requestLoan(user, loanRequest);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testRequestLoanAccountLoanAmountZero() {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(0.0);
        loanRequest.setTenure(12);
        loanRequest.setReason("Test");

        when(accountDao.findByUserId(any(Long.class))).thenReturn(account);

        ResponseEntity<?> response = loanService.requestLoan(user, loanRequest);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testRequestLoanAccountLoanAmountNegative() {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(-1000.0);
        loanRequest.setTenure(12);
        loanRequest.setReason("Test");

        when(accountDao.findByUserId(any(Long.class))).thenReturn(account);

        ResponseEntity<?> response = loanService.requestLoan(user, loanRequest);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testRequestLoanAccountLoanAmountPositive() {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(1000.0);
        loanRequest.setTenure(12);
        loanRequest.setReason("Test");

        when(accountDao.findByUserId(any(Long.class))).thenReturn(account);
        when(loanDao.save(any(Loan.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = loanService.requestLoan(user, loanRequest);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testRequestLoanAccountLoanAmountPositiveLoanStatusPending() {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(1000.0);
        loanRequest.setTenure(12);
        loanRequest.setReason("Test");

        when(accountDao.findByUserId(any(Long.class))).thenReturn(account);
        when(loanDao.save(any(Loan.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = loanService.requestLoan(user, loanRequest);

        // check when loanDao.save is getting called, status is PENDING
        verify(loanDao, times(1)).save(
                argThat(x -> x.getStatus() == LoanStatus.PENDING)
        );

        assertEquals(200, response.getStatusCodeValue());
    }

    // loan action tests

    @Test
    public void testActionLoanUserNotAdmin() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);

        LoanActionRequest loanActionRequest = new LoanActionRequest();
        loanActionRequest.setLoanId(1L);
        loanActionRequest.setAction(LoanAction.APPROVE);

        ResponseEntity<?> response = loanService.actionLoan(user, loanActionRequest);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testActionLoanLoanNotFound() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.ADMIN);

        LoanActionRequest loanActionRequest = new LoanActionRequest();
        loanActionRequest.setLoanId(1L);
        loanActionRequest.setAction(LoanAction.APPROVE);

        when(loanDao.findById(any(Long.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = loanService.actionLoan(user, loanActionRequest);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testActionLoanLoanFound() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.ADMIN);

        LoanActionRequest loanActionRequest = new LoanActionRequest();
        loanActionRequest.setLoanId(1L);
        loanActionRequest.setAction(LoanAction.APPROVE);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus(LoanStatus.PENDING);

        when(loanDao.findById(any(Long.class))).thenReturn(Optional.of(loan));
        when(loanDao.save(any(Loan.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = loanService.actionLoan(user, loanActionRequest);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testActionLoanLoanFoundLoanStatusNotPending() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.ADMIN);

        LoanActionRequest loanActionRequest = new LoanActionRequest();
        loanActionRequest.setLoanId(1L);
        loanActionRequest.setAction(LoanAction.APPROVE);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus(LoanStatus.APPROVED);

        when(loanDao.findById(any(Long.class))).thenReturn(Optional.of(loan));

        ResponseEntity<?> response = loanService.actionLoan(user, loanActionRequest);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testActionLoanLoanFoundLoanStatusPending() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.ADMIN);

        LoanActionRequest loanActionRequest = new LoanActionRequest();
        loanActionRequest.setLoanId(1L);
        loanActionRequest.setAction(LoanAction.APPROVE);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus(LoanStatus.PENDING);

        when(loanDao.findById(any(Long.class))).thenReturn(Optional.of(loan));
        when(loanDao.save(any(Loan.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = loanService.actionLoan(user, loanActionRequest);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testActionLoanReject() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.ADMIN);

        LoanActionRequest loanActionRequest = new LoanActionRequest();
        loanActionRequest.setLoanId(1L);
        loanActionRequest.setAction(LoanAction.REJECT);
        loanActionRequest.setReason("Test");

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus(LoanStatus.PENDING);

        when(loanDao.findById(any(Long.class))).thenReturn(Optional.of(loan));
        when(loanDao.save(any(Loan.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = loanService.actionLoan(user, loanActionRequest);

        // check when loanDao.save is getting called, status is REJECTED and remarks is set
        verify(loanDao, times(1)).save(
                argThat(x -> x.getStatus() == LoanStatus.REJECTED && x.getRemarks().equals("Test"))
        );

        assertEquals(200, response.getStatusCodeValue());
    }

}
