package com.team7.club.activity.service;

import com.team7.club.activity.entity.Activity;
import com.team7.club.activity.repository.ActivityRepository;
import com.team7.club.common.config.http.Response;
import com.team7.club.community.entity.Post;
import com.team7.club.photo.util.S3Util;
import com.team7.club.user.entity.Users;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final Response response;
    private final S3Util s3Util;

    public ResponseEntity<?> savePost(Activity savedActivity, Users users) {
        savedActivity.setUser(users);
        savedActivity.setClub(users.getClub());
        savedActivity.setWriter(users.getName());
        activityRepository.save(savedActivity);
        return response.success("활동기록 등록 성공");
    }

    public ResponseEntity<?> getActivityList(Long clubId) {
        List<Activity> activityList = activityRepository.getByClubId(clubId);
        return response.success(activityList);
    }
}
