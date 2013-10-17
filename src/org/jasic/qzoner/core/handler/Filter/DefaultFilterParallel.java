package org.jasic.qzoner.core.handler.filter;
import org.jasic.qzoner.core.entity.IData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * This like a special filter pool,all the filter line must do filter.
 * User: Jasic
 * Date: 13-9-29
 */
public class DefaultFilterParallel implements FilterParallel {

    /**
     * Parallel name
     */
    private String name;

    /**
     * FilterLine cache
     */
    private Map<String, FilterLine> map;

    private ExecutorService es;

    public DefaultFilterParallel() {
        this("DefaultFilterParallel");
    }

    public DefaultFilterParallel(String name) {
        this.name = name;
        this.map = new ConcurrentHashMap<String, FilterLine>();
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public FilterLine getFilterLine(String name) {
        return map.get(name);
    }

    @Override
    public void addFilterLine(FilterLine filterLine) {
        map.put(filterLine.getName(), filterLine);
    }

    @Override
    public synchronized void parallelFire(IData data) {
        if (es == null) {
            es = Executors.newFixedThreadPool(map.size());
        }

        for (FilterLine filterLine : map.values()) {
            filterLine.fire(data);
        }
    }


}
