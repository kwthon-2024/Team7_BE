package com.team7.club.notice.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.team7.club.user.entity.Users; // 정확한 경로로 import
import com.team7.club.notice.Entity.NoticeImage;
import java.util.ArrayList;
import java.util.List;


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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    private Users user; // 사용자 식별 번호

    @Column(name = "title", nullable = false, length =10)
    private String title; // 제목

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // 내용

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeImage> images = new ArrayList<>(); // 기본값 빈 리스트로 초기화




    public Notice(Users user, String title, String content, String image) {
        this.user = user;
        this.title = title;
        this.content = content;
    }
    public void addImage(NoticeImage image) {
        images.add(image);
        image.setNotice(this);
    }
}
