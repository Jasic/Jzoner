package org.jasic.qzoner.core.entity.http;
import org.jasic.qzoner.core.entity.IData;

import java.util.ArrayList;
import java.util.List;
/**
 * User: Jasic
 * Date: 13-9-24
 */
public class Header implements IData{

    private String type; //请求类型 如get post

    private List<HeaderRowPair> pairs;

    public Header() {
        pairs = new ArrayList<HeaderRowPair>(16);
    }

    @Override
    public String toString() {
        return "Header{" +
                "type='" + type + '\'' +
                ", pairs=" + pairs +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<HeaderRowPair> getPairs() {
        return pairs;
    }

    public void setPairs(List<HeaderRowPair> pairs) {
        this.pairs = pairs;
    }
}
