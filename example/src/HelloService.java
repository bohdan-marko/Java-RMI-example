import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class HelloService extends UnicastRemoteObject implements HelloInterface {
    private String message;

    public HelloService(String message) throws RemoteException {
        this.message = message;
    }

    public String sayHello() throws RemoteException {
        try {
            System.out.println("Current device name is: " + InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return message;
    }
}
