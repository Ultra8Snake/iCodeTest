package com.igetcool.icodetest.appender;

import com.igetcool.icodetest.boot.SettingsManager;
import com.igetcool.icodetest.layout.CommonLayout;
import com.igetcool.icodetest.models.event.CommonTextEvent;
import com.igetcool.icodetest.utils.FileProcessor;

/**
 * CommonFileAppender类是用于单元测试继承的公共文件追加器。
 * 此类继承自AbstractAppender类，并实现了具体的事件追加逻辑。
 * 它提供了一个构造函数来初始化文件追加器实例，并定义了是否覆盖已存在文件的标志。
 */
public class CommonFileAppender extends AbstractAppender {

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
    public CommonFileAppender(boolean overwrite) {
        this.overwrite = overwrite;
        this.layout = new CommonLayout<CommonTextEvent>();
    }

    /**
     * 将事件对象追加到文件中。
     * 该方法接受一个泛型事件对象，转换为CommonTextEvent类型，并调用文件处理器来创建或追加文件。
     *
     * @param <T>   事件对象的类型
     * @param event 要追加的事件对象
     * @return 如果事件成功追加，则返回true；否则返回false
     */
    @Override
    public <T> boolean append(T event) {
        CommonTextEvent commonTextEvent = (CommonTextEvent) event;
        return FileProcessor.createFileWithContent(
                getDirectoryPath(commonTextEvent),
                String.format("%s.java", SettingsManager.INSTANCE.getCommonClassName()),
                getLayout().format(commonTextEvent),
                this.overwrite
        );
    }

    /**
     * 获取事件对象的目录路径。
     *
     * @param commonTextEvent 事件对象
     * @return 返回事件对象的绝对路径，用于确定文件的存储位置
     */
    private String getDirectoryPath(CommonTextEvent commonTextEvent) {
        return commonTextEvent.getAbsolutePath();
    }
}
