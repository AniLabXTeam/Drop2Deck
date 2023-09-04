package com.crazyxacker.apps.drop2deck.util;

public class QRCodeUtils {
    private static final String GOOGLE_QR_GEN_URL = "https://chart.googleapis.com/chart?cht=qr&chs=300&choe=UTF-8&chl=";

    public static String createQRCodeUrl(String data) {
        return GOOGLE_QR_GEN_URL + data;
    }
}
