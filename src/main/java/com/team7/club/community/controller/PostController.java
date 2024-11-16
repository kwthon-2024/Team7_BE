package com.team7.club.community.controller;


import com.team7.club.common.config.AuthUser;
import com.team7.club.common.config.http.Response;
import com.team7.club.common.config.lib.Helper;
import com.team7.club.community.dto.request.PostRequestDto;
import com.team7.club.community.entity.Post;
import com.team7.club.community.service.PostService;
import com.team7.club.photo.util.S3Util;
import com.team7.club.user.entity.Users;
import com.team7.club.user.security.CustomUserPrincipal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/community")
@RestController
public class PostController {
    private final PostService postService;
    private final Response response;
    private final S3Util s3Util;

    @PostMapping
    public ResponseEntity<?> post(
            @Validated @RequestPart(value = "post") PostRequestDto.Post post,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Errors errors,
            @AuthUser CustomUserPrincipal customUserPrincipal) {

        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }

        Users users = customUserPrincipal.getUser();

        System.out.print("file:"+files.isEmpty());

// S3에 업로드한 이미지 URL 리스트 생성
        List<String> imageUrls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            System.out.println("why"); // 디버깅용 출력
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) { // 파일이 비어있는지 확인
                    System.out.println(file);
                    String imageUrl = s3Util.upload(file);
                    imageUrls.add(imageUrl);
                } else {
                    System.out.println("Empty file detected: " + file);
                }
            }
        }



        Post savedPost = Post.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .postCategory(post.getPostCategory())
                .build();


        if(!imageUrls.isEmpty()){
            savedPost.setImage(String.join(",", imageUrls));
        }

        return postService.savePost(savedPost, users);
    }


//  게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<?> postUpdate(@Validated @RequestPart(value = "post") PostRequestDto.Post post,
                                        @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                        Errors errors,
                                        @PathVariable Long postId, @AuthUser CustomUserPrincipal customUserPrincipal) {
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        Users users= customUserPrincipal.getUser();
        Post currentPost = postService.getCurPost(postId);
        if (currentPost.getUser().getId() != users.getId()) {
            return response.fail("작성자만 수정이 가능합니다!", HttpStatus.FORBIDDEN);
        }

        System.out.print("file:"+files);

// S3에 업로드한 이미지 URL 리스트 생성
        List<String> imageUrls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            System.out.println("why"); // 디버깅용 출력
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) { // 파일이 비어있는지 확인
                    System.out.println(file);
                    String imageUrl = s3Util.upload(file);
                    imageUrls.add(imageUrl);
                } else {
                    System.out.println("Empty file detected: " + file);
                }
            }
        }


        Post savedPost = Post.builder()
                .title(currentPost.getTitle())
                .content(currentPost.getContent())
                .postCategory(currentPost.getPostCategory())
                .build();

        if(!imageUrls.isEmpty()){
            savedPost.setImage(String.join(",", imageUrls));
        }

        return postService.updatePost(postId, savedPost);
    }

//  게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, @AuthUser CustomUserPrincipal customUserPrincipal) {
        Users users= customUserPrincipal.getUser();
        Post currentPost = postService.getCurPost(postId);
        if (currentPost.getUser().getId() != users.getId()) {
            return response.fail("작성자만 삭제가 가능합니다!", HttpStatus.FORBIDDEN);
        }

        return postService.deletePost(postId);
    }

//  게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

//  게시글 목록 조회
    @GetMapping
    public ResponseEntity<?> getPosts(@AuthUser CustomUserPrincipal customUserPrincipal) {
        Users users= customUserPrincipal.getUser();
        return postService.getPostList(users.getClub().getId());
    }


}

//@Valid @RequestPart(value = "post") PostRequestDto.Post post,
//@RequestPart(value = "files", required = false) List<MultipartFile> files
