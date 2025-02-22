package serverrmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {

	public static void main(String[] args) {
		try {
			//Tạo Object
			ChuyenDoiTienTe cdtt = new ChuyenDoiTienTe();
			
			//Đăng kí cổng
			LocateRegistry.createRegistry(1099);
			
			//Đưa ra cổng
			Naming.rebind("rmi://localhost/chuyenDoiTienTe", cdtt);
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

