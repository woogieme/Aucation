package com.example.aucation_chat.chat.db.entity.personal;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_participant")
public class ChatParticipant {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "partici_pk")
	private long particiPk;

	// 입장시간
	@CreatedDate
	@Column(name = "partici_join")
	private LocalDateTime particiJoin;

	// 퇴장시간
	@Column(name = "partici_exit", columnDefinition = "TIMESTAMP")
	private LocalDateTime particiExit;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name="member_pk")
	// private Member member;
	@Column(name = "member_Pk")
	private long memberPk;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private ChatRoom chatRoom;

	// private void setMember(Member member){
	// 	this.member = member;
	// 	if(member!=null){
	// 		member.getC
	// 	}
	// }

	private void setChatRoom(ChatRoom chatRoom){
		this.chatRoom = chatRoom;
		if(chatRoom != null){
			chatRoom.getChatParticipants().add(this);
		}
	}

	@Builder
	public ChatParticipant(long particiPk, LocalDateTime particiJoin, LocalDateTime particiExit,
		long memberPk, ChatRoom chatRoom) {
		this.particiPk = particiPk;
		this.particiJoin = particiJoin;
		this.particiExit = particiExit;
		this.memberPk = memberPk;
		this.chatRoom = chatRoom;
	}
}
