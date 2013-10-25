package org.jasic.qzoner.common;
import org.jasic.qzoner.core.entity.BaseData;
import org.jasic.qzoner.core.entity.IData;
import org.jasic.qzoner.core.entity.http.Header;
import org.jasic.qzoner.core.handler.filter.Filter;
import org.jasic.qzoner.core.handler.filter.FilterLine;
import org.jasic.qzoner.util.ParserUtil;

import java.nio.charset.Charset;
/**
 * User: Jasic
 * Date: 13-10-9
 */
public class FilterGenerator {

    public static FilterLine genTcpHttpReqFilterLine() {
        FilterLine line = new org.jasic.qzoner.core.handler.filter.DefaultFilterLine();
        line.addAfter("HTTP_REQUEST", new Filter() {
            @Override
            public String getName() {
                return "HTTP_REQUEST";
            }

            @Override
            public void filter(IData data) {

                BaseData bData = (BaseData) data;
                String reqstr = new String(bData.getData(), Charset.forName("UTF-8"));
                Header header;
                try {
                    header = ParserUtil.parserHeader(reqstr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return line;
    }
}
