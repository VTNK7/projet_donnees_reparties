import java.rmi.RemoteException;
import java.util.ArrayList;

public class ServerObject {
    public enum T_state {NL, WL, RL};

	private Client_itf writer;

	private ArrayList<Client_itf> readers;

	private T_state state;

	private int id;

	public Object obj;

	public ServerObject(Object o, int id) {
		this.obj = o;
		this.id = id;
		this.state = T_state.NL;
		this.writer = null;
		this.readers = new ArrayList<Client_itf>();
	}

	// invoked by the user program on the client node
	public void lock_read(Client_itf client) {
		try {
			if (state == T_state.WL) {
				obj = writer.reduce_lock(id);
				readers.add(writer);
			}
			writer = null;
			readers.add(client);
			state = T_state.RL;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// invoked by the user program on the client node
	public void lock_write(Client_itf client) {
		if (state == T_state.RL) {
			readers.remove(client);
			for (Client_itf reader : readers) {
				try {
					reader.invalidate_reader(id);
				} catch (RemoteException e) {
					e.printStackTrace();
				}	
			}
		}

		if (state == T_state.WL) {
			try {
				obj = writer.invalidate_writer(id);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		readers.clear();
		writer = client;
		state = T_state.WL;
	}


}
