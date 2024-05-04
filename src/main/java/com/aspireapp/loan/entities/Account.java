package com.aspireapp.loan.entities;

import com.aspireapp.loan.enums.AccountStatus;
import com.aspireapp.loan.enums.KycStatus;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "account")
public class Account extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "loan_balance")
    private BigDecimal loanBalance;

    @Column(name = "total_amount_paid")
    private BigDecimal totalAmountPaid;

    @Column(name = "total_loan_taken")
    private BigDecimal totalLoanTaken;

    @Column(name = "kyc_status")
    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "activation_date")
    private Date activationDate;

    @Column(name = "interest")
    private BigDecimal interest;

    @Column(name = "payable_amount")
    private BigDecimal payableAmount;

    @Column(name = "remarks")
    private String remarks;

}
