package com.todayhouse.domain.email.dto.response;

import com.todayhouse.domain.email.domain.EmailVerificationToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TokenVerificationResponse {
    private String email;
    private String token;
    private LocalDateTime expiredAt;

    public TokenVerificationResponse(EmailVerificationToken token) {
        this.email = token.getEmail();
        this.token = token.getToken();
        this.expiredAt = token.getExpiredAt();
    }
}
