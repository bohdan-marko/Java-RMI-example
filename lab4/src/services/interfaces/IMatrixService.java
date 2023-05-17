package services.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IMatrixService extends Remote {
    double[][] multiply(double[][] matrix1, double[][] matrix2) throws RemoteException;

    double[][] add(double[][] matrix1, double[][] matrix2) throws RemoteException;

    double[][] subtraction(double[][] matrix1, double[][] matrix2) throws RemoteException;

    double[][] multiply(double[][] matrix, double number) throws RemoteException;

    double[][] transpose(double[][] matrix) throws RemoteException;

    double[][] calculateY1(double[][] A, double[][] b) throws RemoteException;

    double[][] calculateY2(double[][] A1, double[][] b1, double[][] c1) throws RemoteException;

    double[][] calculateY3(double[][] A2, double[][] B2, double[][] C2) throws RemoteException;

    void print(String name, double[][] matrix, Boolean print) throws RemoteException;
}
