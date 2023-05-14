import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        try {
            // отримати реєстр з вузла "localhost" на порту 1099
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // знайти об'єкт за ім'ям "Hello"
            HelloInterface obj = (HelloInterface) registry.lookup("Hello");

            for (int i = 0; i < 10; i++) {
                System.out.println(obj.sayHello());
            }
        } catch (Exception e) {
            System.out.println("Client exception: " + e);
        }
    }
}
