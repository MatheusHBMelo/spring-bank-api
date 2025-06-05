package br.com.springbank.service.user;

import br.com.springbank.domain.entities.user.*;
import br.com.springbank.domain.repositories.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    private UserEntity userEntity;

    private RoleEntity roleEntity;

    private PermissionEntity permissionEntity;

    @BeforeEach
    void setUp() {
        permissionEntity = PermissionEntity.builder().id(1L).name("READ").build();
        roleEntity = RoleEntity.builder().id(1L).name(RoleEnum.USER).permissions(Set.of(permissionEntity)).build();
        userEntity = UserEntity.builder().id(1L).username("Matheus").email("matheus@email.com").password("12345")
                .status(StatusEnum.ACTIVE).role(Set.of(roleEntity)).createdAt(LocalDateTime.now()).build();
    }

    @Test
    void deveCarregarOUsuarioComSucesso() {
        when(this.userRepository.findByUsername(anyString())).thenReturn(Optional.of(userEntity));

        UserDetails response = this.userDetailsService.loadUserByUsername("Matheus");

        assertEquals("Matheus", response.getUsername());
        assertEquals("12345", response.getPassword());
        assertTrue(response.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(response.getAuthorities().contains(new SimpleGrantedAuthority("READ")));

        verify(userRepository).findByUsername("Matheus");
    }

    @Test
    void deveRetornarExcecaoSeUsuarioNaoExistir() {
        when(this.userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UsernameNotFoundException ex = Assertions.assertThrows(UsernameNotFoundException.class,
                () -> this.userDetailsService.loadUserByUsername("NãoExiste")
        );

        assertEquals(UsernameNotFoundException.class, ex.getClass());
        assertEquals("O username NãoExiste não existe no sistema", ex.getMessage());

        verify(userRepository).findByUsername("NãoExiste");
    }
}