package co.winmon.groupbuy.entity;

import co.winmon.groupbuy.entity.key.UserLineDetailsKey;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Setter
@Getter
@Builder
@Entity
@Table(name = "user_line_details")
@AllArgsConstructor
@NoArgsConstructor
public class UserLineDetailsEntity {
    @EmbeddedId
    private UserLineDetailsKey id;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "email")
    private String email;

    @CreatedDate
    @Column(name = "create_time")
    private Instant createTime;

    @LastModifiedDate
    @Column(name = "modify_time")
    private Instant modifyTime;
}