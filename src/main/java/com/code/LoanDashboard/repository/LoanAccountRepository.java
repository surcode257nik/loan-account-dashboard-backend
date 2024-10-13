package com.code.LoanDashboard.repository;

import com.code.LoanDashboard.entity.LoanAccount;
import com.code.LoanDashboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanAccountRepository extends JpaRepository<LoanAccount, Integer> {
    List<LoanAccount> findByUser(User user);
}
