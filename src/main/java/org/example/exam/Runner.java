package org.example.exam;

import lombok.RequiredArgsConstructor;
import org.example.exam.entity.Role;
import org.example.exam.entity.Roles;
import org.example.exam.repository.RoleRepository;
import org.example.exam.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Runner implements CommandLineRunner {
    private final RoleRepository repository;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            repository.save(new Role(null, Roles.PROGRAMMER.name()));
            repository.save(new Role(null, Roles.ADMIN.name()));
            repository.save(new Role(null, Roles.MAINTAINER.name()));
        }
    }
}
