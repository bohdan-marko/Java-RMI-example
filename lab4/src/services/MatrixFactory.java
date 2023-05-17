package services;

import services.interfaces.IMatrixFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class MatrixFactory extends UnicastRemoteObject implements IMatrixFactory {

    private int n;
    private Random random;
    private String deviceName;

    public MatrixFactory(int n, Random random) throws RemoteException, UnknownHostException {
        super();
        this.n = n;
        this.random = random;
        this.deviceName = InetAddress.getLocalHost().getHostName();
    }

    @Override
    public double[][] createRandomMatrix() {
        System.out.println("Creating random matrix.\tDevice: " + deviceName);
        return createRandomMatrix(n, n);
    }

    @Override
    public double[][] createRandomVector() {
        System.out.println("Creating random vector.\tDevice: " + deviceName);
        return createRandomMatrix(n, 1);
    }

    private double[][] createRandomMatrix(int numRows, int numCols) {
        var matrix = new double[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                matrix[i][j] = random.nextDouble() * 10;
            }
        }
        return matrix;
    }

    @Override
    public double[][] createVectorB() {
        System.out.println("Creating vector B.\tDevice: " + deviceName);
        var b = new double[n][1];
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0)
                b[i][0] = Math.pow(i, 2) / 12.0;
            else
                b[i][0] = i;
        }
        return b;
    }

    @Override
    public double[][] createMatrixC() {
        System.out.println("Creating matrix C.\tDevice: " + deviceName);
        var C = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = 1.0 / (i + Math.pow(j, 2));
            }
        }
        return C;
    }
}

