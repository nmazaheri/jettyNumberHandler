import data.NumberAggregator;
import data.WindowDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by navid.mazaheri on 8/14/15.
 */
public class ServerListener extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ServerListener.class);
    private final int socket = 4000;
    private final int maxClients = 5;
    private long secondsBetweenLogAggregation = 10l;
    private List<Socket> clientSocketList = new ArrayList();
    private ExecutorService clientProcessingPool = Executors.newFixedThreadPool(maxClients);
    private WindowDataStore windowDataStore = new WindowDataStore();

    public static void main(String[] args) throws Exception {
        ServerListener server = new ServerListener();
        server.start();
    }

    public void run() {
        startLoggingTask();

        try {
            ServerSocket serverSocket = new ServerSocket(socket);
            logger.info("Server started on socket {}", socket);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (isBelowMaxClientCapacity()) {
                    clientSocketList.add(clientSocket);
                    logger.debug("client connected; currentClientPorts={}",
                            ClientUtil.getActivePorts(clientSocketList));
                    clientProcessingPool.submit(new ClientTask(clientSocketList, clientSocket, windowDataStore));
                } else {
                    // TODO: should i close the connection?
                    clientSocket.close();
                    logger.warn("Too many clients");
                }
            }

        } catch (IOException e) {
            logger.error("Unable to process client request. ", e);
        }
    }

    private void startLoggingTask() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        NumberAggregator numberAggregator = new NumberAggregator(windowDataStore);
        scheduledExecutorService
                .scheduleAtFixedRate(numberAggregator, 0l, secondsBetweenLogAggregation, TimeUnit.SECONDS);
    }

    private boolean isBelowMaxClientCapacity() {
        return clientSocketList.size() < maxClients;
    }

}


