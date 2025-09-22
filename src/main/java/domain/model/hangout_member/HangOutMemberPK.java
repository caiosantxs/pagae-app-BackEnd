package domain.model.hangout_member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class HangOutMemberPK implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "hangout_id")
    private Long hangOutId;

    public HangOutMemberPK() {}

    public HangOutMemberPK(Long userId, Long hangOutId) {
        this.userId = userId;
        this.hangOutId = hangOutId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getHangOutId() {
        return hangOutId;
    }

    public void setHangOutId(Long hangOutId) {
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
