package com.team7.club.notice.entity;

import com.team7.club.notice.Entity.Notice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notice_images") // 테이블 이름
public class NoticeImage {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 이미지 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice; // Notice와의 연관관계

    @Column(name = "image_path", nullable = true) // 선택사항으로 변경
    private String imagePath; // 이미지 경로

    public NoticeImage(String imagePath) {
        this.imagePath = imagePath;
    }
}
