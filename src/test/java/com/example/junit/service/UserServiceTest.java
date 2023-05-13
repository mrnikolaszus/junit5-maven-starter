package com.example.junit.service;

import com.example.junit.TestBase;
import com.example.junit.dto.User;
import com.example.junit.extension.*;
import com.example.junit.service.Dao.UserDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.setLenientDateParsing;
import static org.hamcrest.collection.IsEmptyCollection.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;

@Tag("fast")
@Tag("user")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith({
        UserServiceParamResolver.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
        MockitoExtension.class
//        ThrowableExtension.class
})
public class UserServiceTest extends TestBase {
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    private static final User MASHA = User.of(3, "Masha", "222");

    @Captor
    private ArgumentCaptor<Integer> argumentCaptor;
    @Mock(lenient = true)
    private UserDao userDao;
    @InjectMocks
    private UserService userService;

    UserServiceTest(TestInfo testInfo){
        System.out.println();

    }

    @BeforeAll
    static void  init(){
        System.out.println("Before all: " );
    }

    @BeforeEach
    void prepare(){
        System.out.println("Before each: " + this);
//        lenient().when(userDao.delete(IVAN.getId())).thenReturn(true);
//        mockStatic().
        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());
//        Mockito.mock(UserDao.class, Mockito.withSettings().lenient());
//        this.userDao = Mockito.spy(new UserDao());
//        this.userService = new UserService(userDao);
    }

    @Test
    void throwExceptionIfDatabaseIsNotAvailabe(){
        Mockito.doThrow(RuntimeException.class).when(userDao).delete(IVAN.getId());
        assertThrows(RuntimeException.class, () -> userService.delete(IVAN.getId()));
    }
    @Test
    void shouldDeleteExistedUser(){
        userService.add(IVAN);
//        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());
//        Mockito.doReturn(true).when(userDao).delete(Mockito.anyInt());

//        Mockito.when(userDao.delete(IVAN.getId()))
//                .thenReturn(true)
//                .thenReturn(false);

//        BDDMockito.given(userDao.delete(IVAN.getId())).willReturn(true);
//
//        BDDMockito.willReturn(true).given(userDao).delete(IVAN.getId());

        var deleteResult = userService.delete(IVAN.getId());
        System.out.println(userService.delete(IVAN.getId()));
        System.out.println(userService.delete(IVAN.getId()));

//        var argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(userDao, Mockito.times(3)).delete(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(1);

//        Mockito.reset(userDao);
        assertThat(deleteResult).isTrue();
    }
    
    @Test
    @Order(1)
    @DisplayName("abc")
    void userEmptyIfNoUserAdded(UserService userService) throws IOException {
        if(true){
            throw new RuntimeException();
        }
        System.out.println("Test 1: " + this);
         var users = userService.getAll();

         MatcherAssert.assertThat(users, empty());
        assertTrue(users.isEmpty(), ()-> "User list should be Empty"  );
        // input -> [box = func] -> actual output
    }
    @Test
    void usersSizeIfUserAdded(){
        //given
        //when
        //then
        // when
        //then ]]
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);
        userService.add(MASHA);


        var users = userService.getAll();

        assertThat(users).hasSize(3);

//        assertEquals(3, users.size());

    }




    @Test
    void usersConvertedToMapByID(){
        userService.add(IVAN, PETR, MASHA);

        Map<Integer, User> users = userService.getAllConvertedByID();


        MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.getId()));


        assertAll(
                ()-> assertThat(users).containsKeys(IVAN.getId(), PETR.getId(), MASHA.getId()),
                ()-> assertThat(users).containsValues(IVAN, PETR, MASHA)
        );
    }




    @AfterEach
    void deleteDataFromDatabase(){
        System.out.println("After each: " + this);
    }

    @AfterAll
    static void closeConnectionPool(){
        System.out.println("After all: ");
    }

    @Nested
    @DisplayName("test user login")
    @Tag("login")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    class LoginTest{
        @Test
        @Disabled("flaky")
        void loginFailIfPasswordIsNotCorrect(){
            userService.add(IVAN);
            var mayBeUser = userService.login(IVAN.getUsername(), "test");

            assertTrue(mayBeUser.isEmpty());
        }

        @Test
        @RepeatedTest(value = 3, name = RepeatedTest.LONG_DISPLAY_NAME )
        void loginFailIfUserDoesNotExist(){
            userService.add(IVAN);
            var mayBeUser = userService.login("test", IVAN.getPassword() );

            assertTrue(mayBeUser.isEmpty());
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
        void checkLoginFuncPerf() {
            System.out.println(Thread.currentThread().getName());
            var result = assertTimeoutPreemptively(Duration.ofMillis(200), () -> {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(300L);
                return userService.login("dumm", IVAN.getPassword());

            });
        }
        @Test
//    @org.junit.Test(expected = IllegalArgumentException.class)
        void throwExceptionIfUserNameOrPasswordIsNull() {
//       try {
//           userService.login(null, "test");
//           fail("login should throw exception on null username");
//       } catch (IllegalArgumentException e){
//           assertTrue(true);
//       }
            assertAll(
                    () -> {
                        var exception =  assertThrows(IllegalArgumentException.class, () -> userService.login(null, "test"));

                        assertThat(exception.getMessage()).isEqualTo("username or password is null");
                    },
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("test", null)));


        }
//        assertThrows(IllegalArgumentException.class, () -> userService.login(null, "test"));
//        }
//                ()-> assertThat(users).containsKeys(IVAN.getId(), PETR.getId(), MASHA.getId()),
//                ()-> assertThat(users).containsValues(IVAN, PETR, MASHA)
//        );

    @ParameterizedTest(name = "{arguments} test")
//    @ArgumentsSource()
////    @NullSource
////    @EmptySource
////    @NullAndEmptySource
//    @ValueSource(strings = {
//    "Ivan", "Petr", "Masha"})

    @MethodSource("com.example.junit.service.UserServiceTest#getArgumentsForLoginTest")
//    @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
//    @CsvSource({
//            "Ivan,123",
//            "Petr,111",
//            "Masha,222"
//    })
    @DisplayName("LOGIN DISPNAME")
        void loginParameterizedTest(String username, String password, Optional<User> user){
            userService.add(IVAN, PETR, MASHA);

        var maybeUser = userService.login(username, password);
        assertThat(maybeUser).isEqualTo(user);
    }

    }
    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123",  Optional.of(IVAN)),
                Arguments.of("Petr", "111",  Optional.of(PETR)),
                Arguments.of("Masha", "222",  Optional.of(MASHA)),
                Arguments.of("Masha", "test",  Optional.empty()),
                Arguments.of("test", "222",  Optional.empty())
        );
    }



}
