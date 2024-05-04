package com.aspireapp.loan.dao;

import com.aspireapp.loan.entities.Account;
import com.aspireapp.loan.entities.LoanPayment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanPaymentDao extends CrudRepository<LoanPayment, Long> {

    LoanPayment findByOrderId(String orderId);
}
