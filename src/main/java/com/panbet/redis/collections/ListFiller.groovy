package com.panbet.redis.collections

import groovy.util.logging.Slf4j
import org.springframework.scheduling.annotation.Scheduled

import java.util.concurrent.BlockingDeque
import java.util.concurrent.atomic.AtomicInteger

//@Component
@Slf4j
class ListFiller {

    private final BlockingDeque<String> redisList

    private final AtomicInteger counter = new AtomicInteger(0)

    ListFiller(BlockingDeque<String> redisList) {
        this.redisList = redisList
    }

    @Scheduled(fixedDelay = 3000L)
    void putToRedis() {
        log.debug('New list item')
        redisList.offerFirst(String.valueOf(counter.incrementAndGet()))
    }
}
