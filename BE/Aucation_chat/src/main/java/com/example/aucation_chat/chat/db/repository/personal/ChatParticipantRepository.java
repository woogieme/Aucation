package com.example.aucation_chat.chat.db.repository.personal;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aucation_chat.chat.db.entity.personal.ChatParticipant;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
	List<ChatParticipant> findByChatRoom_ChatPk(long chatPk);
	Optional<ChatParticipant> findByChatRoom_ChatPkAndMemberPk(long chatPk, long memberPk);
}
