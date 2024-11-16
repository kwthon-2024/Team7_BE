package com.team7.club.clubs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team7.club.clubs.entity.Club;
import com.team7.club.user.entity.Users;

public interface ClubRepository extends JpaRepository<Club,Long> {

	Optional<Club> findByClubName(String clubName);

	boolean existsByClubName(String clubName);
}
