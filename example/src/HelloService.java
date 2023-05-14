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
        InetAddress ip = null;
        InetAddress name = null;
        try {
            ip = InetAddress.getLocalHost();
            name = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Current IP address is: " + ip);
        System.out.println("Current device name is: " + name);
        return message;
    }
}
