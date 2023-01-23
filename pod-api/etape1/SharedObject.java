import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {

	public enum lockType {NL, RLC, WLC, RLT, WLT, RLT_WLC};

	private boolean attendre;

	private lockType lock;

	public Object obj;

	int id;

	public SharedObject(Object object, int id) {
		this.id = id;
    }

    // invoked by the user program on the client node
	public void lock_read() {
	}

	// invoked by the user program on the client node
	public void lock_write() {
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		return id;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
	}

	public synchronized Object invalidate_writer() {
		return id;
	}

    public int getId() {
        return this.id;
    }
}
