package org.jasic.qzoner.core.entity;
/**
 * User: Jasic
 * Date: 13-10-9
 */
public class BaseData implements IData {

    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
