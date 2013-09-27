package org.jasic.qzoner.util;
import org.jasic.qzoner.core.entity.http.Header;
import org.jasic.qzoner.core.entity.http.HeaderRowPair;
import org.jasic.utils.Asserter;

import java.io.BufferedReader;
import java.io.StringReader;
/**
 * User: Jasic
 * Date: 13-9-24
 */
public class ParserUtil {

    /**
     * 解释http请求头
     *
     * @param reqHeaderStr
     * @return
     * @throws Exception
     */
    public static Header parserHeader(String reqHeaderStr) throws Exception {

        Header header = new Header();
        Asserter.isTrue(reqHeaderStr != null);

        BufferedReader br = new BufferedReader(new StringReader(reqHeaderStr));
        String line = br.readLine();
        boolean host = false;
        boolean get = false;
        String[] lines = new String[2];
        while (line != null) {
            line = line.toLowerCase();
            String[] lineArr;

            if (line.startsWith("get ") || line.startsWith("post ")) {
                lineArr = line.split(" ");
                String type = lineArr[0];
                HeaderRowPair pair = new HeaderRowPair();
                pair.setName(lineArr[0]);
                pair.setValue(lineArr[1]);

                header.setType(type);
                header.getPairs().add(pair);

            } else {

                lineArr = line.split(": ");
                if (lineArr.length == 2) {
                    HeaderRowPair pair = new HeaderRowPair();
                    pair.setName(lineArr[0]);
                    pair.setValue(lineArr[1]);
                    header.getPairs().add(pair);
                }
            }
            line = br.readLine();
        }
        return header;
    }
}
