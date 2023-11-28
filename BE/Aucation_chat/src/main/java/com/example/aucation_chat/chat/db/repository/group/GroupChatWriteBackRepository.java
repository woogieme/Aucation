package com.example.aucation_chat.chat.db.repository.group;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.aucation_chat.common.redis.dto.RedisChatMessage;
import com.example.aucation_chat.common.util.DateFormatPattern;

@Repository
public class GroupChatWriteBackRepository {
	private final JdbcTemplate jdbcTemplate;

	public GroupChatWriteBackRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveAll(List<RedisChatMessage> chatList) {
		String sql = ""
			+ "insert into group_chat_message "
			+ "(member_pk, group_chat_message_content, group_chat_message_time, chat_room_group_chat_pk)"
			+ " values (?,?,?,?)";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				RedisChatMessage message = chatList.get(i);

				ps.setLong(1, message.getMemberPk());
				ps.setString(2, message.getMessageContent());
				ps.setTimestamp(3, Timestamp.valueOf(
					LocalDateTime.parse(message.getMessageTime(), DateTimeFormatter.ofPattern(DateFormatPattern.get()))
				));
				ps.setLong(4, message.getChatPk());
			}

			@Override
			public int getBatchSize() {
				return chatList.size();
			}
		});
	}

}
