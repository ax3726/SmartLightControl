package com.regenpod.smartlightcontrol.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 页面：定时器管理类
 *
 * @author lm
 */
@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public class ScheduledExecutorServiceManager {
    /**
     * 单例设计模式
     */
    private static class Holder {
        static ScheduledExecutorServiceManager mInstance = new ScheduledExecutorServiceManager();
    }

    public static ScheduledExecutorServiceManager getInstance() {
        return ScheduledExecutorServiceManager.Holder.mInstance;
    }

    /**
     * 核心线程池的数量，同时能够执行的线程数量
     */
    @SuppressWarnings("FieldCanBeLocal")
    private int corePoolSize;
    /**
     * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
     */
    @SuppressWarnings("FieldCanBeLocal")
    private int maximumPoolSize;
    /**
     * 存活时间
     */
    @SuppressWarnings("FieldCanBeLocal")
    private long keepAliveTime = 1;
    @SuppressWarnings("FieldCanBeLocal")
    private TimeUnit unit = TimeUnit.HOURS;
    private ScheduledExecutorService mScheduledExecutorService;

    private ScheduledExecutorServiceManager() {
        //给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行（有研究论证的）
        corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        mScheduledExecutorService = new ScheduledThreadPoolExecutor(corePoolSize,
                threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 延时执行
     *
     * @param command 执行方法
     * @param delay   延时时间
     */
    public ScheduledFuture schedule(Runnable command, long delay) {
        return mScheduledExecutorService.schedule(command, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 延时执行
     *
     * @param command 执行方法
     * @param delay   延时时间
     * @param unit    延时单位
     */
    public ScheduledFuture schedule(Runnable command, long delay, TimeUnit unit) {
        return mScheduledExecutorService.schedule(command, delay, unit);
    }

    /**
     * 延时执行
     *
     * @param callable 执行方法
     * @param delay    延时时间
     * @param unit     延时单位
     * @param <V>      返回类型
     */
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return mScheduledExecutorService.schedule(callable, delay, unit);
    }

    /**
     * initialDelay后，每{@param period}秒执行一次
     * 该方法设置了执行周期，下一次执行时间相当于是上一次的执行开始时间加上{@param period}，它是采用以固定的频率来执行任务：
     *
     * @param command      执行方法
     * @param initialDelay 初始延迟
     * @param period       每几毫秒执行一次
     * @return ？？
     */
    public ScheduledFuture scheduleAtFixedRate(Runnable command, long initialDelay, long period) {
        return scheduleAtFixedRate(command, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * initialDelay后，每period秒执行一次
     * 该方法设置了执行周期，下一次执行时间相当于是上一次的执行开始时间加上{@param period}，它是采用已固定的频率来执行任务：
     *
     * @param command      执行方法
     * @param initialDelay 初始延迟
     * @param period       每几个{@param unit}时间执行一次
     * @param unit         时间单位
     * @return ？？
     */
    public ScheduledFuture scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return mScheduledExecutorService.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /**
     * 上次执行完 command 后 ，delay时间后再次执行
     * 该方法设置了执行周期，与scheduleAtFixedRate方法不同的是，下一次执行时间是上一次任务 执行完 的系统时间加上{@param period}，
     * 因而具体执行时间不是固定的，但周期是固定的，是采用相对固定的延迟来执行任务：
     *
     * @param command      执行方法
     * @param initialDelay 初始延迟
     * @param delay        执行完后延迟几秒再次执行
     */
    public ScheduledFuture scheduleWithFixedDelay(Runnable command, long initialDelay, long delay) {
        return scheduleWithFixedDelay(command, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 上次执行完 command 后 ，delay时间后再次执行
     * 该方法设置了执行周期，与scheduleAtFixedRate方法不同的是，下一次执行时间是上一次任务 执行完 的系统时间加上{@param period}，
     * 因而具体执行时间不是固定的，但周期是固定的，是采用相对固定的延迟来执行任务：
     *
     * @param command      执行方法
     * @param initialDelay 初始延迟
     * @param delay        执行完后延迟几个{@param unit}再次执行
     * @param unit         时间单位
     */
    public ScheduledFuture scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return mScheduledExecutorService.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    /**
     * 延时执行
     *
     * @param command 执行方法
     */
    public void execute(Runnable command) {
        mScheduledExecutorService.execute(command);
    }
}