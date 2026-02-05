package com.example.pagae_app.domain.expense;

import com.example.pagae_app.domain.expense_shares.ExpenseShare;
import com.example.pagae_app.domain.hangout.HangOut;
import com.example.pagae_app.domain.payment.Payment;
import com.example.pagae_app.domain.user.User;
import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "expenses")
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hangout_id", referencedColumnName = "id", nullable = false)
    private HangOut hangOut;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_creator_id", referencedColumnName = "id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "user_payer_id")
    private User payer;

    @Column(name = "date", nullable = true)
    private LocalDate date;

    public List<ExpenseShare> getShares() {
        return shares;
    }

    public void setShares(List<ExpenseShare> shares) {
        this.shares = shares;
    }

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseShare> shares = new ArrayList<>();

    public Expense() {}

    public Expense(ExpenseRequestDTO data, HangOut hangOut) {
        this.hangOut = hangOut;
        this.description = data.description();
        this.totalAmount = data.totalAmount();
        this.creator = creator;
    }

    public void addPayment(Payment payment) {
        this.payments.add(payment);
        payment.setExpense(this);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public HangOut getHangOut() {
        return hangOut;
    }
    public void setHangOut(HangOut hangOut) {
        this.hangOut = hangOut;
    }
    public List<Payment> getPayments() {
        return payments;
    }
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
    public User getCreator() {
        return creator;
    }
    public void setCreator(User creator) {
        this.creator = creator;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public User getPayer() {return payer;}
    public void setPayer(User payer) { this.payer = payer;}
}
