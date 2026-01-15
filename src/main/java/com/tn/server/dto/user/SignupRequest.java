package com.tn.server.dto.user;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @Pattern(
            regexp = "^[a-zA-Z0-9가-힣-_]{2,15}$",
            message = "닉네임은 숫자, 영어, 한글, -, _ 만 사용하여 2~15자여야 합니다."
    )
    private String nickname;
}