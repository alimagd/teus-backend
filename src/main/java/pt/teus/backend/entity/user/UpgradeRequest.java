package pt.teus.backend.entity.user;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class UpgradeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo user;

    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    private UpgradeStatus status;

    public UpgradeRequest(UserInfo user, BigDecimal amountPaid, UpgradeStatus status) {
        this.user = user;
        this.amountPaid = amountPaid;
        this.status = status;
    }

    public UpgradeRequest() {

    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public UserInfo getUser() {
        return user;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public UpgradeStatus getStatus() {
        return status;
    }

    public void setStatus(UpgradeStatus status) {
        this.status = status;
    }
}
