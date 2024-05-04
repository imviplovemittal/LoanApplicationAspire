package com.aspireapp.loan.entities;

import com.aspireapp.loan.enums.RepaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "loan_repayments")
@AllArgsConstructor
@NoArgsConstructor
public class LoanRepayment extends BaseEntity {

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "repayment_amount")
    private BigDecimal repaymentAmount;

    @Column(name = "repayment_date")
    private Date repaymentDate;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RepaymentStatus status;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid;

    @Column(name = "payment_date")
    private Date paymentDate;

}
