package com.igetcool.icodetest.processor;

import com.igetcool.icodetest.enums.OperateType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

/**
 * Processor 接口定义了一个处理操作的契约，用于执行与特定操作类型相关的任务。
 * 该接口允许不同的实现类对 IntelliJ IDEA 插件中的操作进行封装和自定义。
 * 实现此接口的类将负责具体的处理逻辑，比如生成代码、执行分析或其他与编辑器相关的任务。
 */
public interface Processor {

    /**
     * 获取与此处理器关联的操作类型。
     *
     * @return 返回一个 OperateType 枚举值，表示处理器所处理的操作类型。
     */
    OperateType operateType();

    /**
     * 处理指定项目和编辑器中的操作。
     * 该方法将被触发以执行与操作类型相关的具体任务。
     *
     * @param project 当前的 IntelliJ IDEA 项目实例。
     * @param editor  当前活动编辑器实例，可以用于操作编辑器中的文本或光标位置。
     */
    void process(Project project, Editor editor);

}
