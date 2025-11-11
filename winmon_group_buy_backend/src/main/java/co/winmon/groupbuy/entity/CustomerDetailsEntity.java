package co.winmon.groupbuy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Setter
@Getter
@Builder
@Entity
@Table(name = "customer_details")
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailsEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "line_user_id", nullable = false, unique = true)
    private String lineUserId;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "email")
    private String email;

    @Builder.Default
    @Column(name = "create_time")
    private Instant createTime = Instant.now();

    @Column(name = "last_login_time")
    private Instant lastLoginTime;
}