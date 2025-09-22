package domain.repositories;

import domain.model.hangout_member.HangOutMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HangOutMemberRepository extends JpaRepository<HangOutMember, Long> {
}
