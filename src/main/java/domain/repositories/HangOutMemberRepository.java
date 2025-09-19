package domain.repositories;

import domain.model.hangout_member.HangOutMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HangOutMemberRepository extends JpaRepository<HangOutMember, UUID> {
}
