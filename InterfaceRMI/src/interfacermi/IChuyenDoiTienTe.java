package interfacermi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChuyenDoiTienTe extends Remote{
	public double chuyenDoiTienTe(double tiGia1, double tiGia2, double soTien) throws RemoteException;
}


