import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.rmi.server.UnicastRemoteObject;
import java.time.Clock;

public class Server extends UnicastRemoteObject implements Server_itf {

    private HashMap<Integer,ServerObject> server_objects;
    private HashMap<String, Integer> table;

    protected Server() throws RemoteException {
        server_objects = new HashMap<Integer,ServerObject>();
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
        server_objects.put(id,new ServerObject(o, id));
        return id;
    }

    public Object lock_read(int id, Client_itf client) throws RemoteException {
        server_objects.get(id).lock_read(client);
        return server_objects.get(id).obj;
    }

    public Object lock_write(int id, Client_itf client) throws RemoteException {
        server_objects.get(id).lock_write(client);
        return server_objects.get(id).obj;
    }

    public static void main(String[] args) {
        Clock clock = Clock.systemUTC();
        try {
            Registry registre = LocateRegistry.createRegistry(3000);
            Naming.bind("//localhost:3000/server", new Server());
            
            while (true){
            Thread.sleep(5000);
            System.out.println("Server running " + clock.instant());
        }
        } catch (Exception e) {
        }
    }
}
