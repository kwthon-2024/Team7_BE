package com.team7.club.common.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Retention(RetentionPolicy.RUNTIME) //  설정한 부분까지 정책이 유지되어야 한다.
@Target(ElementType.PARAMETER) // 커스텀한 어노테이션이 사용되는 위치 설정
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : #this")
// AnonymousAuthenticationFilter에 의해 생성된 Authentication의 경우 null을 반환.
public @interface AuthUser {
}
