package br.com.springbank.service.user;

import br.com.springbank.domain.entities.user.UserEntity;
import br.com.springbank.domain.repositories.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("O username " + username + " não existe no sistema"));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        user.getRole()
                .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getName().name()))));

        user.getRole()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
