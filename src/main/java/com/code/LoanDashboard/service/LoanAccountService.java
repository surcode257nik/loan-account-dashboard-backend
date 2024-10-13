package com.code.LoanDashboard.service;

import com.code.LoanDashboard.entity.EarlyRepayment;
import com.code.LoanDashboard.entity.LoanAccount;
import com.code.LoanDashboard.entity.RepaymentSchedule;
import com.code.LoanDashboard.entity.User;
import com.code.LoanDashboard.exception.ResourceNotFoundException;
import com.code.LoanDashboard.repository.EarlyRepaymentRepository;
import com.code.LoanDashboard.repository.LoanAccountRepository;
import com.code.LoanDashboard.repository.RepaymentScheduleRepository;
import com.code.LoanDashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LoanAccountService {

    private LoanAccountRepository loanAccountRepository;
    private RepaymentScheduleRepository repaymentScheduleRepository;
    private EarlyRepaymentRepository earlyRepaymentRepository;
    private UserRepository userRepository;

    @Transactional
    public LoanAccount createLoan(Integer userId, LoanAccount loan) {
        User user = userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException("user not found"));
        loan.setUser(user);
        return loanAccountRepository.save(loan);
    }

    @Autowired
    public LoanAccountService(LoanAccountRepository loanAccountRepository,
                              RepaymentScheduleRepository repaymentScheduleRepository,
                              EarlyRepaymentRepository earlyRepaymentRepository,
                              UserRepository userRepository) {
        this.loanAccountRepository = loanAccountRepository;
        this.repaymentScheduleRepository = repaymentScheduleRepository;
        this.earlyRepaymentRepository = earlyRepaymentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Fetch all loan accounts for a specific user.
     */

    public List<LoanAccount> getLoanAccountByUser(User user){
        return loanAccountRepository.findByUser(user);
    }

    /**
     * Fetch a specific loan account by ID.
     */
    public LoanAccount getLoanAccountById(Integer loanAccountId){
        return loanAccountRepository.findById(loanAccountId).orElseThrow(()->
            new ResourceNotFoundException("Loan Account not found with ID: " + loanAccountId));
    }

    /**
     * Fetch repayment schedules for a specific loan account.
     */

    public List<RepaymentSchedule> getRepaymentSchedule(Integer loanAccountId){
        LoanAccount loanAccount = loanAccountRepository.findById(loanAccountId)
                .orElseThrow(()-> new ResourceNotFoundException("Loan Account not found with ID: " + loanAccountId));
        return repaymentScheduleRepository.findByLoanAccountOrderByInstallmentNumberAsc(loanAccount);
    }

    /**
     * Fetch repayment schedules for a specific loan account.
     */

    public List<EarlyRepayment> getEarlyRepayments(Integer loanAccountId){
        return (List<EarlyRepayment>) earlyRepaymentRepository.findById(loanAccountId).orElseThrow(()-> new ResourceNotFoundException("Early payment has not been done yet!!"));
    }

    /**
     * Make an early repayment and recalculate the repayment schedule.
     */
    @Transactional
    public EarlyRepayment makeEarlyRepayment(Integer loanAccountId, BigDecimal amount){
        LoanAccount loanAccount = getLoanAccountById(loanAccountId);

        // Validate repayment amount
        if(amount.compareTo(loanAccount.getBalancePrincipal())>0){
            throw  new IllegalArgumentException("Repayment amount exceeds the outstanding principal.");
        }

        // Update loan account balances
        loanAccount.setBalancePrincipal(loanAccount.getBalancePrincipal().subtract(amount));
        loanAccount.setTotalPayable(loanAccount.getTotalPayable().subtract(amount));
        loanAccountRepository.save(loanAccount);

        // Record the early repayment
        EarlyRepayment earlyRepayment = new EarlyRepayment();
        earlyRepayment.setLoanAccount(loanAccount);
        earlyRepayment.setAmount(amount);
        earlyRepayment.setRepaymentDate(LocalDate.now());
        earlyRepaymentRepository.save(earlyRepayment);

        // Recalculate repayment schedules
        recalculateSchedules(loanAccount);
        return earlyRepayment;

    }

    private void recalculateSchedules(LoanAccount loanAccount) {

        List<RepaymentSchedule> schedules = repaymentScheduleRepository
                .findByLoanAccountOrderByInstallmentNumberAsc((loanAccount));

        BigDecimal remainingPrincipal = loanAccount.getBalancePrincipal();
        BigDecimal remainingInterest = loanAccount.getBalanceInterest();
        BigDecimal interestRate = loanAccount.getInterestRate().divide(new BigDecimal("100"));

        int totalInstallments = schedules.size();
        int installmentNumber = 1;

        for(RepaymentSchedule schedule : schedules){
            if(remainingPrincipal.compareTo(BigDecimal.ZERO)<=0){
                // Set all remaining installments to zero if principal is fully repaid
                schedule.setBalancePrincipal(BigDecimal.ZERO);
                schedule.setBalanceInterest(BigDecimal.ZERO);
                schedule.setInterest(BigDecimal.ZERO);
                schedule.setPrincipal(BigDecimal.ZERO);
                schedule.setTotal(BigDecimal.ZERO);
            }else{
                // Recalculate principal and interest for each installment
                BigDecimal principalPortion = remainingPrincipal
                        .divide(new BigDecimal(totalInstallments-installmentNumber+1),2,BigDecimal.ROUND_HALF_UP);

                BigDecimal interestPortion = remainingPrincipal
                        .multiply(interestRate).divide(new BigDecimal("12"), 2, BigDecimal.ROUND_HALF_UP); // Assuming monthly interest

                BigDecimal totalPortion = principalPortion.add(interestPortion);

                schedule.setPrincipal(principalPortion);
                schedule.setInterest(interestPortion);
                schedule.setTotal(totalPortion);
                schedule.setBalancePrincipal(remainingPrincipal.subtract(principalPortion));
                schedule.setBalanceInterest(remainingInterest.subtract(interestPortion));

                remainingPrincipal = remainingPrincipal.subtract(principalPortion);
                remainingInterest = remainingInterest.subtract(interestPortion);
            }
            repaymentScheduleRepository.save(schedule);
            installmentNumber++;

            }
        }
}
