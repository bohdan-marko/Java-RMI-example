package services.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IMatrixFactory extends Remote {
    double[][] createRandomMatrix() throws RemoteException;
    double[][] createRandomVector() throws RemoteException;
    double[][] createVectorB() throws RemoteException;
    double[][] createMatrixC() throws RemoteException;
}
