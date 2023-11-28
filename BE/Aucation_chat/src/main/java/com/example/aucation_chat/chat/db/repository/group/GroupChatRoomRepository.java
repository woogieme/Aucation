package com.example.aucation_chat.chat.db.repository.group;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aucation_chat.chat.db.entity.group.GroupChatRoom;

public interface GroupChatRoomRepository extends JpaRepository<GroupChatRoom, Long> {
	Optional<GroupChatRoom> findByChatPk(long chatPk);

	Optional<GroupChatRoom> findByChatSession(String chatSession);
}
