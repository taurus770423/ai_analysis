package co.winmon.groupbuy.repository;

import co.winmon.groupbuy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 透過 Line User ID 查找用戶
    Optional<User> findByLineUserId(String lineUserId);
}