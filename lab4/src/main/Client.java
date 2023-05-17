package main;

import services.AverageProcessorEnjoyer;
import services.MatrixFactory;
import services.MatrixService;
import services.interfaces.IMatrixFactory;
import services.interfaces.IMatrixService;
import utils.GlobalHelper;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(GlobalHelper.ServerIpAddress, 1099);
            var startTime = System.currentTimeMillis();

            var serverFactory = (IMatrixFactory)registry.lookup("factory");
            var serverMatrixService = (IMatrixService)registry.lookup("matrixService");

            var clientFactory = new MatrixFactory(GlobalHelper.N, GlobalHelper.random);
            var clientMatrixService = new MatrixService();

            var processor = new AverageProcessorEnjoyer(
                    serverMatrixService,
                    clientMatrixService,
                    serverFactory,
                    clientFactory);

            var x = processor.execute();

            clientMatrixService.print("Result 'X'", x, true);

            var endTime = System.currentTimeMillis();
            System.out.println("Execution time: " + (endTime - startTime) + " ms");

        } catch (Exception e) {
            System.out.println("Client error: " + e);
        }

    }
}
