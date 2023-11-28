package com.example.aucation.auction.db.repository;

import com.example.aucation.auction.db.entity.AuctionBid;
import com.example.aucation.common.redis.dto.SaveAuctionBIDRedis;
import com.example.aucation.common.util.DateFormatPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class AuctionBidRepository {
    private final JdbcTemplate jdbcTemplate;

    public AuctionBidRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAll(List<SaveAuctionBIDRedis> auctionBIDList, Long aucPk) {
        String sql = "insert into auction_bid (auction_bid_pk,auction_bid_price, auction_auction_pk, member_member_pk" +
                ",auction_bid_datetime) values (?,?,?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                SaveAuctionBIDRedis auctionBid = auctionBIDList.get(i);
                ps.setString(1, aucPk+"_"+i);
                ps.setLong(2, auctionBid.getBidPrice());
                ps.setLong(3, aucPk);
                ps.setLong(4, auctionBid.getPurchasePk());
                ps.setTimestamp(5, Timestamp.valueOf(
                        LocalDateTime.parse(auctionBid.getBidTime(), DateTimeFormatter.ofPattern(DateFormatPattern.get()))
                ));
            }

            @Override
            public int getBatchSize() {
                return auctionBIDList.size();
            }
        });
    }


}
