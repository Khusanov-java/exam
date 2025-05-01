package org.example.exam.DTOS;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private Integer verifiedPassword;

    public UserDTO() {
    }

    public UserDTO(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }
}
