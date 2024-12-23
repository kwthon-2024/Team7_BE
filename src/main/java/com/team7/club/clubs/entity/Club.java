package com.team7.club.clubs.entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import lombok.*;
import com.team7.club.user.entity.BaseTime;
import com.team7.club.user.entity.ClubRole;
import com.team7.club.user.entity.Users;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Club extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String clubName; //동아리 이름
	@Column
	private Integer clubMember; //동아리 인원
	@Column
	private String clubProfessor; //동아리 지도 교수
	@Column
	private String clubDepartment; //동아리 소속학부
	@Column
	private String professorDepartment; //동아리 지도 교수 학부
	@Column
	private String chairManName; //회장 이름
	@Column
	private String chairManDepartment; //회장 학부
	@Column
	private Integer grade; // 학년
	@Column
	private String studentNumber; //회장 학번
	@Column
	private String phoneNumber; // 회장 전화번호


}
