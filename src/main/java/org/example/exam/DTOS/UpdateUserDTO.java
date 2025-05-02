package org.example.exam.DTOS;

import lombok.Value;

@Value
public class UpdateUserDTO {
    Integer id;
    String username;
    String email;
}
