package com.code.LoanDashboard.repository;

import com.code.LoanDashboard.entity.LoanAccount;
import com.code.LoanDashboard.entity.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, Integer> {

    List<RepaymentSchedule> findByLoanAccountOrderByInstallmentNumberAsc(LoanAccount loanAccount);
}
