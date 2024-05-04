package com.aspireapp.loan.dao;

import com.aspireapp.loan.entities.Loan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanDao extends CrudRepository<Loan, Long> {


    List<Loan> findByAccountId(Long id);
}
