package com.example.pagae_app.domain.hangout;

import com.example.pagae_app.domain.expense.Expense;
import com.example.pagae_app.domain.hangout_member.HangOutMember;
import com.example.pagae_app.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "hangouts")
@Table(name = "hangouts")
public class HangOut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "id", nullable = false)
    private User creator;

    @Column(name = "creation_date", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDate creationDate;

    @Enumerated(EnumType.STRING) // 1. Garante que o Java trate como Texto
    @Column(name = "status", nullable = false, columnDefinition = "varchar(255) DEFAULT 'ATIVO'") // 2. Força o SQL exato
    private StatusHangOut status = StatusHangOut.ATIVO;

    @OneToMany(mappedBy = "hangOut", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "hangOut", fetch = FetchType.LAZY)
    private List<HangOutMember> members = new ArrayList<>();

    public HangOut(HangOutRequestDTO data, User creator) {
        this.title = data.title();
        this.description = data.description();
        this.creator = creator;
        this.creationDate = LocalDate.now();
        this.status = StatusHangOut.ATIVO;
    }

    public HangOut() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public User getCreator() {
        return creator;
    }
    public void setCreator(User creator) {
        this.creator = creator;
    }
    public LocalDate getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
    public List<Expense> getExpenses() {
        return expenses;
    }
    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
    public StatusHangOut getStatus() {
        return status;
    }

    public void setStatus(StatusHangOut status) {
        this.status = status;
    }

    public List<HangOutMember> getMembers() {
        return members;
    }

}
