package cn.z.clock;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <h1>高性能时钟</h1>
 *
 * <p>
 * createDate 2021/02/24 10:09:31
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
public class Clock {

    /**
     * 现在时间戳(原子长整形，防止多线程异常)
     */
    private static final AtomicLong NOW = new AtomicLong(System.currentTimeMillis());

    static {
        Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "Clock");
            thread.setDaemon(true);
            return thread;
        }).scheduleAtFixedRate(() -> NOW.set(System.currentTimeMillis()), 1, 1, TimeUnit.MILLISECONDS);
    }

    private Clock() {
    }

    /**
     * 获取时间戳
     */
    public static long now() {
        return NOW.get();
    }

    /**
     * 获取Date
     *
     * @since 1.2.0
     */
    public static Date date() {
        return new Date(NOW.get());
    }

    /**
     * 获取Timestamp
     *
     * @since 1.2.0
     */
    public static Timestamp timestamp() {
        return new Timestamp(NOW.get());
    }

}
