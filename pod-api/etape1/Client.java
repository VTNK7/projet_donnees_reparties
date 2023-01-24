import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.rmi.registry.*;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {

	static HashMap<Integer,SharedObject> client_objects;

	private static Client client;

	private static Server_itf server;

	private static boolean bool = true;

	public Client() throws RemoteException {
		super();
		//client_objects = new HashMap<Integer,SharedObject>();
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		try {
			server = (Server_itf) Naming.lookup("//localhost:3000/server");
			if (bool){
				Client.client = new Client();
				client_objects = new HashMap<Integer,SharedObject>(); //on initialise la hashmap qu'au premier client
				bool = false;
			}
		} catch (Exception e) {
			System.out.println("Incapable de se connecter au serveur");
			e.printStackTrace();
		}
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {

		try {
			int id;
			System.out.println("lookup server");
			id = server.lookup(name);
			System.out.println("identifiant  : " + id);
			if (id == 0){
				return null;
				
			}
			SharedObject so = new SharedObject(null, id);
			client_objects.put(id, so);
			return so;
			
		} catch (Exception e) {
			System.out.println("FAIL LOOKUP");
			e.printStackTrace();
		}
		return null;
	}
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
		try {
			System.out.println("On enregistre " + name + " et id " + ((SharedObject) so).getId());
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
			System.out.println("Create id " + id);
			SharedObject object = new SharedObject(o, id);
			client_objects.put(id, object);
			System.out.println("Create taille " + client_objects.size());
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
		try {
			return server.lock_read(id,client);

		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		try {
			return server.lock_write(id,client);
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		return client_objects.get(id).reduce_lock();
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
		client_objects.get(id).invalidate_reader();
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		return client_objects.get(id).invalidate_writer();
	}
}
