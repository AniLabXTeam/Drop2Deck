package com.crazyxacker.apps.drop2deck.util;

import java.net.*;
import java.util.*;

public class NetworkUtils {
    private static final Comparator<String> IP_COMPARATOR = (a, b) -> {
        int[] aOct = Arrays.stream(a.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int[] bOct = Arrays.stream(b.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int r = 0;
        for (int i = 0; i < aOct.length && i < bOct.length; i++) {
            r = Integer.compare(aOct[i], bOct[i]);
            if (r != 0) {
                return r;
            }
        }
        return r;
    };

    public static List<String> getLocalIPs() {
        List<String> addresses = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                final NetworkInterface cur = interfaces.nextElement();

                if (cur.isLoopback() || !cur.isUp()) {
                    continue;
                }

                for (InterfaceAddress addr : cur.getInterfaceAddresses()) {
                    InetAddress inetAddress = addr.getAddress();

                    if (!(inetAddress instanceof Inet4Address)) {
                        continue;
                    }

                    String address = inetAddress.getHostAddress();
                    if (!address.startsWith("192.168.")) {
                        continue;
                    }

                    addresses.add(inetAddress.getHostAddress());
                    addresses.sort(IP_COMPARATOR);
                }
            }

            return addresses;
        } catch (SocketException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
