package com.igetcool.icodetest.appender;

import com.igetcool.icodetest.layout.Layout;

public interface Appender {

    <T> boolean append(T classTextEvent);

    void setLayout(Layout layout);

    Layout getLayout();
}