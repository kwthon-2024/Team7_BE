package com.team7.club.community.dto.request;


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
public class PostRequestDto {

    @Getter
    @Setter
    public static class Post {

        @NotEmpty(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotEmpty(message = "내용은 필수 입력값입니다.")
        private String content;

        private String image;
    }

    @Getter
    @Setter
    public static class UpdatePost {

        @NotEmpty(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotEmpty(message = "내용은 필수 입력값입니다.")
        private String content;

        private String image;
    }
}
