package com.redot.auth;

import com.redot.domain.user.Role;
import com.redot.domain.user.User;
import com.redot.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oauth2User = super.loadUser(userRequest);

        String provider =
                userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = oauth2User.getAttributes();

        // provider별 사용자 정보 추출
        OAuthAttributes oauthAttributes =
                OAuthAttributes.of(provider, attributes);

        // User 조회 or 없으면 Guest로 저장
        User user = userRepository
                .findByProviderAndProviderId(
                        provider, oauthAttributes.getProviderId()
                )
                .map(entity -> entity.updateEmail(oauthAttributes.getEmail())) //네이버에 등록한 이메일을 사용자가 바꿨을 경우 새 이메일 가져옴.
                .orElse(oauthAttributes.toEntity(provider, Role.GUEST));
        User savedUser = userRepository.save(user);

        return new CustomOAuth2User(savedUser, attributes);
    }
}
