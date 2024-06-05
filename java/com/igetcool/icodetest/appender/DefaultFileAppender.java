package com.igetcool.icodetest.appender;

import com.igetcool.icodetest.layout.DefaultLayout;
import com.igetcool.icodetest.models.ClassMetaInfo;
import com.igetcool.icodetest.models.ClassTextEvent;
import com.igetcool.icodetest.utils.FileProcessor;

public class DefaultFileAppender extends AbstractAppender {

    private final boolean overwrite;

    public DefaultFileAppender(boolean overwrite) {
        this.overwrite = overwrite;
        this.layout = new DefaultLayout<ClassTextEvent>();
    }

    @Override
    public <T> boolean append(T t) {
        ClassTextEvent classTextEvent = (ClassTextEvent) t;
        ClassMetaInfo classMetaInfo = classTextEvent.getClassMetaInfo();
        String includeMethodName = classTextEvent.getIncludeMethodName();
        String finalClassName = classMetaInfo.getFinalClassName();
        if (includeMethodName != null && !includeMethodName.isEmpty()) {
            finalClassName += "_" + includeMethodName;
            classMetaInfo.setFinalClassName(finalClassName);
        }
        return FileProcessor.createFileWithContent(
                classMetaInfo.getFinalAbsolutePath(),
                finalClassName + ".java",
                getLayout().format(classTextEvent),
                this.overwrite
        );
    }

}
