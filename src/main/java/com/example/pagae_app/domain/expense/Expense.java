package com.example.pagae_app.domain.expense;

import com.example.pagae_app.domain.hangout.HangOut;
import com.example.pagae_app.domain.payment.Payment;
import jakarta.persistence.*;


import java.math.BigDecimal;
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
    private List<Payment> payments;

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
}
