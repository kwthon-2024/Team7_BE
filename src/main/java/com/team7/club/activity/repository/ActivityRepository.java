package com.team7.club.activity.repository;

import com.team7.club.activity.entity.Activity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> getByClubId(Long clubId);
}
