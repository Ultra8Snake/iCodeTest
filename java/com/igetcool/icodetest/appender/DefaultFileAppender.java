package com.igetcool.icodetest.appender;

import com.igetcool.icodetest.layout.DefaultLayout;
import com.igetcool.icodetest.models.ClassMetaInfo;
import com.igetcool.icodetest.models.event.DefaultTextEvent;
import com.igetcool.icodetest.utils.FileProcessor;

/**
 * DefaultFileAppender类是用于单元测试的文件追加器。
 * 此类继承自AbstractAppender类，并实现了针对特定事件对象的文件追加逻辑。
 * 它专门用于处理ClassTextEvent类型的事件，这些事件与单元测试的类元数据相关。
 * 此类提供了一个构造函数来初始化文件追加器实例，并定义了是否覆盖已存在文件的标志。
 */
public class DefaultFileAppender extends AbstractAppender {

    /**
     * 是否覆盖文件的标志。
     * <p>
     * 当此标志为true时，如果目标文件已存在，将被覆盖；为false时，内容将被追加到文件末尾。
     */
    private final boolean overwrite;

    /**
     * 构造函数，初始化文件追加器实例。
     *
     * @param overwrite 是否覆盖已存在文件的标志。
     */
    public DefaultFileAppender(boolean overwrite) {
        this.overwrite = overwrite;
        this.layout = new DefaultLayout<DefaultTextEvent>();
    }

    /**
     * 将单元测试事件对象追加到文件中。
     * 该方法接受ClassTextEvent类型的事件对象，并根据事件对象的类元数据和方法名创建或追加文件。
     * 如果事件对象包含方法名，文件名将包含该方法名以区分不同的测试用例。
     *
     * @param <T>   事件对象的类型，必须是ClassTextEvent或其子类型
     * @param event 要追加的单元测试事件对象
     * @return 如果事件成功追加，则返回true；否则返回false
     */
    @Override
    public <T> boolean append(T event) {
        DefaultTextEvent defaultTextEvent = (DefaultTextEvent) event;
        ClassMetaInfo classMetaInfo = defaultTextEvent.getClassMetaInfo();
        String includeMethodName = defaultTextEvent.getIncludeMethodName();
        String finalClassName = classMetaInfo.getFinalClassName();
        if (includeMethodName != null && !includeMethodName.isEmpty()) {
            finalClassName += "_" + includeMethodName;
            classMetaInfo.setFinalClassName(finalClassName);
        }
        return FileProcessor.createFileWithContent(
                classMetaInfo.getFinalAbsolutePath(),
                finalClassName + ".java",
                getLayout().format(defaultTextEvent),
                this.overwrite
        );
    }

}
