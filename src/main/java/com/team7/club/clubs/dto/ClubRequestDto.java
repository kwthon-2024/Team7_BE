package com.team7.club.clubs.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.team7.club.community.entity.PostCategory;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClubRequestDto {

    @Getter
    @Setter
    public static class Post {

        @NotEmpty(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotEmpty(message = "내용은 필수 입력값입니다.")
        private String content;

        private PostCategory postCategory;

        private String image;
    }

}
