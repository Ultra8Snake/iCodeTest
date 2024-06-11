package com.igetcool.icodetest.processor;

import com.igetcool.icodetest.enums.OperateType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

public class ProcessorContext {

    private final Processor processor;

    private ProcessorContext(Processor processor) {
        this.processor = processor;
    }

    public static ProcessorContext create(OperateType type) {
        Processor bean;
        switch (type) {
            case FIXED:
                bean = new FixedProcessor();
                break;
            case CUSTOM:
                bean = new CustomProcessor();
                break;
            case RECURSIVE:
                bean = new RecursiveProcessor();
                break;
            default:
                throw new IllegalArgumentException("Illegal Argument！");
        }
        return new ProcessorContext(bean);
    }

    public void process(Project project, Editor editor) {
        processor.process(project, editor);
    }
}
