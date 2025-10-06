package com.example.pagae_app.domain.hangout_member;

import com.example.pagae_app.domain.hangout.HangOut;
import com.example.pagae_app.domain.user.User;
import jakarta.persistence.*;

@Entity(name = "hangout_members")
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


    public HangOutMember() {
        this.id = new HangOutMemberPK();
    }

    public HangOutMember(HangOut hangOut, User user) {
        this.hangOut = hangOut;
        this.user = user;
        this.id = new HangOutMemberPK(user.getId(), hangOut.getId());
    }

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
