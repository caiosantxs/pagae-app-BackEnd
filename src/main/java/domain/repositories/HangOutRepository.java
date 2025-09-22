package domain.repositories;

import domain.model.hangout.HangOut;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HangOutRepository extends JpaRepository<HangOut, Long> {
}
