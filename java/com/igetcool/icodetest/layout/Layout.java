package com.igetcool.icodetest.layout;

/**
 * 布局抽象类，定义了布局生成的接口。
 * <p>
 * 此类作为所有布局生成类的基类，提供了一个抽象方法用于格式化和生成类布局代码。
 * 具体的布局实现类需要继承此类并实现抽象方法，以生成符合特定格式要求的Java类布局。
 */
public abstract class Layout<T> {

    public Layout() {
    }

    /**
     * 格式化并生成Java类的布局代码。
     * <p>
     * 此方法需要被子类重写，以实现具体的布局格式生成逻辑。
     * 它接收一个包含类信息和测试代码的事件对象，并返回格式化后的Java类布局代码字符串。
     *
     * @param t 包含类信息和测试代码的事件对象。
     * @return 格式化后的Java类布局代码字符串。
     */
    public abstract String format(T t);
}