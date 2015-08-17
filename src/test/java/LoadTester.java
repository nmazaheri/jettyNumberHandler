import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by navid.mazaheri on 8/17/15.
 */
public class LoadTester {
    public static void main(String[] args) {
        int portNumber = 4000;
        int maxIterations = 1000000000;

        try {
            Socket echoSocket = new Socket("localhost", portNumber);
            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
            for (int i = 0; i < maxIterations; ++i) {
                out.println(String.format("%09d", (int) (Math.random() * 1000000000.0)));
            }
            out.close();
            echoSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}