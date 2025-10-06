package com.example.pagae_app.domain.expense_participants;

import com.example.pagae_app.domain.expense.Expense;
import com.example.pagae_app.domain.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "expense_participants")
public class ExpenseParticipant {
    @EmbeddedId
    private ExpenseParticipantPK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("expenseId")
    @JoinColumn(name = "expense_id")
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    public ExpenseParticipant() {
        this.id = new ExpenseParticipantPK();
    }

    public ExpenseParticipant(Expense expense, User user) {
        this.expense = expense;
        this.user = user;
        this.id = new ExpenseParticipantPK(expense.getId(), user.getId());
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
}
