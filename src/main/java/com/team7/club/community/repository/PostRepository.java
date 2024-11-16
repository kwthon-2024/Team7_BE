package com.team7.club.community.repository;

import com.team7.club.community.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> getByClubId(Long clubId);
}
