package services;
import services.interfaces.IMatrixFactory;
import services.interfaces.IMatrixOperationsService;

import java.rmi.RemoteException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AwesomeProcessor {

    private IMatrixOperationsService serverMatrixOperationsService;
    private IMatrixOperationsService clientMatrixOperationsService;
    private final IMatrixFactory serverFactory;
    private final IMatrixFactory clientFactory;

    public AwesomeProcessor(
            IMatrixOperationsService serverMatrixOperationsService,
            IMatrixOperationsService clientMatrixOperationsService,
            IMatrixFactory serverFactory,
            IMatrixFactory clientFactory) {
        this.serverMatrixOperationsService = serverMatrixOperationsService;
        this.clientMatrixOperationsService = clientMatrixOperationsService;
        this.serverFactory = serverFactory;
        this.clientFactory = clientFactory;
    }

    public double[][] execute() throws ExecutionException, InterruptedException {
        CompletableFuture<double[][]> AFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return clientFactory.createRandomMatrix();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<double[][]> bFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return serverFactory.createVectorB();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<double[][]> y1Future = CompletableFuture.allOf(AFuture, bFuture)
                .thenApplyAsync(ignored -> {
                    System.out.println("Thread for 'y1': " + Thread.currentThread().getName());
                    try {
                        return clientMatrixOperationsService.calculateY1(AFuture.join(), bFuture.join());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });

        CompletableFuture<double[][]> b1Future = CompletableFuture.supplyAsync(() -> {
            try {
                return serverFactory.createRandomVector();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<double[][]> c1Future = CompletableFuture.supplyAsync(() -> {
            try {
                return clientFactory.createRandomVector();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<double[][]> A1Future = CompletableFuture.supplyAsync(() -> {
            try {
                return clientFactory.createRandomMatrix();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<double[][]> y2Future = CompletableFuture.allOf(b1Future, c1Future, A1Future)
                .thenApplyAsync(ignored -> {
                    System.out.println("Thread for 'y2': " + Thread.currentThread().getName());
                    try {
                        return serverMatrixOperationsService.calculateY2(A1Future.join(), b1Future.join(), c1Future.join());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });

        CompletableFuture<double[][]> A2Future = CompletableFuture.supplyAsync(() -> {
            try {
                return serverFactory.createRandomMatrix();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<double[][]> B2Future = CompletableFuture.supplyAsync(() -> {
            try {
                return clientFactory.createRandomMatrix();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<double[][]> C2Future = CompletableFuture.supplyAsync(() -> {
            try {
                return serverFactory.createMatrixC();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<double[][]> y3Future = CompletableFuture.allOf(A2Future, B2Future, C2Future)
                .thenApplyAsync(ignored -> {
                    System.out.println("Thread for 'y3': " + Thread.currentThread().getName());
                    try {
                        return clientMatrixOperationsService.calculateY3(A2Future.join(), B2Future.join(), C2Future.join());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });

        System.out.println("Main thread for 'x': " + Thread.currentThread().getName());

        CompletableFuture.allOf(y1Future, y2Future, y3Future);

        var x = calculateX(y1Future.join(), y2Future.join(), y3Future.join());

        return x;
    }

    private double[][] calculateX(double[][] y1, double[][] y2, double[][] y3)
            throws ExecutionException, InterruptedException {
        var y1Transpose = CompletableFuture.supplyAsync(()-> {
            try {
                return serverMatrixOperationsService.transpose(y1);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        var y2Transpose = CompletableFuture.supplyAsync(()-> {
            try {
                return clientMatrixOperationsService.transpose(y2);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        var operation1 = CompletableFuture.supplyAsync(() -> {
            try{
                System.out.println("calculateX operation1 thread: " + Thread.currentThread().getName());
                var one = serverMatrixOperationsService.multiply(y3, y3);
                var two = serverMatrixOperationsService.multiply(one, y1);
                var three = serverMatrixOperationsService.add(two, y2);
                var four = serverMatrixOperationsService.multiply(y1Transpose.get(), three);
                return serverMatrixOperationsService.multiply(y3, four[0][0]);
            } catch (InterruptedException | ExecutionException | RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        var operation2 = CompletableFuture.supplyAsync(() -> {
            try{
                System.out.println("calculateX operation2 thread: " + Thread.currentThread().getName());
                return clientMatrixOperationsService.multiply(y1, y2Transpose.get());
            } catch (InterruptedException | ExecutionException | RemoteException e) {
                throw new RuntimeException(e);
            }
        });

        var operation3 = CompletableFuture.allOf(operation1, operation2)
                .supplyAsync(() -> {
                    try {
                        System.out.println("calculateX operation3 thread: " + Thread.currentThread().getName());
                        var one = serverMatrixOperationsService.add(operation1.join(), operation2.join());
                        var two = serverMatrixOperationsService.multiply(one, y2);
                        return serverMatrixOperationsService.transpose(two);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });

        System.out.println("calculateX main thread: " + Thread.currentThread().getName());

        try {
            return clientMatrixOperationsService.add(y2Transpose.get(), operation3.get());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
