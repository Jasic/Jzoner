package org.jasic.qzoner.util;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import org.jasic.utils.ByteUtil;
import org.jasic.utils.StringUtils;
import org.junit.Test;

/**
 * User: Jasic
 * Date: 13-9-10
 */
public class NetWorkUtil {


    @Test
    public void test() {
        NetworkInterface iF = getIfByMac("78-45-C4-05-80-1D");
        System.out.println(iF);
        System.out.println(getIpByMac("78-45-C4-05-80-1D"));
        System.out.println(getSubNetByMac("78-45-C4-05-80-1D"));

    }

    /**
     * 根据mac地址获取字网掩码字符串
     *
     * @param mac
     * @return
     */
    public static String getSubNetByMac(String mac) {
        NetworkInterface iF = getIfByMac(mac);
        if (iF != null && iF.addresses != null && iF.addresses.length != 0) {
            for (NetworkInterfaceAddress address : iF.addresses) {
                if (address.subnet == null) continue;
                String ip = StringUtils.deleteWhitespace(address.subnet.getHostAddress());
                if (StringUtils.isMatch("^[1-2]?[0-9]{1,2}[.][1-2]?[0-9]{1,2}[.][1-2]?[0-9]{1,2}[.][1-2]?[0-9]{1,2}$",
                        ip)) {
                    return ip;
                }
            }
        }
        return null;
    }

    /**
     * 根据mac地址获取ip字符串
     *
     * @param mac
     * @return
     */
    public static String getIpByMac(String mac) {
        NetworkInterface iF = getIfByMac(mac);
        if (iF != null && iF.addresses != null && iF.addresses.length != 0) {
            for (NetworkInterfaceAddress address : iF.addresses) {
                String ip = StringUtils.deleteWhitespace(address.address.getHostAddress());
                if (StringUtils.isMatch("^[1-2]?[0-9]{1,2}[.][1-2]?[0-9]{1,2}[.][1-2]?[0-9]{1,2}[.][1-2]?[0-9]{1,2}$",
                        ip)) {
                    return ip;
                }
            }
        }
        return null;
    }


    /**
     * mac 地址字符需符合这样的规则 如:00-50-56-C0-00-08
     *
     * @param mac
     * @return
     */
    public static NetworkInterface getIfByMac(String mac) {

        // 简单判断mac地址
        if (null == mac || mac.split("-").length != 6) {
            throw new RuntimeException("Mac address is wrong, expected like as [00-00-00-00-00-00] but get [" + mac + "]");
        }
        NetworkInterface eth = null;
        NetworkInterface[] ethList = JpcapCaptor.getDeviceList();
        if (ethList == null || ethList.length == 0) return null;

        for (jpcap.NetworkInterface iF : ethList) {
            if (null == iF || iF.mac_address == null) continue;
            String actul_mac = ByteUtil.toHexString(iF.mac_address).replace(" ", "-");
            if (mac.equals(actul_mac)) {
                eth = iF;
                break;
            }
        }
        return eth;
    }

}
