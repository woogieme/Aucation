package com.example.aucation_chat.discount.db.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.example.aucation_chat.common.dto.HistoryStatus;
import com.example.aucation_chat.member.db.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
// @AttributeOverrides({
// 	@AttributeOverride(name = "id", column = @Column(name = "discount_history_pk")),
// 	@AttributeOverride(name = "createdAt", column = @Column(name = "discount_history_created_at"))
// })
public class DiscountHistory{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "discount_history_pk")
	private	long discountHistoryPk;


	private LocalDateTime historyDatetime;
	private LocalDateTime historyDoneDatetime;
	private HistoryStatus historyStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="discount_owner_pk")
	private Member owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="discount_customer_pk")
	private Member customer;

	@OneToOne
	@JoinColumn(name="discount_pk")
	private Discount discount;
}
