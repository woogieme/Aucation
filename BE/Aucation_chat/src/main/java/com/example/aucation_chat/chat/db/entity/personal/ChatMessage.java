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

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name ="chat_message")
public class ChatMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "message_pk")
	private long messagePk;

	@Column(name = "message_content")
	private String messageContent;

	@Column(name = "message_time", columnDefinition = "TIMESTAMP(3)")
	private LocalDateTime messageTime;

	@Column(name="member_pk")
	private long memberPk;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private ChatRoom chatRoom;

	private void setChatRoom(ChatRoom chatRoom){
		this.chatRoom = chatRoom;
		if(chatRoom != null){
			chatRoom.getChatMessages().add(this);
		}
	}

	@Builder
	public ChatMessage(long messagePk, String messageContent, LocalDateTime messageTime, long memberPk,
		ChatRoom groupChatRoom) {
		this.messagePk = messagePk;
		this.messageContent = messageContent;
		this.messageTime = messageTime;
		this.memberPk = memberPk;
		this.chatRoom = groupChatRoom;
	}
}
