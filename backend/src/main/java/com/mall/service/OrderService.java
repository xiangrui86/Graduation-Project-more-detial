package com.mall.service;

import com.mall.entity.CartItem;
import com.mall.entity.Order;
import com.mall.entity.OrderItem;
import com.mall.entity.Product;
import com.mall.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createFromCart(Long userId, Long merchantId, String receiverName, String receiverPhone, String receiverAddress) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        List<CartItem> merchantCart = items.stream().filter(c -> {
            Optional<Product> p = productRepository.findById(c.getProductId());
            return p.isPresent() && p.get().getMerchantId().equals(merchantId);
        }).toList();
        if (merchantCart.isEmpty()) throw new RuntimeException("购物车中无该商家商品");

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem c : merchantCart) {
            Product p = productRepository.findById(c.getProductId()).orElseThrow();
            if (p.getStock() < c.getQuantity()) throw new RuntimeException("商品 " + p.getName() + " 库存不足");
            BigDecimal sub = p.getPrice().multiply(BigDecimal.valueOf(c.getQuantity()));
            total = total.add(sub);
            orderItems.add(OrderItem.builder()
                    .productId(p.getId())
                    .productName(p.getName())
                    .productImage(p.getImage())
                    .price(p.getPrice())
                    .quantity(c.getQuantity())
                    .subTotal(sub)
                    .build());
        }

        String orderNo = "O" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + UUID.randomUUID().toString().substring(0, 8);
        Order order = Order.builder()
                .orderNo(orderNo)
                .userId(userId)
                .merchantId(merchantId)
                .status("PENDING")
                .totalAmount(total)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .receiverAddress(receiverAddress)
                .build();
        order = orderRepository.save(order);

        for (OrderItem oi : orderItems) {
            oi.setOrderId(order.getId());
            orderItemRepository.save(oi);
            Product p = productRepository.findById(oi.getProductId()).orElseThrow();
            p.setStock(p.getStock() - oi.getQuantity());
            productRepository.save(p);
        }

        merchantCart.forEach(c -> cartItemRepository.deleteById(c.getId()));
        return order;
    }

    public Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }

    public Page<Order> findByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<Order> findByMerchant(Long merchantId, Pageable pageable) {
        return orderRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId, pageable);
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public List<OrderItem> getItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Transactional
    public void updateStatus(Long orderId, String status) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        o.setStatus(status);
        orderRepository.save(o);
    }
}
