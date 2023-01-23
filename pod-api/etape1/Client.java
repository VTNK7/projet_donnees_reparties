import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.rmi.registry.*;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {

	private static HashMap<Integer,SharedObject> client_objects;

	private static Client client;

	private static Server_itf server;


	public Client() throws RemoteException {
		super();
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		try {
			Client.client = new Client();
			server = (Server_itf) Naming.lookup("//localhost:3000/server");
			client_objects = new HashMap<Integer,SharedObject>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
		try {
			int id;
			id = server.lookup(name);
			return client_objects.get(id);
		} catch (Exception e) {
			e.getStackTrace();
		}
		return null;
	}
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
		try {
			server.register(name, ((SharedObject) so).getId());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		int id;
		try {
			id = server.create(o);
			SharedObject object = new SharedObject(o, id);
			client_objects.put(id, object);
			return object;
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
		return server.lock_read(id,client);
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		return id;
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		return client_objects.get(id).reduce_lock();
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		return id;
	}
}
