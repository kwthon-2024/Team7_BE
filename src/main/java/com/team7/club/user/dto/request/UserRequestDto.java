package com.team7.club.user.dto.request;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.team7.club.user.entity.ClubRole;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserRequestDto {

	/** 회원 가입
	 * 학번, 동아리 이름, 동아리 직책, 이름, 이메일, 비밀번호
	 * */
	@Getter
	@Setter
	public static class SignUp {
		@NotEmpty(message = "이름은 필수 입력값입니다.")
		private String name;

		@NotEmpty(message = "이메일은 필수 입력값입니다.")
		@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
		private String email;

		@NotEmpty(message = "비밀번호는 필수 입력값입니다.")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
		private String password;

		@NotEmpty(message = "학번은 필수 입력값입니다.")
		private String studentNumber;

		@NotEmpty(message = "동아리 이름은 필수 입력값입니다.")
		private String clubName;

		@NotNull(message = "동아리 직책은 필수 입력값입니다.")
		private ClubRole clubRole;

		private Integer grade; //학년

		private Integer clubMember; //동아리 인원
		private String clubProfessor; //동아리 지도 교수

		private String clubDepartment; //동아리 소속학부
		private String professorDepartment; //동아리 지도 교수 학부
	}

	@Getter
	@Setter
	public static class Login {
		@NotEmpty(message = "이메일은 필수 입력값입니다.")
		@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
		private String email;

		@NotEmpty(message = "비밀번호는 필수 입력값입니다.")
		private String password;

		public UsernamePasswordAuthenticationToken toAuthentication() {
			return new UsernamePasswordAuthenticationToken(email, password);
		}
	}

	@Getter
	@Setter
	public static class Reissue {
		@NotEmpty(message = "accessToken 을 입력해주세요.")
		private String accessToken;

		@NotEmpty(message = "refreshToken 을 입력해주세요.")
		private String refreshToken;
	}

	@Getter
	@Setter
	public static class Logout {
		@NotEmpty(message = "잘못된 요청입니다.")
		private String accessToken;

		@NotEmpty(message = "잘못된 요청입니다.")
		private String refreshToken;
	}
}
