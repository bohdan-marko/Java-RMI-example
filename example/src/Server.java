import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public Server() {
        try {
            HelloService obj = new HelloService("Hello from Server");

            // створити реєстр на порту 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // реєструвати об'єкт з ім'ям "Hello"
            registry.rebind("Hello", obj);
        } catch (Exception e) {
            System.out.println("Server exception: " + e);
        }
    }

    public static void main(String[] args) {
        System.out.println("Server started...");
        new Server();
    }
}