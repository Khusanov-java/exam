package org.example.exam.DTOS;

import lombok.Value;
import org.example.exam.entity.Role;

import java.util.List;

@Value
public class AdminUpdateUserDTO {
    Integer id;
    String username;
    String email;
    List<Role> roles;
}
