package services;

import services.interfaces.IMatrixOperationsService;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatrixOperationsService extends UnicastRemoteObject implements IMatrixOperationsService {

    public MatrixOperationsService() throws RemoteException {
        super();
    }

    @Override
    public double[][] multiply(double[][] matrix1, double[][] matrix2) {
        System.out.println("Multiply matrices");
        int m1rows = matrix1.length;
        int m1cols = matrix1[0].length;

        int m2rows = matrix2.length;
        int m2cols = matrix2[0].length;

        if (m1cols != m2rows) {
            throw new IllegalArgumentException("Matrix multiply error");
        }

        double[][] result = new double[m1rows][m2cols];

        for (int i = 0; i < m1rows; i++) {
            for (int j = 0; j < m2cols; j++) {
                int sum = 0;
                for (int k = 0; k < m1cols; k++) {
                    sum += matrix1[i][k] * matrix2[k][j];
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    @Override
    public double[][] add(double[][] matrix1, double[][] matrix2) {
        System.out.println("Add matrices");
        int numRows = matrix1.length;
        int numCols = matrix1[0].length;

        if (numRows != matrix2.length || numCols != matrix2[0].length) {
            throw new IllegalArgumentException("Matrices have different sizes");
        }

        double[][] result = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                result[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }
        return result;
    }

    @Override
    public double[][] subtraction(double[][] matrix1, double[][] matrix2) {
        System.out.println("Subtract matrices");
        int numRows = matrix1.length;
        int numCols = matrix1[0].length;

        if (numRows != matrix2.length || numCols != matrix2[0].length) {
            throw new IllegalArgumentException("Matrices have different sizes");
        }

        double[][] result = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                result[i][j] = matrix1[i][j] - matrix2[i][j];
            }
        }
        return result;
    }

    @Override
    public double[][] multiply(double[][] matrix, double number) {
        System.out.println("Multiply matrix by a number");
        int numRows = matrix.length;
        int numCols = matrix[0].length;

        double[][] result = new double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                result[i][j] = matrix[i][j] * number;
            }
        }

        return result;
    }

    @Override
    public double[][] transpose(double[][] matrix) {
        System.out.println("Transpose matrix");
        int numRows = matrix.length;
        int numCols = matrix[0].length;

        double[][] result = new double[numCols][numRows];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }

    @Override
    public double[][] calculateY1(double[][] A, double[][] b) {
        System.out.println("Calculate 'Y1'");
        return multiply(A, b);
    }

    @Override
    public double[][] calculateY2(double[][] A1, double[][] b1, double[][] c1) {
        System.out.println("Calculate 'Y2'");
        return multiply(A1, subtraction(multiply(b1, 12), c1));
    }

    @Override
    public double[][] calculateY3(double[][] A2, double[][] B2, double[][] C2) {
        System.out.println("Calculate 'Y3'");
        return multiply(A2, subtraction(B2, C2));
    }

    @Override
    public synchronized void print(String name, double[][] matrix, Boolean print) {
        if (matrix == null || matrix.length == 0) {
            System.out.println("Matrix is empty.");
            return;
        }

        System.out.println("Matrix " + name);

        if (!print) {
            return;
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        int maxElementWidth = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int elementWidth = String.format("%.2f", matrix[i][j]).length();
                if (elementWidth > maxElementWidth) {
                    maxElementWidth = elementWidth;
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.printf("%" + (maxElementWidth + 3) + "s", String.format("%.2f", matrix[i][j]));
            }
            System.out.println();
        }
        System.out.println();
    }
}

