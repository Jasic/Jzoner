package org.jasic.qzoner.core;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.core.handler.HandlerDispatcher;
import org.jasic.qzoner.util.NetWorkUtil;
import org.jasic.utils.StringUtils;
import org.jasic.utils.TimeUtil;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
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

    private static final Map<String, PacketCaptor> mac_captor;
    private int snaplen;//  Max number of bytes captured at once

    // unit ms
    private int timeout;// Timeout of processPacket(). Not all platforms support a timeout; on platforms that don't, the timeout is ignored. On platforms that support a timeout, a zero value will cause Jpcap to wait forever to allow enough packets to arrive, with no timeout.

    private String filter;

    private boolean blockMode;// processPacket()

    private long openDeviceTimeOut;//打开网卡超时

    static {
        mac_captor = new ConcurrentHashMap<String, PacketCaptor>();
    }

    private PacketCaptor(IpMacPair ipMacPair) {
        this.ipMacPair = ipMacPair;
        this.init();
    }

    /**
     * 根据地址对获取包捕获者
     *
     * @param ipMacPair
     * @return
     */
    public static final PacketCaptor newPacketCaptor(IpMacPair ipMacPair) {
        PacketCaptor captor = mac_captor.get(ipMacPair.getMac());
        if (null == captor) {
            captor = new PacketCaptor(ipMacPair);
            mac_captor.put(ipMacPair.getMac(), captor);
        }
        return captor;
    }

    private void init() {
        this.snaplen = 1024;
        this.timeout = 1000;
        this.openDeviceTimeOut = 20;//打开网卡超时

    }

    private boolean initIf() {
        this.nif = NetWorkUtil.getIfByMac(this.ipMacPair.getMac());
        return this.nif != null;
    }

    public void startCaptor() throws TimeoutException {

        if (this.filter == null) {
            this.filter = "";
        }
        try {
            long t1 = System.currentTimeMillis();
            long t2 = System.currentTimeMillis();

            while (!initIf() && (t2 - t1 < this.openDeviceTimeOut)) {
                t2 = System.currentTimeMillis();
                logger.info("网卡[" + this.ipMacPair + "]未初始化成功，继续尝试");
                TimeUtil.sleep(1);
            }
                logger.info("网卡[" + this.ipMacPair + "]初始化成功!");

            this.jpcapCaptor = JpcapCaptor.openDevice(nif, this.snaplen, true, timeout);
            this.jpcapCaptor.setFilter(this.filter, true);
            this.jpcapCaptor.setPacketReadTimeout(this.timeout);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new TimeoutException("打开指定网卡[" + StringUtils.entityToString(this.ipMacPair) + "]超时");
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
