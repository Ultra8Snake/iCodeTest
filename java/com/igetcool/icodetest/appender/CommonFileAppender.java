package com.igetcool.icodetest.appender;

import com.igetcool.icodetest.boot.SettingsManager;
import com.igetcool.icodetest.layout.CommonLayout;
import com.igetcool.icodetest.models.CommonTextEvent;
import com.igetcool.icodetest.utils.FileProcessor;

public class CommonFileAppender extends AbstractAppender {

    private final boolean overwrite;

    public CommonFileAppender(boolean overwrite) {
        this.overwrite = overwrite;
        this.layout = new CommonLayout<CommonTextEvent>();
    }

    @Override
    public <T> boolean append(T t) {
        CommonTextEvent commonTextEvent = (CommonTextEvent) t;
        return FileProcessor.createFileWithContent(
                getDirectoryPath(commonTextEvent),
                String.format("%s.java", SettingsManager.INSTANCE.getCommonClassName()),
                getLayout().format(commonTextEvent),
                this.overwrite
        );
    }

    private String getDirectoryPath(CommonTextEvent commonTextEvent) {
        return commonTextEvent.getAbsolutePath();
    }
}
