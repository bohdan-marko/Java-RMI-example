package services;

import services.interfaces.IMatrixFactory;
import services.interfaces.IMatrixOperationsService;
import java.rmi.RemoteException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AssProcessor {

    private IMatrixOperationsService serverMatrixService;
    private IMatrixOperationsService clientMatrixService;
    private final IMatrixFactory serverFactory;
    private final IMatrixFactory clientFactory;

    public AssProcessor(
            IMatrixOperationsService serverMatrixService,
            IMatrixOperationsService clientMatrixService,
            IMatrixFactory serverFactory,
            IMatrixFactory clientFactory) {
        this.serverMatrixService = serverMatrixService;
        this.clientMatrixService = clientMatrixService;
        this.serverFactory = serverFactory;
        this.clientFactory = clientFactory;
    }

    private CompletableFuture<double[][]> createMatrixAsync(
            IMatrixFactory factory,
            boolean isVector) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return isVector ? factory.createRandomVector() : factory.createRandomMatrix();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private CompletableFuture<double[][]> calculateY1(
            CompletableFuture<double[][]> A,
            CompletableFuture<double[][]> b)
    {
        return CompletableFuture.allOf(A, b)
                .thenApplyAsync(ignored -> {
                    try {
                        System.out.println("Thread for 'y1': " + Thread.currentThread().getName());
                        return clientMatrixService.calculateY1(A.join(), b.join());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private CompletableFuture<double[][]> calculateY2(
            CompletableFuture<double[][]> A1,
            CompletableFuture<double[][]> b1,
            CompletableFuture<double[][]> c1)
    {
        return CompletableFuture.allOf(A1, b1, c1)
                .thenApplyAsync(ignored -> {
                    try {
                        System.out.println("Thread for 'y2': " + Thread.currentThread().getName());
                        return serverMatrixService.calculateY2(A1.join(), b1.join(), c1.join());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private CompletableFuture<double[][]> calculateY3(
            CompletableFuture<double[][]> A2,
            CompletableFuture<double[][]> B2,
            CompletableFuture<double[][]> C2)
    {
        return CompletableFuture.allOf(A2, B2, C2)
                .thenApplyAsync(ignored -> {
                    try {
                        System.out.println("Thread for 'y3': " + Thread.currentThread().getName());
                        return clientMatrixService.calculateY3(A2.join(), B2.join(), C2.join());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public double[][] execute() throws ExecutionException, InterruptedException {
        CompletableFuture<double[][]> AFuture = createMatrixAsync(clientFactory, false);
        CompletableFuture<double[][]> bFuture = createMatrixAsync(serverFactory, true);

        CompletableFuture<double[][]> y1Future = calculateY1(AFuture, bFuture);

        CompletableFuture<double[][]> b1Future = createMatrixAsync(serverFactory, true);
        CompletableFuture<double[][]> c1Future = createMatrixAsync(serverFactory, true);
        CompletableFuture<double[][]> A1Future = createMatrixAsync(clientFactory, false);

        CompletableFuture<double[][]> y2Future = calculateY2(A1Future, b1Future, c1Future);

        CompletableFuture<double[][]> A2Future = createMatrixAsync(clientFactory, false);
        CompletableFuture<double[][]> B2Future = createMatrixAsync(clientFactory, false);
        CompletableFuture<double[][]> C2Future = createMatrixAsync(clientFactory, false);

        CompletableFuture<double[][]> y3Future = calculateY3(A2Future, B2Future, C2Future);

        System.out.println("Thread for 'x': " + Thread.currentThread().getName());

        CompletableFuture.allOf(y1Future, y2Future, y3Future);

        var x = calculateX(y1Future.join(), y2Future.join(), y3Future.join());

        return x;
    }

    private double[][] calculateX(double[][] y1, double[][] y2, double[][] y3) throws ExecutionException, InterruptedException {
        CompletableFuture<double[][]> y1Transpose = transposeMatrixAsync(serverMatrixService, y1);
        CompletableFuture<double[][]> y2Transpose = transposeMatrixAsync(serverMatrixService, y2);

        CompletableFuture<double[][]> operation1 = calculateOperation1(y1, y2, y3, y1Transpose, clientMatrixService);
        CompletableFuture<double[][]> operation2 = calculateOperation2(y1, y2Transpose, serverMatrixService);
        CompletableFuture<double[][]> operation3 = calculateOperation3(operation1, operation2, y2, clientMatrixService);

        System.out.println("[calculateX] Main Thread: " + Thread.currentThread().getName());

        try {
            return clientMatrixService.add(y2Transpose.get(), operation3.get());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<double[][]> transposeMatrixAsync(
            IMatrixOperationsService service,
            double[][] matrix) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return service.transpose(matrix);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private CompletableFuture<double[][]> calculateOperation1(
            double[][] y1,
            double[][] y2,
            double[][] y3,
            CompletableFuture<double[][]> y1Transpose,
            IMatrixOperationsService service) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("[calculateX] Thread for operation1: " + Thread.currentThread().getName());
                var step1 = service.multiply(y3, y3);
                var step2 = service.multiply(step1, y1);
                var step3 = service.add(step2, y2);
                var step4 = service.multiply(y1Transpose.get(), step3);
                return service.multiply(y3, step4[0][0]);
            } catch (InterruptedException | ExecutionException | RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private CompletableFuture<double[][]> calculateOperation2(
            double[][] y1,
            CompletableFuture<double[][]> y2Transpose,
            IMatrixOperationsService service) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("[calculateX] Thread for operation2: " + Thread.currentThread().getName());
                return service.multiply(y1, y2Transpose.get());
            } catch (InterruptedException | ExecutionException | RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private CompletableFuture<double[][]> calculateOperation3(
            CompletableFuture<double[][]> operation1,
            CompletableFuture<double[][]> operation2,
            double[][] y2,
            IMatrixOperationsService service) {
        return CompletableFuture.allOf(operation1, operation2)
                .thenApplyAsync(ignored -> {
                    try {
                        System.out.println("[calculateX] Thread for operation3: " + Thread.currentThread().getName());
                        var step1 = service.add(operation1.join(), operation2.join());
                        var step2 = service.multiply(step1, y2);
                        return service.transpose(step2);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
