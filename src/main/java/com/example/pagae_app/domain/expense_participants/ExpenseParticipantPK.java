package com.example.pagae_app.domain.expense_participants;

import com.example.pagae_app.domain.hangout_member.HangOutMemberPK;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class ExpenseParticipantPK {

    @Column(name = "user_id")
    Long userId;

    @Column(name = "expense_id")
    Long expenseId;

    public ExpenseParticipantPK() {}

    public ExpenseParticipantPK(Long userId, Long expenseId) {
        this.userId = userId;
        this.expenseId = expenseId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getExpenseId() {
        return expenseId;
    }
    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseParticipantPK that = (ExpenseParticipantPK) o;
        return Objects.equals(expenseId, that.expenseId) && Objects.equals(userId, that.userId);
    }
}
