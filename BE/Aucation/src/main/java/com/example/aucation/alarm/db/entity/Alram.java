package com.example.aucation.alarm.db.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.aucation.common.entity.BaseEntity;
import com.example.aucation.member.db.entity.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id",column = @Column(name="alram_pk"))
public class Alram extends BaseEntity {

	private String alramBody;
	private AlarmType alramType;
	private long alramTypePk;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn
//	private Member member;

	@Builder
	public Alram(Long id, LocalDateTime createdAt, Long createdBy, LocalDateTime lastModifiedAt,
		Long lastModifiedBy, boolean isDeleted, String alramBody, AlarmType alramType, long alramTypePk,
		Member member) {
		super(id, createdAt, createdBy, lastModifiedAt, lastModifiedBy, isDeleted);
		this.alramBody = alramBody;
		this.alramType = alramType;
		this.alramTypePk = alramTypePk;
//		addMember(member);
	}

//	private void addMember(Member member) {
//		if(this.member!=null){
//			this.member.getAlramList().remove(this);
//		}
//		this.member=member;
//		member.getAlramList().add(this);
//	}
}
