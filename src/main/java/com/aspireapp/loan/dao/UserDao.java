package com.aspireapp.loan.dao;

import com.aspireapp.loan.entities.Account;
import com.aspireapp.loan.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<User, Long> {

    User findByToken(String token);

}
