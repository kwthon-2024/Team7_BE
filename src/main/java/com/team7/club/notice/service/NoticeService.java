package com.team7.club.notice.service;

import com.team7.club.notice.Entity.Notice;
import com.team7.club.notice.dto.NoticeRequestDto;
import com.team7.club.notice.dto.NoticeResponseDto;
import com.team7.club.notice.repository.NoticeRepository;
import com.team7.club.user.entity.Users;
import com.team7.club.user.repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UsersRepository usersRepository;

    public NoticeService(NoticeRepository noticeRepository, UsersRepository usersRepository) {
        this.noticeRepository = noticeRepository;
        this.usersRepository = usersRepository;
    }

    // 공지사항 전체 조회
    public List<NoticeResponseDto> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(notice -> new NoticeResponseDto(
                        notice.getNotificationId(),
                        notice.getUser().getId(), // User의 ID 반환
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getCreatedDate(), // 생성일
                        notice.getUpdatedDate()  // 수정일
                ))
                .collect(Collectors.toList());
    }


    // ID로 공지사항 조회
    public NoticeResponseDto getNoticeById(Long notificationId) {
        Notice notice = noticeRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        return new NoticeResponseDto(
                notice.getNotificationId(),
                notice.getUser().getId(), // User의 ID 반환
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedDate(), // 생성일
                notice.getUpdatedDate()  // 수정일
        );
    }

    // 공지사항 생성
    public NoticeResponseDto createNotice(NoticeRequestDto requestDto) {
        // 현재 인증된 사용자 정보 가져오기
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 이메일로 사용자 조회
        Users user = usersRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // 공지사항 생성
        Notice notice = new Notice(
                user, // 현재 인증된 사용자
                requestDto.getTitle(),
                requestDto.getContent()
        );

        Notice savedNotice = noticeRepository.save(notice);

        return new NoticeResponseDto(
                savedNotice.getNotificationId(),
                savedNotice.getUser().getId(), // User의 ID 반환
                savedNotice.getTitle(),
                savedNotice.getContent(),
                savedNotice.getCreatedDate(), // 생성일
                savedNotice.getUpdatedDate()
        );
    }

    // 공지사항 수정
    public NoticeResponseDto updateNotice(Long notificationId, NoticeRequestDto requestDto) {
        Notice notice = noticeRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        // 현재 인증된 사용자 정보 가져오기
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 이메일로 사용자 조회
        Users user = usersRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // 업데이트 내용 설정
        notice.setUser(user);
        notice.setTitle(requestDto.getTitle());
        notice.setContent(requestDto.getContent());

        Notice updatedNotice = noticeRepository.save(notice);

        return new NoticeResponseDto(
                updatedNotice.getNotificationId(),
                updatedNotice.getUser().getId(), // User의 ID 반환
                updatedNotice.getTitle(),
                updatedNotice.getContent(),
                updatedNotice.getCreatedDate(), // 생성일
                updatedNotice.getUpdatedDate()  // 수정일
        );
    }

    // 공지사항 삭제
    public void deleteNotice(Long notificationId) {
        noticeRepository.deleteById(notificationId);
    }
}
