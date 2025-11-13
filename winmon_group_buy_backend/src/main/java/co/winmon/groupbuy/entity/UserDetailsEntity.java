package co.winmon.groupbuy.entity;

import co.winmon.groupbuy.enums.UserStatusEnums;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Setter
@Getter
@Builder
@Entity
@Table(name = "user_details")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserDetailsEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatusEnums status = UserStatusEnums.NORMAL;

    @CreatedDate
    @Column(name = "create_time")
    private Instant createTime;

    @LastModifiedDate
    @Column(name = "modify_time")
    private Instant modifyTime;

    @Builder.Default
    @Column(name = "last_login_time")
    private Instant lastLoginTime = Instant.now();
}