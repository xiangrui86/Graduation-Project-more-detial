package com.mall.service;

import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.entity.PaymentRecord;
import com.mall.repository.OrderItemRepository;
import com.mall.repository.OrderRepository;
import com.mall.repository.PaymentRecordRepository;
import com.mall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 模拟支付：用户点击支付确认后直接标记订单为已支付，并写入支付记录。
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String PAY_METHOD = "SIMULATE";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final PaymentRecordRepository paymentRecordRepository;

    @Transactional
    public boolean simulatePay(Long orderId, Long userId) {
        Optional<Order> opt = orderRepository.findById(orderId);
        if (opt.isEmpty()) return false;
        Order order = opt.get();
        if (!order.getUserId().equals(userId)) return false;
        if (!"PENDING".equals(order.getStatus())) return false;

        order.setStatus("PAID");
        order.setPayMethod(PAY_METHOD);
        order.setPayTime(LocalDateTime.now());
        order.setPayAmount(order.getTotalAmount());
        orderRepository.save(order);

        PaymentRecord record = PaymentRecord.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .payMethod(PAY_METHOD)
                .payAmount(order.getTotalAmount())
                .payTime(LocalDateTime.now())
                .build();
        paymentRecordRepository.save(record);

        // 更新商品销量
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        for (OrderItem oi : items) {
            productRepository.findById(oi.getProductId()).ifPresent(p -> {
                p.setSales(p.getSales() + oi.getQuantity());
                productRepository.save(p);
            });
        }
        return true;
    }
}
