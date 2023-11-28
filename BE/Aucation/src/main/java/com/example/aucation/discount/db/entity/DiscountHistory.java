package com.example.aucation.discount.db.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.example.aucation.common.entity.BaseEntity;
import com.example.aucation.common.entity.HistoryStatus;
import com.example.aucation.member.db.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverrides({
	@AttributeOverride(name = "id", column = @Column(name = "discount_history_pk")),
	@AttributeOverride(name = "createdAt", column = @Column(name = "discount_history_created_at"))
})
public class DiscountHistory extends BaseEntity {

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

	@Builder
	public DiscountHistory(Long id, LocalDateTime createdAt, Long createdBy, LocalDateTime lastModifiedAt,
		Long lastModifiedBy, boolean isDeleted, LocalDateTime historyDatetime, LocalDateTime historyDoneDatetime,
		HistoryStatus historyStatus, Member owner, Member customer, Discount discount) {
		super(id, createdAt, createdBy, lastModifiedAt, lastModifiedBy, isDeleted);
		this.historyDatetime = historyDatetime;
		this.historyDoneDatetime = historyDoneDatetime;
		this.historyStatus = historyStatus;
		this.owner = owner;
		this.customer = customer;
		this.discount = discount;
	}

	public void updateStatus() {
		this.historyStatus = HistoryStatus.AFTER_CONFIRM;
	}

	public void updateStatusSell() {
		this.historyStatus=HistoryStatus.AFTER_CONFIRM;
	}

	public void updateStatusTime() {
		this.historyDoneDatetime=LocalDateTime.now();
	}
}
