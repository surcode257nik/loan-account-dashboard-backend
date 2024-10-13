package com.code.LoanDashboard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "loan_accounts")
public class LoanAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Many LoanAccounts to One User
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "principal_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "total_payable", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPayable;

    @Column(name = "balance_principal", nullable = false, precision = 15, scale = 2)
    private BigDecimal balancePrincipal;

    @Column(name = "balance_interest", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceInterest;

    @Column(name = "start_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // One LoanAccount to Many RepaymentSchedules
    @JsonManagedReference
    @OneToMany(mappedBy = "loanAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RepaymentSchedule> repaymentSchedules;


    // One LoanAccount to Many EarlyRepayments
    @JsonManagedReference
    @OneToMany(mappedBy = "loanAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EarlyRepayment> earlyRepayments;

    // Getters and Setters

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public LoanAccount() {
    }

    public LoanAccount(int id, User user, BigDecimal principalAmount,
                       BigDecimal interestRate, BigDecimal totalPayable,
                       BigDecimal balancePrincipal, BigDecimal balanceInterest,
                       LocalDate startDate, LocalDate endDate, LocalDateTime createdAt,
                       LocalDateTime updatedAt, Set<RepaymentSchedule> repaymentSchedules,
                       Set<EarlyRepayment> earlyRepayments) {
        this.id = id;
        this.user = user;
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.totalPayable = totalPayable;
        this.balancePrincipal = balancePrincipal;
        this.balanceInterest = balanceInterest;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.repaymentSchedules = repaymentSchedules;
        this.earlyRepayments = earlyRepayments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getTotalPayable() {
        return totalPayable;
    }

    public void setTotalPayable(BigDecimal totalPayable) {
        this.totalPayable = totalPayable;
    }

    public BigDecimal getBalancePrincipal() {
        return balancePrincipal;
    }

    public void setBalancePrincipal(BigDecimal balancePrincipal) {
        this.balancePrincipal = balancePrincipal;
    }

    public BigDecimal getBalanceInterest() {
        return balanceInterest;
    }

    public void setBalanceInterest(BigDecimal balanceInterest) {
        this.balanceInterest = balanceInterest;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<RepaymentSchedule> getRepaymentSchedules() {
        return repaymentSchedules;
    }

    public void setRepaymentSchedules(Set<RepaymentSchedule> repaymentSchedules) {
        this.repaymentSchedules = repaymentSchedules;
    }

    public Set<EarlyRepayment> getEarlyRepayments() {
        return earlyRepayments;
    }

    public void setEarlyRepayments(Set<EarlyRepayment> earlyRepayments) {
        this.earlyRepayments = earlyRepayments;
    }

    @Override
    public String toString() {
        return "LoanAccount{" +
                "id=" + id +
                ", user=" + user +
                ", principalAmount=" + principalAmount +
                ", interestRate=" + interestRate +
                ", totalPayable=" + totalPayable +
                ", balancePrincipal=" + balancePrincipal +
                ", balanceInterest=" + balanceInterest +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", repaymentSchedules=" + repaymentSchedules +
                ", earlyRepayments=" + earlyRepayments +
                '}';
    }
}
