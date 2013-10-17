package org.jasic.qzoner.core.handler.filter;
import org.jasic.qzoner.core.entity.IData;
/**
 * 对多条过滤线进行并发过滤，得出不同的结果，一条过滤线返回一条结果
 * <p/>
 * User: Jasic
 * Date: 13-9-29
 */
public interface FilterParallel {

    /**
     * Returns the name of the filter.
     */
    String getName();

    /**
     * Returns the filter.
     */
    FilterLine getFilterLine(String name);

    /**
     * Add new filterline
     *
     * @param filterLine
     * @return
     */
    void addFilterLine(FilterLine filterLine);

    /**
     * do concurrent filter
     *
     * @param data
     */
    void parallelFire(IData data);
}
