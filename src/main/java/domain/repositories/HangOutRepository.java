package domain.repositories;

import domain.model.hangout.HangOut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HangOutRepository extends JpaRepository<HangOut, UUID> {
}
