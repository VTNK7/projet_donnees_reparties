import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;

public class Server implements Server_itf{
    
    private ArrayList<ServerObject> server_objects;

    private HashMap<String, Integer> table;

    public int lookup(String name) throws java.rmi.RemoteException{
        return table.get(name);
    }
	public void register(String name, int id) throws java.rmi.RemoteException{
        table.put(name, id);
    }

	public int create(Object o) throws java.rmi.RemoteException{
        int id = o.hashCode(); //il est possible qu'un hashcode ne soit pas unique néanmoins c'est très peu probable
        server_objects.add(new ServerObject(o, id));
        return id;
    }
	public Object lock_read(int id, Client_itf client) throws java.rmi.RemoteException{
        return null;
    }
	public Object lock_write(int id, Client_itf client) throws java.rmi.RemoteException{
        return null;
    }

    public static void main(String[] args) throws MalformedURLException, RemoteException, AlreadyBoundException{
        try {
            LocateRegistry.createRegistry(4000);
        } catch (Exception e) {
        }
        Naming.bind("//localhost:4000/server", new Server());
    }
}
