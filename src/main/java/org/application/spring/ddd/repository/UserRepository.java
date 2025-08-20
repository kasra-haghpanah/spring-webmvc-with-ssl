package org.application.spring.ddd.repository;

import org.application.spring.ddd.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User,String> {

    @Query("SELECT u FROM User u WHERE u.userName = :userName")
    User findByUserName(@Param("userName") String userName);


    @Modifying
    @Transactional("appTM")
    @Query("UPDATE User u SET u.activationCode = null WHERE u.userName = :email")
    int updateUserForActivationCode(@Param("email") String email);

}
