package com.example.pagae_app.domain.payment;

import com.example.pagae_app.domain.expense.Expense;
import com.example.pagae_app.domain.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "payments")
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = true)
    private LocalDate date;

    public Payment() {}
    public Payment(PaymentRequestDTO data, Expense expense, User payer) {
        this.expense = expense;
        this.amount = data.amount();
        this.user = payer;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Expense getExpense() {
        return expense;
    }
    public void setExpense(Expense expense) {
        this.expense = expense;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}

}
