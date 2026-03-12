package com.mall.repository;

import com.mall.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySenderIdAndReceiverIdOrderByCreatedAtAsc(Long senderId, Long receiverId);

    List<ChatMessage> findByReceiverIdAndReadFlagFalse(Long receiverId);
}
