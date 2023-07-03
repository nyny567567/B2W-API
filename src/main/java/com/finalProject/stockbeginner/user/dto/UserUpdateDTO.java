package com.finalProject.stockbeginner.user.dto;

import com.finalProject.stockbeginner.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    private String password;

    private String nick;

    private String image;

    }





