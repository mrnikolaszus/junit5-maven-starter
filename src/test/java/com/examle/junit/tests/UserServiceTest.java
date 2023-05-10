package com.examle.junit.tests;

import com.example.junit.dto.User;
import com.example.junit.service.UserService;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public
class UserServiceTest {
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    private static final User MASHA = User.of(3, "Masha", "222");
    private UserService userService;

    @BeforeAll
    static void  init(){
        System.out.println("Before all: " );
    }

    @BeforeEach
    void prepare(){
        System.out.println("Before each: " + this);
        userService = new UserService();
    }
    @Test
    void userEmptyIfNoUserAdded(){
        System.out.println("Test 1: " + this);
         var users = userService.getAll();

        assertTrue(users.isEmpty(), ()-> "User list should be Empty"  );
        // input -> [box = func] -> actual output
    }
    @Test
    void usersSizeIfUserAdded(){
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);
        userService.add(MASHA);


        var users = userService.getAll();

        assertThat(users).hasSize(3);

//        assertEquals(3, users.size());

    }

    @Test
    void loginSuccessIfUserExists(){
        userService.add(IVAN);
        Optional<User> mayBeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

        assertThat(mayBeUser).isPresent();
        mayBeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));


//        assertTrue(mayBeUser.isPresent());
//         mayBeUser.ifPresent(user -> assertEquals(IVAN, user));
    }

    @Test
    void usersConvertedToMapByID(){
        userService.add(IVAN, PETR, MASHA);

        Map<Integer, User> users = userService.getAllConvertedByID();

        assertAll(
                ()-> assertThat(users).containsKeys(IVAN.getId(), PETR.getId(), MASHA.getId()),
                ()-> assertThat(users).containsValues(IVAN, PETR, MASHA)
        );
    }

    @Test
    void loginFailIfPasswordIsNotCorrect(){
        userService.add(IVAN);
        var mayBeUser = userService.login(IVAN.getUsername(), "test");

        assertTrue(mayBeUser.isEmpty());
    }

    @Test
    void loginFailIfUserDoesNotExist(){
        userService.add(IVAN);
        var mayBeUser = userService.login("test", IVAN.getPassword() );

        assertTrue(mayBeUser.isEmpty());
    }


    @AfterEach
    void deleteDataFromDatabase(){
        System.out.println("After each: " + this);
    }

    @AfterAll
    static void closeConnectionPool(){
        System.out.println("After all: ");
    }

}
