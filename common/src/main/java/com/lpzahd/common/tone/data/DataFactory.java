package com.lpzahd.common.tone.data;

import com.lpzahd.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ) 数据工厂，用于数据转换。
 * 这里有一个想法：直接操作数据源 与 保留数据源重新创建一份合适的数据 的争辩
 * ui 直接显示未加工的数据源操作（通过即时验证数据显示），优点 ： 简单，方便 缺点 ：元数据可能不足以补全，显示ui的时候承载了太多无关的操作
 * 保留数据源 优点： 分离显示 与 逻辑操作，直接展示最终效果。拓展字段容易。 缺点 ： 增加数据
 */
public class DataFactory<E, D> {

    /**
     * 加工
     */
    private DataProcess<E, D> process;

    private DataFactory() {
    }

    public void setProcess(DataProcess<E, D> process) {
        this.process = process;
    }

    public static <E, D> DataFactory<E, D> of(DataProcess<E, D> process) {
        DataFactory<E, D> factory = new DataFactory<>();
        factory.setProcess(process);
        return factory;
    }

    public D process(E e) {
        return process.process(e);
    }

    public List<D> processArray(List<E> list) {
        if(Lists.empty(list)) return Collections.emptyList();

        List<D> datas = new ArrayList<>(list.size());
        for(E e : list) {
            datas.add(process.process(e));
        }
        return datas;
    }

    public interface DataProcess<E, D> {
        D process(E e);
    }

}
