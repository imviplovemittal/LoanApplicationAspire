package com.aspireapp.loan.manager;

import com.aspireapp.loan.LoanApplication;
import com.aspireapp.loan.dao.AccountDao;
import com.aspireapp.loan.dao.LoanDao;
import com.aspireapp.loan.dao.LoanPaymentDao;
import com.aspireapp.loan.dao.LoanRepaymentsDao;
import com.aspireapp.loan.dto.*;
import com.aspireapp.loan.entities.*;
import com.aspireapp.loan.enums.LoanAction;
import com.aspireapp.loan.enums.LoanStatus;
import com.aspireapp.loan.enums.PaymentStatus;
import com.aspireapp.loan.enums.UserRole;
import com.aspireapp.loan.handler.PGHandler;
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
public class RepaymentManagerTest {

    @InjectMocks
    private RepaymentManager repaymentManager;

    @Mock
    private AccountDao accountDao;

    @Mock
    private LoanDao loanDao;

    @Mock
    private LoanRepaymentsDao loanRepaymentsDao;

    @Mock
    private LoanManager loanManager;

    @Mock
    private TimeUtil timeUtil;

    @Mock
    private LoanPaymentDao loanPaymentDao;

    @Mock
    private PGHandler pgHandler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInitiatePaymentGatewayWithoutAnAccount() {
        User user = new User();
        user.setId(1L);

        PGRequestDTO pgPaymentRequest = new PGRequestDTO();
        pgPaymentRequest.setLoanId(1L);

        when(accountDao.findByUserId(user.getId())).thenReturn(null);

        ResponseDTO<?> response = repaymentManager.initiatePaymentGateway(user, pgPaymentRequest);

        assertEquals("Account not found for user: 1", response.getMessage());
    }

    @Test
    public void testInitiatePaymentGatewayWithoutALoan() {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        PGRequestDTO pgPaymentRequest = new PGRequestDTO();
        pgPaymentRequest.setLoanId(1L);

        when(accountDao.findByUserId(user.getId())).thenReturn(account);
        when(loanDao.findById(pgPaymentRequest.getLoanId())).thenReturn(Optional.empty());

        ResponseDTO<?> response = repaymentManager.initiatePaymentGateway(user, pgPaymentRequest);

        assertEquals("Loan not found for id: 1", response.getMessage());
    }

    @Test
    public void testInitiatePaymentGatewayWithLoanNotBelongingToUser() {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUserId(2L);

        PGRequestDTO pgPaymentRequest = new PGRequestDTO();
        pgPaymentRequest.setLoanId(1L);

        when(accountDao.findByUserId(user.getId())).thenReturn(account);
        when(loanDao.findById(pgPaymentRequest.getLoanId())).thenReturn(Optional.of(loan));

        ResponseDTO<?> response = repaymentManager.initiatePaymentGateway(user, pgPaymentRequest);

        assertEquals("Loan does not belong to user: 1", response.getMessage());
    }

    @Test
    public void testInitiatePaymentGatewayWithAmountGreaterThanPendingLoanAmount() {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUserId(1L);
        loan.setPayableAmount(BigDecimal.valueOf(1000));

        PGRequestDTO pgPaymentRequest = new PGRequestDTO();
        pgPaymentRequest.setLoanId(1L);
        pgPaymentRequest.setAmount(2000.0);

        when(accountDao.findByUserId(user.getId())).thenReturn(account);
        when(loanDao.findById(pgPaymentRequest.getLoanId())).thenReturn(Optional.of(loan));

        ResponseDTO<?> response = repaymentManager.initiatePaymentGateway(user, pgPaymentRequest);

        assertEquals("Amount requested is greater than pending loan amount", response.getMessage());
    }

    @Test
    public void testInitiatePaymentGateway() {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUserId(1L);
        loan.setPayableAmount(BigDecimal.valueOf(1000));

        PGRequestDTO pgPaymentRequest = new PGRequestDTO();
        pgPaymentRequest.setLoanId(1L);
        pgPaymentRequest.setAmount(500.0);
        pgPaymentRequest.setRedirectURI("http://localhost:8080/redirect");

        LoanPayment loanPayment = new LoanPayment();
        loanPayment.setLoanId(loan.getId());
        loanPayment.setAmount(BigDecimal.valueOf(pgPaymentRequest.getAmount()));
        loanPayment.setOrderId("order-" + System.currentTimeMillis());
        loanPayment.setStatus(PaymentStatus.PENDING);

        when(accountDao.findByUserId(user.getId())).thenReturn(account);
        when(loanDao.findById(pgPaymentRequest.getLoanId())).thenReturn(Optional.of(loan));
        when(loanPaymentDao.save(any(LoanPayment.class))).thenAnswer(i -> i.getArguments()[0]);
        when(loanPaymentDao.findByOrderId(anyString())).thenReturn(loanPayment);


        ResponseDTO<?> response = repaymentManager.initiatePaymentGateway(user, pgPaymentRequest);

        assertEquals("Data fetched successfully.", response.getMessage());
    }

    @Test
    public void testInitiatePaymentGatewayWithFailedPayment() {
        User user = new User();
        user.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setUserId(1L);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setUserId(1L);
        loan.setPayableAmount(BigDecimal.valueOf(1000));

        PGRequestDTO pgPaymentRequest = new PGRequestDTO();
        pgPaymentRequest.setLoanId(1L);
        pgPaymentRequest.setAmount(500.0);
        pgPaymentRequest.setRedirectURI("http://localhost:8080/redirect");

        when(accountDao.findByUserId(user.getId())).thenReturn(account);
        when(loanDao.findById(pgPaymentRequest.getLoanId())).thenReturn(Optional.of(loan));
        when(loanPaymentDao.save(any(LoanPayment.class))).thenAnswer(i -> i.getArguments()[0]);
        when(pgHandler.initiatePaymentGateway(any(LoanPayment.class), anyString())).thenThrow(new RuntimeException("Payment failed"));

        ResponseDTO<?> response = repaymentManager.initiatePaymentGateway(user, pgPaymentRequest);

        assertEquals(false, response.isSuccess());
    }
}
