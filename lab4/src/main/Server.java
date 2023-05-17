package main;

import services.MatrixFactory;
import services.MatrixOperationsService;
import utils.GlobalHelper;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            var serverFactory = new MatrixFactory(GlobalHelper.N, GlobalHelper.random);
            var serverMatrixService = new MatrixOperationsService();

            System.setProperty("java.rmi.server.hostname", GlobalHelper.ServerIpAddress);
            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("factory", serverFactory);
            registry.rebind("matrixService", serverMatrixService);

            System.out.println("Server started...");

        } catch (Exception e) {
            System.out.println("Server error" + e);
        }

    }
}
