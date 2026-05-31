package com.rajcloud.user;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    @Test
    void constructorSetsFields() {
        User user = new User("Pramod", "p@example.com");

        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo("Pramod");
        assertThat(user.getEmail()).isEqualTo("p@example.com");
    }

    @Test
    void protectedConstructorSupportsJpa() throws Exception {
        Constructor<User> constructor = User.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThat(constructor.newInstance().getId()).isNull();
    }
}
