package com.example.aucation_chat.auction.db.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.example.aucation_chat.common.dto.HistoryStatus;
import com.example.aucation_chat.member.db.entity.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
// @AttributeOverride(name = "id",column = @Column(name="auction_history_pk"))
public class AuctionHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auction_history_pk")
	private	long auctionHistoryPk;

	private LocalDateTime historyDateTime;
	private LocalDateTime historyDoneDateTime;

	@OneToOne
	@JoinColumn(name="auction_pk")
	private Auction auction;

	@ManyToOne
	@JoinColumn(name = "owner_pk")
	private Member owner;

	@ManyToOne
	@JoinColumn(name = "customer_pk")
	private Member customer;

	private HistoryStatus historyStatus;
}
