package com.aspireapp.loan.controller;

import com.aspireapp.loan.dto.LoanActionRequest;
import com.aspireapp.loan.dto.LoanRequest;
import com.aspireapp.loan.entities.User;
import com.aspireapp.loan.enums.UserRole;
import com.aspireapp.loan.service.LoanService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.common.collect.ImmutableMap;

@Slf4j
@RestController
@RequestMapping("/api/loans/**")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "token_value")
    })
    @PostMapping("/request")
    public ResponseEntity<?> requestLoan(@RequestAttribute User user, @RequestBody LoanRequest loanRequest) {

        log.info("Request received for loan request for user: {}, request: {}", user.getId(), loanRequest);

        return loanService.requestLoan(user, loanRequest);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "token_value")
    })
    @PostMapping("/action")
    public ResponseEntity<?> actionLoan(@RequestAttribute User user, @RequestBody LoanActionRequest loanActionRequest) {

        if (user.getRole() != UserRole.ADMIN) {
            return ResponseEntity.badRequest().body(ImmutableMap.of("message", "Unauthorized access"));
        }

        log.info("Request received for loan action by admin: {}, request: {}", user.getId(), loanActionRequest);

        return loanService.actionLoan(user, loanActionRequest);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "token_value")
    })
    @GetMapping("/")
    public ResponseEntity<?> getLoans(@RequestAttribute User user) {
        log.info("Request received for fetching loans for user: {}", user.getId());
        return loanService.getLoans(user);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "token_value")
    })
    @GetMapping("/detail")
    public ResponseEntity<?> getLoan(@RequestAttribute User user, @RequestParam Long id) {
        log.info("Request received for fetching loan for user: {}, loanId: {}", user.getId(), id);
        return loanService.getLoan(user, id);
    }

}
