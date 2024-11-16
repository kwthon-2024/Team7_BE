package com.team7.club.community.controller;


import com.team7.club.common.config.AuthUser;
import com.team7.club.common.config.http.Response;
import com.team7.club.common.config.lib.Helper;
import com.team7.club.community.dto.request.PostRequestDto;
import com.team7.club.community.entity.Post;
import com.team7.club.community.service.PostService;
import com.team7.club.user.entity.Users;
import com.team7.club.user.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/community")
@RestController
public class PostController {
    private final PostService postService;
    private final Response response;

//  게시글 등록
    @PostMapping
    public ResponseEntity<?> post(@Validated @RequestBody PostRequestDto.Post post, Errors errors, @AuthUser CustomUserPrincipal customUserPrincipal) {
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        Users users= customUserPrincipal.getUser();
        Post savedPost = Post.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .image(post.getImage())
                .build();
        return postService.savePost(savedPost);
    }

//  게시글 수정
    @PutMapping("/{communityId}")
    public ResponseEntity<?> postUpdate(@Validated @RequestBody PostRequestDto.Post post, Errors errors,
                                        @PathVariable Long communityId, @AuthUser CustomUserPrincipal customUserPrincipal) {
        Users users= customUserPrincipal.getUser();
        Post savedPost = Post.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .image(post.getImage())
                .build();
        return postService.updatePost(communityId, savedPost);
    }

//  게시글 목록 조회
    @GetMapping
    public ResponseEntity<?> getPosts(@AuthUser CustomUserPrincipal customUserPrincipal) {
        Users users= customUserPrincipal.getUser();
        return postService.getPostList(users.getId());
    }
}
