package co.winmon.groupbuy.repository;

import co.winmon.groupbuy.entity.CustomerDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerDetailsRepository extends JpaRepository<CustomerDetailsEntity, String> {

    Optional<CustomerDetailsEntity> findByLineUserId(String lineUserId);

    Optional<CustomerDetailsEntity> findByEmail(String email);

    boolean existsByLineUserId(String lineUserId);
}