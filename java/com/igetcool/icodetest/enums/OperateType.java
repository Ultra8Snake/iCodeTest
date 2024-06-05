package com.igetcool.icodetest.enums;


public enum OperateType {

    FIXED("当前类快速生成单元测试"),
    CUSTOM("当前类自选方法快速生成单元测试"),
    RECURSIVE("当前类同级下所有类快速生成单元测试");

    private final String description;

    OperateType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
