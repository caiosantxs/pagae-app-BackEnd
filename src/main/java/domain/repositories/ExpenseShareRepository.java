package domain.repositories;

import domain.model.expense_shares.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, UUID> {
}
