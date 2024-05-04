package com.aspireapp.loan.dto;

import com.aspireapp.loan.entities.Loan;
import com.aspireapp.loan.entities.LoanRepayment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
@Data
public class LoanDetailDTO {
    private LoanResponseDTO loan;
    private List<LoanRepaymentResponseDTO> loanRepayments;

    public static LoanDetailDTO fromEntity(Loan loan, List<LoanRepayment> loanRepayments) {
        LoanDetailDTO loanDetailDTO = new LoanDetailDTO();
        loanDetailDTO.setLoan(LoanResponseDTO.fromEntity(loan));
        List<LoanRepaymentResponseDTO> loanRepaymentResponseDTOs = loanRepayments.stream()
                .map(LoanRepaymentResponseDTO::fromEntity)
                .collect(Collectors.toList());
        loanDetailDTO.setLoanRepayments(loanRepaymentResponseDTOs);
        return loanDetailDTO;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @Data
    public static class LoanRepaymentResponseDTO {
        private Long repaymentId;
        private Double amount;
        private Date repaymentDate;
        private String remarks;
        private String status;
        private Double amountPaid;
        private Date paymentDate;

        public static LoanRepaymentResponseDTO fromEntity(LoanRepayment loanRepayment) {
            LoanRepaymentResponseDTO loanRepaymentResponseDTO = new LoanRepaymentResponseDTO();
            loanRepaymentResponseDTO.setRepaymentId(loanRepayment.getId());
            loanRepaymentResponseDTO.setAmount(loanRepayment.getRepaymentAmount().doubleValue());
            loanRepaymentResponseDTO.setRepaymentDate(loanRepayment.getRepaymentDate());
            loanRepaymentResponseDTO.setRemarks(loanRepayment.getRemarks());
            loanRepaymentResponseDTO.setStatus(loanRepayment.getStatus().name());
            loanRepaymentResponseDTO.setAmountPaid(loanRepayment.getAmountPaid().doubleValue());
            loanRepaymentResponseDTO.setPaymentDate(loanRepayment.getPaymentDate());
            return loanRepaymentResponseDTO;
        }
    }
}
