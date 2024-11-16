package com.team7.club.notice.controller;

import com.team7.club.notice.dto.NoticeRequestDto;
import com.team7.club.notice.dto.NoticeResponseDto;
import com.team7.club.notice.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/club/notifications")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

//    @PreAuthorize("hasRole('ADMIN')") // ADMIN 권한만 접근 가능
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<NoticeResponseDto> createNotice(
            @Valid @RequestPart("noticeRequest") NoticeRequestDto requestDto, // JSON 데이터
            @RequestPart(value = "files", required = false) List<MultipartFile> files) { // 파일 데이터

        // 파일 처리
        if (files != null) {
            files.forEach(file -> {
                System.out.println("File Name: " + file.getOriginalFilename());
                System.out.println("File Size: " + file.getSize());
            });
        }
        NoticeResponseDto responseDto = noticeService.createNotice(requestDto);
        return ResponseEntity.ok(responseDto);
    }


//    @PreAuthorize("hasRole('ADMIN')") // ADMIN 권한만 접근 가능
    @PutMapping("/{notificationId}")
    public ResponseEntity<NoticeResponseDto> updateNotice(
            @PathVariable Long notificationId,
            @RequestBody NoticeRequestDto requestDto) {
        return ResponseEntity.ok(noticeService.updateNotice(notificationId, requestDto));
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NoticeResponseDto> getNoticeById(@PathVariable Long notificationId) {
        return ResponseEntity.ok(noticeService.getNoticeById(notificationId));
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponseDto>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }
//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long notificationId) {
        noticeService.deleteNotice(notificationId);
        return ResponseEntity.noContent().build();
    }
}