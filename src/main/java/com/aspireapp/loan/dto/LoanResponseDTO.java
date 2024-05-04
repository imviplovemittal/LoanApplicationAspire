package com.aspireapp.loan.dto;

import com.aspireapp.loan.entities.Loan;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@Data
public class LoanResponseDTO {
    Long loanId;
    Double amount;
    Integer tenure;
    String status;
    String purpose;
    Double amountPaid;
    Double interest;
    Double payablePrincipal;
    Double payableAmount;

    public static LoanResponseDTO fromEntity(Loan loan) {
        LoanResponseDTO loanResponseDTO = new LoanResponseDTO();
        loanResponseDTO.setLoanId(loan.getId());
        loanResponseDTO.setAmount(loan.getAmountRequired().doubleValue());
        loanResponseDTO.setTenure(loan.getLoanTerm());
        loanResponseDTO.setStatus(loan.getStatus().name());
        loanResponseDTO.setPurpose(loan.getPurpose());
        loanResponseDTO.setAmountPaid(loan.getAmountPaid().doubleValue());
        loanResponseDTO.setInterest(loan.getInterest().doubleValue());
        loanResponseDTO.setPayablePrincipal(loan.getPayablePrincipal().doubleValue());
        loanResponseDTO.setPayableAmount(loan.getPayableAmount().doubleValue());
        return loanResponseDTO;
    }
}
