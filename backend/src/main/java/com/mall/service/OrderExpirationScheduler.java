package com.mall.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(OrderExpirationScheduler.class);
    private final OrderService orderService;

    /**
     * 每 5 分钟检查一次未支付订单，超过 30 分钟则自动取消。
     */
    @Scheduled(fixedDelayString = "300000")
    public void cancelExpiredPendingOrders() {
        try {
            log.info("开始扫描过期待支付订单");
            orderService.cancelExpiredPendingOrders(30);
            log.info("过期待支付订单扫描完成");
        } catch (Exception e) {
            log.error("自动取消过期订单时出错", e);
        }
    }
}
