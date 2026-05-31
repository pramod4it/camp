package com.cloud.user;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {
    private final UserRepository repository = mock(UserRepository.class);
    private final UserController controller = new UserController(repository);

    @Test
    void createSavesUser() {
        User saved = new User("Pramod", "p@example.com");
        when(repository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(saved);

        User result = controller.create(new CreateUserRequest("Pramod", "p@example.com"));

        assertThat(result.getName()).isEqualTo("Pramod");
        verify(repository).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void findAllReturnsUsers() {
        when(repository.findAll()).thenReturn(List.of(new User("A", "a@example.com")));

        assertThat(controller.findAll()).hasSize(1);
    }

    @Test
    void findByIdReturnsUserWhenPresent() {
        when(repository.findById(1L)).thenReturn(Optional.of(new User("A", "a@example.com")));

        assertThat(controller.findById(1L).getEmail()).isEqualTo("a@example.com");
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.findById(99L)).isInstanceOf(RuntimeException.class);
    }
}
