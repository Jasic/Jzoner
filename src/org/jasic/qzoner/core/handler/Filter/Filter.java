package org.jasic.qzoner.core.handler.Filter;
import org.jasic.qzoner.core.entity.IData;
/**
 * User: Jasic
 * Date: 13-9-29
 */
public interface Filter {

    String getName();

    void filter(IData data);

}
