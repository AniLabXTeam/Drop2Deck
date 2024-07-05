package xyz.drop2deck.util;

public class QRCodeUtils {
    private static final String QR_GEN_URL = "https://quickchart.io/chart?cht=qr&chs=300&choe=UTF-8&chl=";

    public static String createQRCodeUrl(String data) {
        return QR_GEN_URL + data;
    }
}
