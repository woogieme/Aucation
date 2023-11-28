package com.example.aucation.photo.db.repository;

import java.util.List;
import java.util.Optional;

import com.example.aucation.auction.db.entity.Auction;
import com.example.aucation.photo.db.PhotoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aucation.photo.db.Photo;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {
	Optional<List<Photo>> findByAuctionId(long auctionPk);
	Optional<Photo> findFirstByAuctionIdOrderByIdAsc(long auctionPk);
	void deleteByAuctionId(Long id);
}
