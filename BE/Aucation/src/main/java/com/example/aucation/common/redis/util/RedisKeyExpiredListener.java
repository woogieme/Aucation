package com.example.aucation.common.redis.util;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.example.aucation.auction.api.service.AuctionBidService;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {
    /**
     * Creates new {@link MessageListener} for {@code __keyevent@*__:expired} messages.
     *
     * @param listenerContainer must not be {@literal null}.
     */
    public RedisKeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Autowired
    private AuctionBidService auctionBidService;

    /**
     *
     * @param message   redis key
     * @param pattern   __keyevent@*__:expired
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("*********************** REDIS EXPIRED EVENT START !!");
        String key = message.toString();
        log.info("*********************** REDIS EXPIRED KEY = {} !!",key);

        String[] keyInfo = key.split(":");
        String aucUuid = keyInfo[1];
        String[] aucInfo = keyInfo[0].split("-");

        if(aucInfo[1].equals("pre")){
            log.info("*********************** 경매 시작 이벤트!!");
            try {
                auctionBidService.startAuction(aucUuid);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }else {
            log.info("*********************** 경매 종료 이벤트!!");
            try {
                auctionBidService.endAuction(aucUuid);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        log.info("*********************** REDIS EXPIRED EVENT END !!");
    }
}