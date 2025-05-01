package org.example.exam.Service;

import lombok.RequiredArgsConstructor;
import org.example.exam.entity.User;
import org.example.exam.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void save(User verifiedUser) {
        userRepository.save(verifiedUser);
    }
}
