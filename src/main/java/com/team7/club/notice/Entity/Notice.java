package com.team7.club.notice.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notification") // 테이블 이름과 매핑
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificationId")
    private Long notificationId; // 동아리 공지 식별번호

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "memberId", nullable = false)
//    private User user; // 사용자 식별 번호
    @Column(name = "writer", nullable = false)
    private String writer; // 작성자(임시)
    @Column(name = "title", nullable = false, length =10)
    private String title; // 제목

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // 내용

    @Column(name = "image", nullable = true)
    private String image; // 시신 (사진)


    //    public Notice(User user, String title, String content, String image) {
    public Notice(String writer,String title, String content, String image) {
//        this.user = user;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.image = image;
    }
}
