package org.example.exam.DTOS;

import lombok.Data;
import org.example.exam.entity.Attachment;

@Data
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private Integer verifiedPassword;
    private Attachment attachment;

    public UserDTO() {
    }

    public UserDTO(String email, String password, String username, Attachment attachment) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.attachment = attachment;
    }
}
