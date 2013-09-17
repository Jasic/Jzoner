package org.jasic.qzoner.core;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.core.handler.HandlerDispatcher;
import org.jasic.qzoner.util.NetWorkUtil;
import org.slf4j.Logger;

import java.io.IOException;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class PacketCaptor {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(PacketCaptor.class);

    private JpcapCaptor jpcapCaptor;
    private IpMacPair ipMacPair;
    private HandlerDispatcher handler;
    private NetworkInterface nif;

    private int snaplen;//  Max number of bytes captured at once

    // unit ms
    private int timeout;// Timeout of processPacket(). Not all platforms support a timeout; on platforms that don't, the timeout is ignored. On platforms that support a timeout, a zero value will cause Jpcap to wait forever to allow enough packets to arrive, with no timeout.

    private String filter;

    private boolean blockMode;// processPacket()

    public PacketCaptor(IpMacPair ipMacPair) {
        this.ipMacPair = ipMacPair;
        this.init();
    }

    private void init() {
        this.snaplen = 1024;
        this.timeout = 1000;
        this.nif = NetWorkUtil.getIfByMac(this.ipMacPair.getMac());

    }

    public void startCaptor() {

        if (this.filter == null) {
            this.filter = "";
        }
        try {
            this.jpcapCaptor = JpcapCaptor.openDevice(nif, this.snaplen, true, timeout);
            this.jpcapCaptor.setFilter(this.filter, true);
            this.jpcapCaptor.setPacketReadTimeout(this.timeout);
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        this.jpcapCaptor.loopPacket(-1, new HandlerDispatcher());
    }

    public void stopCaptor() {
        this.jpcapCaptor.breakLoop();
        this.jpcapCaptor.close();
    }

    public HandlerDispatcher getHandler() {
        return handler;
    }

    public void setHandler(HandlerDispatcher handler) {
        this.handler = handler;
    }

    public int getSnaplen() {
        return snaplen;
    }

    public void setSnaplen(int snaplen) {
        this.snaplen = snaplen;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public boolean isBlockMode() {
        return blockMode;
    }

    public void setBlockMode(boolean blockMode) {
        this.blockMode = blockMode;
    }
}
