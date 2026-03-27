package com.mall.repository;

import com.mall.entity.Address;
import com.mall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);
    
    List<Address> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);
    
    @Query("SELECT a FROM Address a WHERE a.user = ?1 AND a.isDefault = true")
    Address findDefaultByUser(User user);
    
    long countByUserId(Long userId);
}
