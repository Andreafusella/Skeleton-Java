package com.app.test.repository;

import org.springframework.stereotype.Repository;

import com.app.test.model.user.User;


@Repository
public interface UserRepository extends BaseRepository<User, Integer> {
}
