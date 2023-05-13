package com.example.junit.service;

import com.example.junit.dto.User;
import com.example.junit.service.Dao.UserDao;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;

public class UserService {
    private final List<User> users = new ArrayList<>();
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean delete(Integer userID){
//        Integer userIdLocal = 25;
        return userDao.delete(userID);
    }


    public List<User> getAll(){
        return users;

    }

    public void add(User... users){
        this.users.addAll(Arrays.asList(users));
    }

    public Optional<User> login(String username, String password) {
        if(username == null || password == null){
            throw new IllegalArgumentException("username or password is null");
        }
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();

    }

    public Map<Integer, User> getAllConvertedByID() {
        return users.stream()
                .collect(toMap(User::getId, identity()));
    }
}
