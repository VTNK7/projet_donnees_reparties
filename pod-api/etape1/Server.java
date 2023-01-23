import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements Server_itf {

    private ArrayList<ServerObject> server_objects;

    private HashMap<String, Integer> table;

    protected Server() throws RemoteException {
        server_objects = new ArrayList<ServerObject>();
        table = new HashMap<String, Integer>();
    }

    public int lookup(String name) throws RemoteException {
        return table.get(name);
    }

    public void register(String name, int id) throws RemoteException {
        table.put(name, id);
    }

    public int create(Object o) throws RemoteException {
        int id = o.hashCode(); // il est possible qu'un hashcode ne soit pas unique néanmoins c'est très peu
                               // probable
        server_objects.add(new ServerObject(o, id));
        return id;
    }

    public Object lock_read(int id, Client_itf client) throws RemoteException {
        return null;
    }

    public Object lock_write(int id, Client_itf client) throws RemoteException {
        return null;
    }

    public static void main(String[] args) throws MalformedURLException, RemoteException, AlreadyBoundException {
        try {
            Registry registre = LocateRegistry.createRegistry(1888);
        } catch (Exception e) {
        }
        Naming.bind("//localhost:1888/server", new Server());
    }
}
