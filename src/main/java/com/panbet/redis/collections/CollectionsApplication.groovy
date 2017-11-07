package com.panbet.redis.collections

import com.google.common.util.concurrent.ThreadFactoryBuilder
import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.support.collections.DefaultRedisList
import org.springframework.scheduling.annotation.EnableScheduling

import java.util.concurrent.BlockingDeque
import java.util.concurrent.Executors

@SpringBootApplication
@EnableScheduling
@Slf4j
class CollectionsApplication {


    @Bean
    BlockingDeque<String> redisList(StringRedisTemplate stringRedisTemplate) {
        new DefaultRedisList<>(stringRedisTemplate.boundListOps("hello"), 100)
    }


    @Bean
    MessageListener keyspaceMessageListener() {
        new MessageListener() {
            @Override
            void onMessage(Message msg, byte[] pattern) {
                log.debug('Message channel {}, body {}', new String(msg.channel), new String(msg.body))
            }
        }
    }


    @Bean
    RedisMessageListenerContainer listenerContainer(RedisConnectionFactory connectionFactory,
                                                    MessageListener keyspaceMessageListener) {
        new RedisMessageListenerContainer().with {
            it.taskExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat('msg-listener-').build())
            it.connectionFactory = connectionFactory
            it.addMessageListener(keyspaceMessageListener, new ChannelTopic('__keyspace@0__:hello'))
            it
        }
    }

    @Bean
    RedisConnectionFactory lettuceConnectionFactory() {
        new LettuceConnectionFactory()
    }

    @Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        new StringRedisTemplate(connectionFactory)
    }

    static void main(String[] args) {
		SpringApplication.run(CollectionsApplication.class, args)
    }
}
