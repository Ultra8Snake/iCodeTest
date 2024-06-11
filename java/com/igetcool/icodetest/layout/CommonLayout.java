package com.igetcool.icodetest.layout;

import com.igetcool.icodetest.boot.SettingsManager;
import com.igetcool.icodetest.models.event.CommonTextEvent;

/**
 * 单元测试公共类的布局实现类，用于生成Java类的布局代码。
 */
public class CommonLayout<T> extends Layout<T> {

    /**
     * 格式化并生成单元测试公共类的布局代码。
     *
     * @param t 包含类信息和测试代码的事件对象。
     * @return 格式化后的Java类布局代码字符串。
     */
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
