package com.mall.config;

import com.mall.common.Role;
import com.mall.entity.*;
import com.mall.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final NewsRepository newsRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        // 管理员
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .nickname("管理员")
                .role(Role.ADMIN)
                .enabled(true)
                .build();
        userRepository.save(admin);

        // 商家与商家账号
        Merchant m1 = Merchant.builder().name("运动之星体育用品").description("专业运动器材和装备销售").enabled(true).build();
        m1 = merchantRepository.save(m1);
        User merchant = User.builder()
                .username("merchant")
                .password(passwordEncoder.encode("merchant123"))
                .nickname("商家")
                .role(Role.MERCHANT)
                .merchantId(m1.getId())
                .enabled(true)
                .build();
        userRepository.save(merchant);

        // 用户
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .nickname("用户")
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(user);

        // 分类
        Category c1 = Category.builder().name("足球用品").parentId(null).sortOrder(1).build();
        Category c2 = Category.builder().name("篮球用品").parentId(null).sortOrder(2).build();
        Category c3 = Category.builder().name("健身器材").parentId(null).sortOrder(3).build();
        Category c4 = Category.builder().name("羽毛球用品").parentId(null).sortOrder(4).build();
        Category c5 = Category.builder().name("乒乓球用品").parentId(null).sortOrder(5).build();
        categoryRepository.saveAll(List.of(c1, c2, c3, c4, c5));

        // 商品
        Product p1 = Product.builder().merchantId(m1.getId()).categoryId(c1.getId()).name("专业足球")
                .description("标准比赛用足球，采用高级PU材料").image("/api/images/football.jpg")
                .price(new BigDecimal("299")).originalPrice(new BigDecimal("399"))
                .stock(100).sales(0).onSale(true).isNew(true).build();
        Product p2 = Product.builder().merchantId(m1.getId()).categoryId(c2.getId()).name("篮球")
                .description("室内外通用篮球，防滑耐磨").image("/api/images/basketball.jpg")
                .price(new BigDecimal("199")).originalPrice(new BigDecimal("299")).stock(80).sales(0).onSale(true).isNew(false).build();
        Product p3 = Product.builder().merchantId(m1.getId()).categoryId(c3.getId()).name("跑步机")
                .description("家用电动跑步机，多档位调节").image("/api/images/treadmill.jpg")
                .price(new BigDecimal("1899")).originalPrice(new BigDecimal("2499")).stock(20).sales(5).onSale(true).isNew(true).build();
        Product p4 = Product.builder().merchantId(m1.getId()).categoryId(c4.getId()).name("羽毛球拍")
                .description("专业羽毛球拍，轻量化设计").image("/api/images/badminton.jpg")
                .price(new BigDecimal("399")).originalPrice(new BigDecimal("599")).stock(60).sales(3).onSale(true).isNew(false).build();
        Product p5 = Product.builder().merchantId(m1.getId()).categoryId(c5.getId()).name("乒乓球拍套装")
                .description("防守型乒乓球拍套装，含球和拍包").image("/api/images/pingpong.jpg")
                .price(new BigDecimal("299")).originalPrice(new BigDecimal("499")).stock(50).sales(2).onSale(true).isNew(true).build();
        productRepository.saveAll(List.of(p1, p2, p3, p4, p5));

        // 公告
        News news = News.builder().title("欢迎来到体育用品电商平台").content("我们为您提供最优质的运动器材和专业的购物体验。有任何问题欢迎联系客服。").type("ANNOUNCEMENT").published(true).build();
        newsRepository.save(news);
    }
}
