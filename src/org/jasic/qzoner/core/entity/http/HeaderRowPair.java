package org.jasic.qzoner.core.entity.http;
/**
 * User: Jasic
 * Date: 13-9-24
 */
public class HeaderRowPair {

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "NameValuePair{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public void setValue(String value) {
        this.value = value;
    }
}
