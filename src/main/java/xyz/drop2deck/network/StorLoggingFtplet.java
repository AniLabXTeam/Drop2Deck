package xyz.drop2deck.network;

import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;

public class StorLoggingFtplet implements Ftplet {
    private static final String STOR_COMMAND = "STOR";
    private final StorCallback callback;

    public StorLoggingFtplet(StorCallback callback) {
        this.callback = callback;
    }

    @Override
    public void init(FtpletContext ftpletContext) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public FtpletResult beforeCommand(FtpSession session, FtpRequest request) {
        System.out.println(request.getRequestLine());
        String data = getStorCommandData(request);
        if (data != null) {
            callback.onStart(data);
        }
        return null;
    }

    @Override
    public FtpletResult afterCommand(FtpSession session, FtpRequest request, FtpReply reply) {
        System.out.println(request.getRequestLine());
        String data = getStorCommandData(request);
        if (data != null) {
            callback.onEnd(data);
        }
        return null;
    }

    @Override
    public FtpletResult onConnect(FtpSession session) {
        return null;
    }

    @Override
    public FtpletResult onDisconnect(FtpSession session) {
        return null;
    }

    private String getStorCommandData(FtpRequest request) {
        String requestLine = request.getRequestLine();
        if (requestLine.startsWith(STOR_COMMAND)) {
            return requestLine.replace(STOR_COMMAND, "").trim();
        }
        return null;
    }

    public interface StorCallback {
        void onStart(String fileName);
        void onEnd(String fileName);
    }
}