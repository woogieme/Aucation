package com.example.aucation_chat.chat.db.repository.group;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aucation_chat.chat.db.entity.group.GroupChatMessage;

public interface GroupChatMessageRepository extends JpaRepository<GroupChatMessage, Long> {
	List<GroupChatMessage> findByChatRoom_ChatPk_OrderByMessageTimeDesc(long chatPk);

	/** 메세지 전송시간을 기반으로 내림차순 정렬함으로써 최신 메세지 50개까지 조회 */
	List<GroupChatMessage> findTop50ByChatRoom_ChatPk_OrderByMessageTimeDesc(long chatPk);

}
