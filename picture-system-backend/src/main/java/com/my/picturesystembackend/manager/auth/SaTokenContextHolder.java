package com.my.picturesystembackend.manager.auth;

/**
 * 当前线程的空间鉴权上下文。
 *
 * <p>仅用于一次同步请求内，在业务代码已经查到被操作对象后将其传递给 Sa-Token。
 * 使用线程池时必须在请求结束后调用 {@link #clear()}，避免上下文串到下一个请求。</p>
 */
public final class SaTokenContextHolder {

    private static final ThreadLocal<SpaceUserAuthContext> CONTEXT = new ThreadLocal<>();

    private SaTokenContextHolder() {
    }

    public static void setContext(SpaceUserAuthContext authContext) {
        if (authContext == null) {
            clear();
            return;
        }
        CONTEXT.set(authContext);
    }

    public static SpaceUserAuthContext getContext() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
