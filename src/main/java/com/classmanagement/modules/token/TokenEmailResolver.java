package com.classmanagement.modules.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenEmailResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        boolean isTokenMemberEmail = methodParameter.getParameterAnnotation(TokenEmail.class) != null;
        boolean isString = String.class.equals(methodParameter.getParameterType());
        return isTokenMemberEmail && isString;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String authorizationHeader = nativeWebRequest.getHeader("Authorization");
        log.info("Authorization Header ::: " + authorizationHeader);

        if (authorizationHeader == null) {
            throw new NotHaveAccessTokenException("Access Token이 존재하지 않습니다.");
        }

        String jwtToken = authorizationHeader.substring(7);
        Jwt decodedToken = JwtHelper.decode(jwtToken);
        Map<String, String> claims = objectMapper.readValue(decodedToken.getClaims(), Map.class);
        String email = claims.get("user_name");

        log.info("Decoded email is ::: " + email);
        return email;
    }

}
