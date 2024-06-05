package com.igetcool.icodetest.appender;

import com.igetcool.icodetest.layout.Layout;

public abstract class AbstractAppender implements Appender {

    protected Layout layout;

    @Override
    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    @Override
    public Layout getLayout() {
        return layout;
    }
}
