package org.example.exam.config;

import lombok.RequiredArgsConstructor;
import org.example.exam.entity.User;
import org.example.exam.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class CurrentUser implements UserDetailsService {
    private final UserRepository userRepostory;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepostory.findByEmail(username);
        if (user.isPresent()) {
            return user.get();
        }else {
            throw new UsernameNotFoundException(username);
        }
    }
}

