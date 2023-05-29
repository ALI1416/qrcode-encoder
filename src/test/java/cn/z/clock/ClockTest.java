package cn.z.clock;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * <h1>高性能时钟测试</h1>
 *
 * <p>
 * createDate 2021/02/24 11:54:46
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
class ClockTest {

    /**
     * 常规
     */
    @Test
    void test00Normal() {
        log.info("现在时间戳为：{}", Clock.now());
        log.info("现在Date为：{}", Clock.date());
        log.info("现在Timestamp为：{}", Clock.timestamp());
        // 现在时间戳为：1614583563907
        // 现在Date为：Mon Mar 01 15:26:03 CST 2021
        // 现在Timestamp为：2021-03-01 15:26:03.973
    }

    /**
     * 100万次高性能时钟与系统时钟比较
     */
    // @Test
    void test01Compare100w() {
        compare(1000000);
        // 高性能时钟调用1000000次使用时间为：1毫秒
        // 系统时钟调用1000000次使用时间为：5毫秒
        // 调用1000000次，高性能时钟比系统时钟性能高5.0倍
    }

    /**
     * 1000万次高性能时钟与系统时钟比较
     */
    // @Test
    void test02Compare1000w() {
        compare(10000000);
        // 高性能时钟调用10000000次使用时间为：2毫秒
        // 系统时钟调用10000000次使用时间为：22毫秒
        // 调用10000000次，高性能时钟比系统时钟性能高11.0倍
    }

    /**
     * 1亿次高性能时钟与系统时钟比较
     */
    // @Test
    void test03Compare1e() {
        compare(100000000);
        // 高性能时钟调用100000000次使用时间为：3毫秒
        // 系统时钟调用100000000次使用时间为：327毫秒
        // 调用100000000次，高性能时钟比系统时钟性能高109.0倍
    }

    /**
     * 21亿次高性能时钟与系统时钟比较
     */
    // @Test
    void test04Compare21e() {
        compare(Integer.MAX_VALUE);
        // 高性能时钟调用2147483647次使用时间为：35毫秒
        // 系统时钟调用2147483647次使用时间为：6720毫秒
        // 调用2147483647次，高性能时钟比系统时钟性能高192.0倍
    }

    /**
     * 高性能时钟与系统时钟比较
     *
     * @param count 比较次数
     */
    void compare(int count) {
        /* 初始化 */
        Clock.now();
        System.currentTimeMillis();
        /* 高性能时钟 */
        long a = Clock.now();
        for (int i = 0; i < count; i++) {
            Clock.now();
        }
        long b = Clock.now();
        long ba = b - a;
        log.info("高性能时钟调用{}次使用时间为：{}毫秒", count, ba);
        /*  系统时钟 */
        long c = Clock.now();
        for (int i = 0; i < count; i++) {
            System.currentTimeMillis();
        }
        long d = Clock.now();
        long dc = d - c;
        log.info("系统时钟调用{}次使用时间为：{}毫秒", count, dc);
        /* 比较 */
        log.info("调用{}次，高性能时钟比系统时钟性能高{}倍", count, dc / (double) ba);
    }

}
