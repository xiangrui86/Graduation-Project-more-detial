package com.mall.repository;

import com.mall.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserIdOrderByCreatedAtAsc(Long userId);
    List<ChatMessage> findByUserIdAndProductIdOrderByCreatedAtAsc(Long userId, Long productId);
    List<ChatMessage> findByMerchantIdOrderByCreatedAtAsc(Long merchantId);
    List<ChatMessage> findByMerchantIdAndProductIdOrderByCreatedAtAsc(Long merchantId, Long productId);
    List<ChatMessage> findByProductIdOrderByCreatedAtAsc(Long productId);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.userId = :userId OR cm.productId IN (SELECT c.productId FROM ChatMessage c WHERE c.userId = :userId AND c.productId IS NOT NULL) ORDER BY cm.createdAt ASC")
    List<ChatMessage> findThreadMessagesForUser(Long userId);
}
