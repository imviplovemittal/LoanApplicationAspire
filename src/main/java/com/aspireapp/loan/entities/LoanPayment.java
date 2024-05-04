package com.aspireapp.loan.entities;


import com.aspireapp.loan.enums.PaymentStatus;
import com.aspireapp.loan.enums.RepaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "loan_payment")
@AllArgsConstructor
@NoArgsConstructor
public class LoanPayment extends BaseEntity {

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "bank_ref_no")
    private String bankRefNo;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "source")
    private String source;

}
