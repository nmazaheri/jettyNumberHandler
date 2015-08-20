import data.WindowDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ServerUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by navid.mazaheri on 8/14/15.
 */
public class ServerListener {
    private static final Logger logger = LoggerFactory.getLogger(ServerListener.class);

    private final int socket = 4000;
    private final int maxClients = 5;
    private List<Socket> clientSockets = new ArrayList();
    private WindowDataStore windowDataStore;
    private ExecutorService clientProcessingPool = Executors.newFixedThreadPool(maxClients);
    private ServerSocket serverSocket = null;

    public ServerListener(WindowDataStore windowDataStore) {
        this.windowDataStore = windowDataStore;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(socket, maxClients);
            logger.info("Server started on socket {}", socket);
            while (!serverSocket.isClosed()) {
                handleClient(serverSocket.accept());
            }
        } catch (SocketException e) {
            if (!serverSocket.isClosed())
                logger.warn("server SocketException", e);

        } catch (Exception e) {
            logger.error("Unable to process client request. ", e);
        }
    }

    private void handleClient(Socket clientSocket) {
        synchronized (clientSockets) {
            clientSockets.add(clientSocket);
            logger.info("client connected port={}; currentClientPorts={}", clientSocket.getPort(),
                    ServerUtils.getActivePorts(clientSockets));
        }

        clientProcessingPool.submit(new ClientTask(clientSockets, clientSocket, windowDataStore, this));
    }

    public void shutdown() {
        logger.debug("closing server socket");
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.warn("unable to close serverSocket. ", e);
        }
        clientProcessingPool.shutdown();
        ServerUtils.disconnectAllClients(clientSockets);
    }

}


