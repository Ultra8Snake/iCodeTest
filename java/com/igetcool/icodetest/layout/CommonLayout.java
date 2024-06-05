package com.igetcool.icodetest.layout;

import com.igetcool.icodetest.boot.SettingsManager;
import com.igetcool.icodetest.models.CommonTextEvent;

public class CommonLayout<T> extends Layout<T> {

    @Override
    public String format(T t) {
        CommonTextEvent commonTextEvent = (CommonTextEvent) t;
        return String.format(SettingsManager.INSTANCE.getCommonClassBody(),
                SettingsManager.INSTANCE.getCommonPackageName(),
                commonTextEvent.getQualifiedName(),
                commonTextEvent.getClassName(),
                SettingsManager.INSTANCE.getCommonClassName()
        );
    }
}
