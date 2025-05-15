package br.com.springbank.service.user;

import br.com.springbank.controller.auth.dto.RegisterDto;
import br.com.springbank.domain.entities.user.RoleEntity;
import br.com.springbank.domain.entities.user.RoleEnum;
import br.com.springbank.domain.entities.user.StatusEnum;
import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.repositories.RoleRepository;
import br.com.springbank.domain.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity usercreated = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("O username " + username + " não existe no sistema"));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        usercreated.getRole()
                .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getName().name()))));

        usercreated.getRole()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(usercreated.getUsername(), usercreated.getPassword(), authorities);
    }

    public void registerUser(RegisterDto registerDto){
        String username = registerDto.username();
        String password = registerDto.password();

        RoleEntity role = this.roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new RuntimeException("Essa role não existe no sistema"));

        UserEntity newUser = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Set.of(role))
                .status(StatusEnum.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        this.userRepository.save(newUser);
    }
}
