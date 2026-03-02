package com.aynur.payment.seed;

import com.aynur.payment.domain.entity.User;
import com.aynur.payment.domain.repository.UserRepository;
import com.aynur.payment.security.rbac.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // default admin user (yoxdursa)
        userRepository.findByEmail("admin@local.com").orElseGet(() -> {
            User admin = User.builder()
                    .email("admin@local.com")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of(Roles.ROLE_ADMIN.name(), Roles.ROLE_EDITOR.name()))
                    .build();
            return userRepository.save(admin);
        });
    }
}