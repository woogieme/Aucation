package com.example.aucation.auction.db.entity;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.example.aucation.common.entity.BaseEntity;
import com.example.aucation.common.entity.HistoryStatus;
import com.example.aucation.member.db.entity.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id",column = @Column(name="auction_history_pk"))
public class AuctionHistory extends BaseEntity {

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

	@Builder
	public AuctionHistory(Long id, LocalDateTime createdAt, Long createdBy, LocalDateTime lastModifiedAt,
						  Long lastModifiedBy, boolean isDeleted, LocalDateTime historyDateTime, Auction auction
							, Member owner,Member customer, LocalDateTime historyDoneDateTime, HistoryStatus historyStatus) {
		super(id, createdAt, createdBy, lastModifiedAt, lastModifiedBy, isDeleted );
		this.historyDateTime = historyDateTime;
		this.historyDoneDateTime = historyDoneDateTime;
		this.auction = auction;
		this.historyStatus = historyStatus;
		addOwner(owner);
		addCustomer(customer);
	}

	private void addOwner(Member owner){
		if(this.owner!= null){
			this.owner.getAuctionHistoryOwnerList().remove(this);
		}
		this.owner=owner;
		owner.getAuctionHistoryOwnerList().add(this);
	}

	private void addCustomer(Member customer){
		if(this.customer!= null){
			this.customer.getAuctionHistoryCustomerList().remove(this);
		}
		this.customer=customer;
		customer.getAuctionHistoryCustomerList().add(this);
	}

    public void updateToConfirm() {
		this.historyStatus = HistoryStatus.AFTER_CONFIRM;
		this.historyDoneDateTime = LocalDateTime.now();
    }
}
