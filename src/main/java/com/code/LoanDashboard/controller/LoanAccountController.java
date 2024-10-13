package com.code.LoanDashboard.controller;

import com.code.LoanDashboard.entity.EarlyRepayment;
import com.code.LoanDashboard.entity.LoanAccount;
import com.code.LoanDashboard.entity.RepaymentSchedule;
import com.code.LoanDashboard.entity.User;
import com.code.LoanDashboard.service.LoanAccountService;
import com.code.LoanDashboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanAccountController {
    private LoanAccountService loanAccountService;
    private UserService userService;

    @Autowired
    public LoanAccountController(LoanAccountService loanAccountService,
                                 UserService userService) {
        this.loanAccountService = loanAccountService;
        this.userService = userService;
    }

    /**
     * Get all loan accounts for a specific user.
     * Example: GET /api/loans/user/1
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<List<LoanAccount>> getUserLoans(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        List<LoanAccount> loans = loanAccountService.getLoanAccountByUser(user);
        System.out.println("user: "+user.getUsername() + "\n passowrd:" +user.getPassword());
        System.out.println();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<LoanAccount> getLoanDetails(@PathVariable Integer loanId) {

        LoanAccount loan = loanAccountService.getLoanAccountById(loanId);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/{loanId}/schedule")
    public ResponseEntity<List<RepaymentSchedule>> getRepaymentSchedule(@PathVariable Integer loanId) {
        List<RepaymentSchedule> schedule = loanAccountService.getRepaymentSchedule(loanId);
        return ResponseEntity.ok(schedule);
    }


    @PostMapping("/{loanId}/repayments")
    public ResponseEntity<EarlyRepayment> makeRepayment(
            @PathVariable Integer loanId,
            @RequestParam BigDecimal amount) {
        EarlyRepayment repayment = loanAccountService.makeEarlyRepayment(loanId, amount);
        return ResponseEntity.ok(repayment);
    }
}
