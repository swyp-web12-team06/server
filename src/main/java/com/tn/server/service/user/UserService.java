package com.tn.server.service.user;

import com.tn.server.domain.user.Role;
import com.tn.server.domain.user.User;
import com.tn.server.exception.BusinessException;
import com.tn.server.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    // 회원가입 메서드 (예시)
    @Transactional
    public void signUp(User user, String nickname) { // 파라미터는 프로젝트에 맞게

        // 1. 이미 가입한 유저인지 검사 (핵심!)
        if (user.getRole() == Role.USER) {
            // 💥 여기서 에러를 던지면 GlobalExceptionHandler가 처리해줌!
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED);
        }

        // 2. 가입 로직 진행
        user.updateNickname(nickname);
        user.upgradeToUser(); // GUEST -> USER로 등업
    }
}