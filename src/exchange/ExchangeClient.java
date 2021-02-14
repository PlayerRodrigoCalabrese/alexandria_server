package exchange;

import ch.qos.logback.classic.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.LoggerFactory;
import kernel.Config;

import java.net.InetSocketAddress;

public class ExchangeClient {

    public static Logger logger = (Logger) LoggerFactory.getLogger(ExchangeClient.class);
    public static ExchangeClient INSTANCE;

    private IoSession ioSession;
    private ConnectFuture connectFuture;
    private IoConnector ioConnector;

    static {
        INSTANCE = new ExchangeClient();
    }

    private ExchangeClient() {
        init();
    }

    void setIoSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    private void init(){
        ioConnector = new NioSocketConnector();
        ioConnector.setHandler(new ExchangeHandler());
        ioConnector.setConnectTimeoutMillis(1000);
    }

    public boolean start() {
        if(!Config.INSTANCE.isRunning()) return true;
        try {
            connectFuture = ioConnector.connect(new InetSocketAddress(Config.INSTANCE.getExchangeIp(), Config.INSTANCE.getExchangePort()));
        } catch (Exception e) {
            logger.error("Can't find login server : ", e);
            return false;
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!connectFuture.isConnected()) {
            logger.error("Can't connect to login server");
            return false;
        }

        ExchangeClient.logger.info("Exchange client connected on address : {},{}", Config.INSTANCE.getExchangeIp(), Config.INSTANCE.getExchangePort());
        return true;
    }

    public void stop() {
        if(ioSession != null)
            ioSession.close(true);
        if (connectFuture != null)
            connectFuture.cancel();
        connectFuture = null;
        ioConnector.dispose();
        ExchangeClient.logger.info("Exchange client was stopped.");
    }

    void restart() {
        if(Config.INSTANCE.isRunning()) {
            stop();
            init();
            while(!INSTANCE.start()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public void send(String packet) {
        if(ioSession != null && !ioSession.isClosing() && ioSession.isConnected())
            ioSession.write(StringToIoBuffer(packet));
    }

    private static IoBuffer StringToIoBuffer(String packet) {
        IoBuffer ioBuffer = IoBuffer.allocate(30000);
        ioBuffer.put(packet.getBytes());
        return ioBuffer.flip();
    }
}
