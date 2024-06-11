package com.igetcool.icodetest.appender;

import com.igetcool.icodetest.layout.Layout;

/**
 * Appender接口的抽象实现。
 * 此类提供了布局(Layout)的设置和获取功能。
 * 所有具体追加器实现类都应该继承此类，并实现具体追加逻辑。
 */
public abstract class AbstractAppender implements Appender {

    /**
     * 布局对象，用于格式化信息。
     * 布局对象定义了信息的格式和结构。
     */
    protected Layout layout;

    /**
     * 设置布局(Layout)。
     * 该方法允许外部代码设置格式。
     *
     * @param layout 布局对象
     */
    @Override
    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    /**
     * 获取当前设置的布局(Layout)。
     *
     * @return 返回当前设置的布局对象
     */
    @Override
    public Layout getLayout() {
        return layout;
    }
}
