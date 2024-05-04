package com.aspireapp.loan.dao;

import com.aspireapp.loan.entities.LoanPayment;
import com.aspireapp.loan.entities.LoanRepayment;
import com.aspireapp.loan.enums.RepaymentStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LoanRepaymentsDao extends CrudRepository<LoanRepayment, Long> {

    List<LoanRepayment> findByLoanId(Long id);

    List<LoanRepayment> findByLoanIdAndStatusOrderByRepaymentDateAsc(Long id, RepaymentStatus repaymentStatus);
}
