package org.example.exam.DTOS;

import lombok.Value;
import org.example.exam.entity.Role;

import java.util.List;

@Value
public class AddUserDTO {

    String username;
    String email;
    String password;
    List<Role> roles;

}
