package com.example.junit.service.Dao;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UserDao {
    @SneakyThrows
    public boolean delete(Integer userId){
        try (var connection = DriverManager.getConnection("url", "username", "password")) {
            return true;
        }
    }
}
