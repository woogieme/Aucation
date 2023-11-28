package com.example.aucation_chat.chat.db.entity.group;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name="group_chat_room")
public class GroupChatRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "group_chat_pk")
	private long chatPk;

	@Column(name = "group_chat_session", unique = true)
	private String chatSession;

	@CreatedDate
	@Column(name="group_chat_create")
	private LocalDateTime chatCreate = LocalDateTime.now();

	@Column(name = "group_chat_end", columnDefinition = "TIMESTAMP")
	private LocalDateTime chatEnd;

	////////////////////////////////////////////////////
	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
	List<GroupChatParticipant> chatParticipants = new ArrayList<GroupChatParticipant>();

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
	List<GroupChatMessage> groupChatMessages = new ArrayList<GroupChatMessage>();


	@Builder
	public GroupChatRoom(long chatPk, String chatSession, LocalDateTime chatCreate, LocalDateTime chatEnd) {
		this.chatPk = chatPk;
		this.chatSession = chatSession;
		this.chatCreate = chatCreate;
		this.chatEnd = chatEnd;
	}
}
