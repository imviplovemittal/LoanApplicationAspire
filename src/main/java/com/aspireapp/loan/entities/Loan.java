package com.aspireapp.loan.entities;

import com.aspireapp.loan.enums.LoanStatus;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "loan")
public class Loan extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "amount_required")
    private BigDecimal amountRequired;

    @Column(name = "loan_term")
    private int loanTerm; // in weeks

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid;

    @Column(name = "interest")
    private BigDecimal interest;

    @Column(name = "payable_principal")
    private BigDecimal payablePrincipal;

    @Column(name = "payable_amount")
    private BigDecimal payableAmount;

    @Column(name = "remarks")
    private String remarks;


}
