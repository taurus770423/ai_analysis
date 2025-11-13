package co.winmon.groupbuy.repository;

import co.winmon.groupbuy.entity.UserDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetailsEntity, String> {
    @Query(value = """
        select c.* from user_details c
        join user_line_details cl on cl.user_id = c.id
        where cl.line_user_id = :lineUserId
    """, nativeQuery = true)
    Optional<UserDetailsEntity> findByLineUserId(String lineUserId);
}