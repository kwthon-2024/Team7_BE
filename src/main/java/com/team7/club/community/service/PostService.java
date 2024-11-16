package com.team7.club.community.service;

import com.team7.club.common.config.http.Response;
import com.team7.club.community.entity.Post;
import com.team7.club.community.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final Response response;


    public ResponseEntity<?> savePost(Post savedPost) {
        postRepository.save(savedPost);
        return response.success("게시글 등록 성공");
    }

    public ResponseEntity<?> updatePost(Long postId, Post updatedPost) {
        Post post = postRepository.findById(postId).get();

        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setImage(updatedPost.getImage());
        postRepository.save(post);
        return response.success("게시글 수정 성공");
    }

    public ResponseEntity<?> deletePost(Long postId) {
        postRepository.deleteById(postId);
        return response.success("게시글 삭제 성공");
    }

    public ResponseEntity<?> getPostList(Long clubId) {
        List<Post> postList = (List<Post>) postRepository.findById(clubId).get();
        return response.success(postList);
    }
}
