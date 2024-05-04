package com.aspireapp.loan.dao;

import com.aspireapp.loan.entities.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Repository
public interface AccountDao extends CrudRepository<Account, Long> {

    Account findByUserId(Long id);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(nativeQuery = true, value = "update account set loan_balance = loan_balance + :amountRequired , total_loan_taken = total_loan_taken + :amountRequired, payable_amount = payable_amount + :amountRequired " +
            "where id = :id")
    int addNewLoanToAccount(Long id, BigDecimal amountRequired);
}
