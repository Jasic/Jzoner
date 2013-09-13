package org.jasic.qzoner.util;
import cn.tisson.framework.utils.ByteUtils;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import org.jasic.utils.ByteUtil;
import org.junit.Test;

/**
 * User: Jasic
 * Date: 13-9-10
 */
public class NetWorkUtil {


    @Test
    public void test() {
       NetworkInterface iF = getIpByMac("78-45-C4-05-80-1D");
        System.out.println(iF);
    }

    /**
     * mac 地址字符需符合这样的规则 如:00-50-56-C0-00-08
     * @param mac
     * @return
     */
    public static NetworkInterface getIpByMac(String mac) {

        // 简单判断mac地址
        if (null == mac || mac.split("-").length != 6) {
            throw new RuntimeException("Mac address is wrong, expected like as [00-00-00-00-00-00] but get [" + mac + "]");
        }
        NetworkInterface eth = null;
        NetworkInterface[] ethList = JpcapCaptor.getDeviceList();

        for (jpcap.NetworkInterface iF : ethList) {
            if (iF.mac_address == null) continue;
            String actul_mac = ByteUtil.toHexString(iF.mac_address).replace(" ", "-");
            if (mac.equals(actul_mac)) {
                eth = iF;
                break;
            }
        }
        return eth;
    }

    /**
     * 将mac地址由字符转换byte数组
     * @param s
     * @return
     */
    public static byte[] macStrToByte(String s) {
        byte[] mac = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        String[] s1 = s.split("-");
        for (int x = 0; x < s1.length; x++) {
            mac[x] = (byte) ((Integer.parseInt(s1[x], 16)) & 0xff);
        }
        return mac;
    }
}
