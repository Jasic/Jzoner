package org.jasic.qzoner.core.handler;
import jpcap.packet.Packet;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public abstract class AHandler {

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
