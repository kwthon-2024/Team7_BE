package com.team7.club.notice.service;




import com.team7.club.notice.Entity.Notice;
import com.team7.club.notice.dto.NoticeRequestDto;
import com.team7.club.notice.dto.NoticeResponseDto;
import com.team7.club.notice.repository.NoticeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<NoticeResponseDto> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(notice -> new NoticeResponseDto(
                        notice.getNotificationId(),
                        notice.getWriter(),
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getImage()))
                .collect(Collectors.toList());
    }

    public NoticeResponseDto getNoticeById(Long notificationId) {
        Notice notice = noticeRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
        return new NoticeResponseDto(
                notice.getNotificationId(),
                notice.getWriter(),
                notice.getTitle(),
                notice.getContent(),
                notice.getImage());
    }

    public NoticeResponseDto createNotice(NoticeRequestDto requestDto) {
        Notice notice = new Notice(
                requestDto.getWriter(),
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getImage()
        );
        Notice savedNotice = noticeRepository.save(notice);
        return new NoticeResponseDto(
                savedNotice.getNotificationId(),
                savedNotice.getWriter(),
                savedNotice.getTitle(),
                savedNotice.getContent(),
                savedNotice.getImage()
        );
    }

    public NoticeResponseDto updateNotice(Long notificationId, NoticeRequestDto requestDto) {
        Notice notice = noticeRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        notice.setWriter(requestDto.getWriter());
        notice.setTitle(requestDto.getTitle());
        notice.setContent(requestDto.getContent());
        notice.setImage(requestDto.getImage());

        Notice updatedNotice = noticeRepository.save(notice);
        return new NoticeResponseDto(
                updatedNotice.getNotificationId(),
                updatedNotice.getWriter(),
                updatedNotice.getTitle(),
                updatedNotice.getContent(),
                updatedNotice.getImage()
        );
    }

    public void deleteNotice(Long notificationId) {
        noticeRepository.deleteById(notificationId);
    }
}