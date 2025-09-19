package domain.model.hangout_member;

import domain.model.hangout.HangOut;
import domain.model.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "hangout_members")
public class HangOutMember {

    @EmbeddedId
    private HangOutMemberPK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hangOutId")
    @JoinColumn(name = "hangout_id")
    private HangOut hangOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    public HangOutMemberPK getId() {
        return id;
    }

    public void setId(HangOutMemberPK id) {
        this.id = id;
    }

    public HangOut getHangOut() {
        return hangOut;
    }

    public void setHangOut(HangOut hangOut) {
        this.hangOut = hangOut;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
