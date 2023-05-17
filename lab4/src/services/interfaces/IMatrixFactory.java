package services.interfaces;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IMatrixFactory extends Remote, Serializable {
    double[][] createRandomMatrix() throws RemoteException;
    double[][] createRandomVector() throws RemoteException;
    double[][] createVectorB() throws RemoteException;
    double[][] createMatrixC() throws RemoteException;
}
