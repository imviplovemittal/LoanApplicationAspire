package com.aspireapp.loan.dto;

import com.aspireapp.loan.entities.Account;
import com.aspireapp.loan.entities.Loan;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@Data
public class LoanAccountResponseDTO {

    private Long userId;
    private Double loanBalance;
    private Double totalAmountPaid;
    private Double totalLoanTaken;
    private String kycStatus;
    private String status;
    private String activationDate;
    private Double interest;
    private Double payableAmount;
    private String remarks;
    private List<LoanResponseDTO> loans;

    public static LoanAccountResponseDTO fromEntity(Account account, List<Loan> loans) {
        LoanAccountResponseDTO loanAccountResponseDTO = new LoanAccountResponseDTO();
        loanAccountResponseDTO.setUserId(account.getUserId());
        loanAccountResponseDTO.setLoanBalance(account.getLoanBalance().doubleValue());
        loanAccountResponseDTO.setTotalAmountPaid(account.getTotalAmountPaid().doubleValue());
        loanAccountResponseDTO.setTotalLoanTaken(account.getTotalLoanTaken().doubleValue());
        loanAccountResponseDTO.setKycStatus(account.getKycStatus().name());
        loanAccountResponseDTO.setStatus(account.getStatus().name());
        loanAccountResponseDTO.setActivationDate(account.getActivationDate().toString());
        loanAccountResponseDTO.setInterest(account.getInterest().doubleValue());
        loanAccountResponseDTO.setPayableAmount(account.getPayableAmount().doubleValue());
        loanAccountResponseDTO.setRemarks(account.getRemarks());
        loanAccountResponseDTO.setLoans(loans.stream().map(LoanResponseDTO::fromEntity).collect(Collectors.toList()));
        return loanAccountResponseDTO;
    }

}
