package org.jasic.qzoner.common;
import org.jasic.modue.JvmMonitor;
/**
 * User: Jasic
 * Date: 13-9-27
 */
public class SystemMonitor extends JvmMonitor {

    public SystemMonitor(int monInterval, boolean tDetail) {
        super(monInterval, tDetail);
        super.setName("系统监控");
    }

    @Override
    protected void doMon() {

        logger.info("Cache Monitor:");
        cacheMonitor();
        logger.info("");
    }


    private void cacheMonitor() {
        logger.info("online machine size :" + GlobalCaches.IP_MAC_LAN_CONNECTIVITY_CACHE.size());
        logger.info("data packets size :" + GlobalCaches.IP_MAC_LAN_PACKET_TO_BE_SEND_QUEUE);
    }
}
