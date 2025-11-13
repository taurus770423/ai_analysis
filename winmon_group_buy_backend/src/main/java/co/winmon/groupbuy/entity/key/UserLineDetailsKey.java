package co.winmon.groupbuy.entity.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Builder
@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserLineDetailsKey implements Serializable {
    @Column(name = "user_id")
    private String userId;

    @Column(name = "line_user_id")
    private String lineUserId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLineDetailsKey that = (UserLineDetailsKey) o;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.lineUserId, that.lineUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.lineUserId);
    }
}