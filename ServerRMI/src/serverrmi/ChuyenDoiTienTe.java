package serverrmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import interfacermi.IChuyenDoiTienTe;

public class ChuyenDoiTienTe extends UnicastRemoteObject implements IChuyenDoiTienTe{
	
	protected ChuyenDoiTienTe() throws RemoteException{
		super();
	}
	
	@Override
	public double chuyenDoiTienTe(double tiGia1, double tiGia2, double soTien) throws RemoteException{
		return (tiGia2/tiGia1)*soTien;
	}
}


