package org.jasic.qzoner.core.handler.Filter;
import org.jasic.qzoner.core.entity.IData;
/**
 * User: Jasic
 * Date: 13-9-29
 */
public interface FilterLine {

    /**
     * return this FilterLine name
     * @return
     */
    String getName();

    /**
     * Adds the specified filter with the specified name just before this entry.
     */
    void addBefore(String name, Filter filter);

    /**
     * Adds the specified filter with the specified name just after this entry.
     */
    void addAfter(String name, Filter filter);

    void fire(IData data);

    interface Entry {
        /**
         * Returns the name of the filter.
         */
        String getName();

        /**
         * Returns the filter.
         */
        Filter getFilter();

        /**
         * Adds the specified filter with the specified name just before this entry.
         */
        void addBefore(String name, Filter filter);

        /**
         * Adds the specified filter with the specified name just after this entry.
         */
        void addAfter(String name, Filter filter);

    }
}
