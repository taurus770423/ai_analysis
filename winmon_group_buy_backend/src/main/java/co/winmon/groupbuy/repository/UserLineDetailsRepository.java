package co.winmon.groupbuy.repository;

import co.winmon.groupbuy.entity.UserLineDetailsEntity;
import co.winmon.groupbuy.entity.key.UserLineDetailsKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLineDetailsRepository extends JpaRepository<UserLineDetailsEntity, UserLineDetailsKey> {
    @Query(value = """
        select e.* from user_line_details e where e.line_user_id = :lineUserId
    """, nativeQuery = true)
    Optional<UserLineDetailsEntity> findBylineUserId(String lineUserId);
}
