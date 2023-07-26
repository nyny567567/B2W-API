package com.finalProject.stockbeginner.user.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class forceGradeDownRequestDTO {

    private String blackEmail;

    private String adminEmail;

}
