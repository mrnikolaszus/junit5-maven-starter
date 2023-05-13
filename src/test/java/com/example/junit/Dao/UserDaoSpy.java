package com.example.junit.Dao;

import com.example.junit.dto.User;
import com.example.junit.service.Dao.UserDao;

import java.util.HashMap;
import java.util.Map;

public class UserDaoSpy extends UserDao {
    private UserDao userDao;
    private Map<Integer, Boolean> answers = new HashMap<>();

    public UserDaoSpy(UserDao userDao) {
        this.userDao = userDao;
    }
//    private Answer1<Integer, Boolean> answer1;

    @Override
    public boolean delete(Integer userId) {
        // invocation++;
        return answers.getOrDefault(userId, userDao.delete(userId));
    }
}
