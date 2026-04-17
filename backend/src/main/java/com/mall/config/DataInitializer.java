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
        if (userRepository.count() == 0) {
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
        }

        // 分类
        Category root1 = ensureCategory("球类运动", null, 1);
        Category root2 = ensureCategory("健身训练", null, 2);
        Category root3 = ensureCategory("户外装备", null, 3);
        Category root4 = ensureCategory("游泳水上", null, 4);
        Category root5 = ensureCategory("运动配件", null, 5);

        Category c1 = ensureCategory("足球用品", root1.getId(), 1);
        Category c2 = ensureCategory("篮球用品", root1.getId(), 2);
        Category c3 = ensureCategory("羽毛球用品", root1.getId(), 3);
        Category c4 = ensureCategory("乒乓球用品", root1.getId(), 4);
        Category c5 = ensureCategory("网球用品", root1.getId(), 5);
        Category c6 = ensureCategory("健身器材", root2.getId(), 1);
        Category c7 = ensureCategory("瑜伽用品", root2.getId(), 2);
        Category c8 = ensureCategory("跑步装备", root2.getId(), 3);
        Category c9 = ensureCategory("力量训练", root2.getId(), 4);
        Category c10 = ensureCategory("登山露营", root3.getId(), 1);
        Category c11 = ensureCategory("自行车装备", root3.getId(), 2);
        Category c12 = ensureCategory("滑雪用品", root3.getId(), 3);
        Category c13 = ensureCategory("运动背包", root3.getId(), 4);
        Category c14 = ensureCategory("游泳装备", root4.getId(), 1);
        Category c15 = ensureCategory("泳衣泳镜", root4.getId(), 2);
        Category c16 = ensureCategory("水上娱乐", root4.getId(), 3);
        Category c17 = ensureCategory("运动鞋袜", root5.getId(), 1);
        Category c18 = ensureCategory("运动护具", root5.getId(), 2);
        Category c19 = ensureCategory("运动手套", root5.getId(), 3);
        Category c20 = ensureCategory("运动耳机", root5.getId(), 4);

        if (productRepository.count() == 0) {
            Merchant merchant = merchantRepository.findAll().stream().findFirst().orElse(null);
            if (merchant != null) {
                Product p1 = Product.builder().merchantId(merchant.getId()).categoryId(c1.getId()).name("专业足球")
                        .description("标准比赛用足球，采用高级PU材料").image("/api/images/football.jpg")
                        .price(new BigDecimal("299")).originalPrice(new BigDecimal("399"))
                        .stock(100).sales(0).onSale(true).reviewStatus("APPROVED").isNew(true).build();
                Product p2 = Product.builder().merchantId(merchant.getId()).categoryId(c2.getId()).name("篮球")
                        .description("室内外通用篮球，防滑耐磨").image("/api/images/basketball.jpg")
                        .price(new BigDecimal("199")).originalPrice(new BigDecimal("299")).stock(80).sales(0).onSale(true).reviewStatus("APPROVED").isNew(false).build();
                Product p3 = Product.builder().merchantId(merchant.getId()).categoryId(c6.getId()).name("跑步机")
                        .description("家用电动跑步机，多档位调节").image("/api/images/treadmill.jpg")
                        .price(new BigDecimal("1899")).originalPrice(new BigDecimal("2499")).stock(20).sales(5).onSale(true).reviewStatus("APPROVED").isNew(true).build();
                Product p4 = Product.builder().merchantId(merchant.getId()).categoryId(c3.getId()).name("羽毛球拍")
                        .description("专业羽毛球拍，轻量化设计").image("/api/images/badminton.jpg")
                        .price(new BigDecimal("399")).originalPrice(new BigDecimal("599")).stock(60).sales(3).onSale(true).reviewStatus("APPROVED").isNew(false).build();
                Product p5 = Product.builder().merchantId(merchant.getId()).categoryId(c4.getId()).name("乒乓球拍套装")
                        .description("防守型乒乓球拍套装，含球和拍包").image("/api/images/pingpong.jpg")
                        .price(new BigDecimal("299")).originalPrice(new BigDecimal("499")).stock(50).sales(2).onSale(true).reviewStatus("APPROVED").isNew(true).build();
                productRepository.saveAll(List.of(p1, p2, p3, p4, p5));
            }
        }

        // 公告
        String announcementTitle = "欢迎来到体育用品电商平台";
        String announcementContent = "我们为您提供最优质的运动器材和专业的购物体验。有任何问题欢迎联系客服。";
        if (!newsRepository.existsByTitleAndTypeAndPublishedTrue(announcementTitle, "ANNOUNCEMENT")) {
            News news = News.builder()
                    .title(announcementTitle)
                    .content(announcementContent)
                    .type("ANNOUNCEMENT")
                    .published(true)
                    .build();
            newsRepository.save(news);
        }
    }

    private Category ensureCategory(String name, Long parentId, int sortOrder) {
        return categoryRepository.findByNameAndParentId(name, parentId)
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(name)
                        .parentId(parentId)
                        .sortOrder(sortOrder)
                        .build()));
    }
}
