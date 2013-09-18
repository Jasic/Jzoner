package org.jasic.qzoner.core.handler;
import jpcap.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public abstract class AHandler {

    private final static Logger loger = LoggerFactory.getLogger(AHandler.class);

    public void before(Packet packet) {
    }

    public void after(Packet packet) {
    }

    public void process(Packet packet) {
        before(packet);
        handle(packet);
        after(packet);
    }

    public abstract void handle(Packet packet);
}
