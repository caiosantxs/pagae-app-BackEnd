package domain.model.hangout_member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class HangOutMemberPK implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "hangout_id")
    private UUID hangOutId;

    public HangOutMemberPK() {}

    public HangOutMemberPK(UUID userId, UUID hangOutId) {
        this.userId = userId;
        this.hangOutId = hangOutId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getHangOutId() {
        return hangOutId;
    }

    public void setHangOutId(UUID hangOutId) {
        this.hangOutId = hangOutId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HangOutMemberPK that = (HangOutMemberPK) o;
        return Objects.equals(hangOutId, that.hangOutId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hangOutId, userId);
    }
}
