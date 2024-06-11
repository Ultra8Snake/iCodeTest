package com.igetcool.icodetest.appender;

import com.igetcool.icodetest.layout.Layout;

/**
 * Appender接口定义了一个通用的事件追加机制。
 * 该接口允许实现者定义如何将不同类型的事件对象追加到目标媒介。
 * 此外，Appender接口还提供了布局(Layout)的设置和获取方法，用于定义事件对象的呈现方式。
 */
public interface Appender {

    /**
     * 将指定的事件对象追加到目标媒介。
     * 该方法接受一个泛型参数，使其能够处理不同类型的事件对象。
     *
     * @param <T>   事件对象的类型
     * @param event 要追加的事件对象
     * @return 如果事件成功追加，则返回true；否则返回false
     */
    <T> boolean append(T event);

    /**
     * 设置事件对象的布局(Layout)。
     * 该方法允许外部代码定义事件对象的呈现方式。
     *
     * @param layout 用于定义事件对象呈现方式的布局对象
     */
    void setLayout(Layout layout);

    /**
     * 获取当前设置的布局(Layout)。
     *
     * @return 返回当前设置的布局对象
     */
    Layout getLayout();

}