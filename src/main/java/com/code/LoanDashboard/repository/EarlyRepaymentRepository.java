package com.code.LoanDashboard.repository;

import com.code.LoanDashboard.entity.EarlyRepayment;
import com.code.LoanDashboard.entity.LoanAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EarlyRepaymentRepository extends JpaRepository<EarlyRepayment, Integer> {

    List<EarlyRepayment> findByLoanAccount(LoanAccount loanAccount);
}
