package com.finalProject.stockbeginner.user.dto;

import lombok.Data;

import java.util.Properties;

@Data
public class KakaoUserInfoDTO {
    private Long id;
    private String connected_at;
    private Properties properties;
    private KakaoAccount kakao_account;

    @Data
    public class Properties {
        private String nickname;
        private String profile_image;
        private String thumbnail_image;
    }

    @Data
    public class KakaoAccount {
        private String email;
        private Boolean email_needs_agreement;
        private Boolean has_email;
        private Boolean is_email_valid;
        private Boolean is_email_verified;
        private String gender;
        private Boolean gender_needs_agreement;
        private Boolean has_gender;
        private String age_range;
        private Boolean age_range_needs_agreement;
        private Boolean has_age_range;
        private profile profile;


        @Data
        public class profile {
            private String nickname;
            private String thumbnail_image_url;
            private String profile_image_url;
        }

        }

    }

