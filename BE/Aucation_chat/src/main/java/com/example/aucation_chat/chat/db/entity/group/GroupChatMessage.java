package com.example.aucation_chat.chat.db.entity.group;

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

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name ="group_chat_message")
public class GroupChatMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "group_chat_message_pk")
	private long messagePk;

	@Column(name = "group_chat_message_content")
	private String messageContent;

	@Column(name = "group_chat_message_time", columnDefinition = "TIMESTAMP(3)")
	private LocalDateTime messageTime;

	@Column(name="member_pk")
	private long memberPk;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private GroupChatRoom chatRoom;

	private void setChatRoom(GroupChatRoom groupChatRoom){
		this.chatRoom = groupChatRoom;
		if(groupChatRoom != null){
			groupChatRoom.getGroupChatMessages().add(this);
		}
	}

	@Builder
	public GroupChatMessage(long messagePk, String messageContent, LocalDateTime messageTime, long memberPk,
		GroupChatRoom groupChatRoom) {
		this.messagePk = messagePk;
		this.messageContent = messageContent;
		this.messageTime = messageTime;
		this.memberPk = memberPk;
		this.chatRoom = groupChatRoom;
	}
}
