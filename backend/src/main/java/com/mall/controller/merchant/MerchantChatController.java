package com.mall.controller.merchant;

import com.mall.dto.Result;
import com.mall.entity.ChatMessage;
import com.mall.entity.User;
import com.mall.repository.ChatMessageRepository;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/merchant/chat")
@RequiredArgsConstructor
/** 商家客服聊天接口：会话查询与消息发送。 */
public class MerchantChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    /** 获取当前登录商家账号 ID。 */
    private Long currentMerchantUserId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    /** 拉取商家与指定用户的聊天记录。 */
    @GetMapping("/list")
    public ResponseEntity<Result<?>> list(Authentication auth, @RequestParam Long userId) {
        Long merchantUserId = currentMerchantUserId(auth);
        User me = userRepository.findById(merchantUserId).orElseThrow();
        if (me.getMerchantId() == null) throw new RuntimeException("非商家账号");
        List<ChatMessage> list = chatMessageRepository.findBySenderIdAndReceiverIdOrderByCreatedAtAsc(merchantUserId, userId);
        list.addAll(chatMessageRepository.findBySenderIdAndReceiverIdOrderByCreatedAtAsc(userId, merchantUserId));
        list.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        return ResponseEntity.ok(Result.ok(list));
    }

    /** 商家发送聊天消息给用户。 */
    @PostMapping("/send")
    public ResponseEntity<Result<?>> send(Authentication auth, @RequestBody Map<String, Object> body) {
        Long receiverId = Long.valueOf(body.get("receiverId").toString());
        String content = (String) body.get("content");
        Long senderId = currentMerchantUserId(auth);
        ChatMessage msg = ChatMessage.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .senderType("MERCHANT")
                .receiverType("USER")
                .content(content)
                .readFlag(false)
                .build();
        chatMessageRepository.save(msg);
        return ResponseEntity.ok(Result.ok(msg));
    }
}
