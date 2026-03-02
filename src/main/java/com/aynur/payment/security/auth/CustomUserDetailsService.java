package com.aynur.payment.security.auth;

import com.aynur.payment.domain.entity.User;
import com.aynur.payment.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // login zamanı email ilə user tapır
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPassword(),
                u.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet())
        );
    }
}