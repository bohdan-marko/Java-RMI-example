package services;
import services.interfaces.IMatrixFactory;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Random;

public class MatrixFactory implements IMatrixFactory {

    private int n;
    private Random random;

    public MatrixFactory(int n, Random random) throws RemoteException {
        this.n = n;
        this.random = random;
    }

    @Override
    public double[][] createRandomMatrix() {
        System.out.println("Execute createRandomMatrix method");
        return createRandomMatrix(n, n);
    }

    @Override
    public double[][] createRandomVector() {
        System.out.println("Execute createRandomVector method");
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
        System.out.println("Execute createVectorB method");
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
        System.out.println("Execute createMatrixC method");
        var C = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = 1.0 / (i + Math.pow(j, 2));
            }
        }
        return C;
    }
}

