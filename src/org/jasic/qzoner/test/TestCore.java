package org.jasic.qzoner.test;
import org.jasic.qzoner.util.NetWorkUtil;

import static org.jasic.utils.StringUtils.mapToString;
import static org.jasic.utils.SystemUtil.getGateWayIp;
import static org.jasic.utils.SystemUtil.getLanMacByIp
        ;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class TestCore {

    public static void main(String[] args) throws Throwable {

        String gateWayIp = getGateWayIp("192.168.1.100");
        System.out.println(gateWayIp);
        String gateWayMac = getLanMacByIp(gateWayIp);
        System.out.println(gateWayMac);
        System.out.println(mapToString(getLanMacByIp(new String[]{"1.1.1.1",
                "172.16.24.80", "172.16.26.21", "172.16.27.1", "224.0.0.251", "224.0.0.252", "230.0.0.3", "239.203.13.64", "239.255.255.251"})));
    }
}
