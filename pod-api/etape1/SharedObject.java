import java.io.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class SharedObject implements Serializable, SharedObject_itf {

	public ReentrantLock prio; 
	public Condition cond;

	public enum T_state {NL, RLC, WLC, RLT, WLT, RLT_WLC};
	private T_state state;
	private boolean attendre;



	public Object obj;

	private int id;

	public SharedObject(Object object, int id) {
		this.id = id;
    }

    // invoked by the user program on the client node
	public void lock_read() {
		prio.lock();
		switch(state){
			case NL: 
				state = T_state.RLT;
				obj = Client.lock_read(id);
				break;
			case RLC:
				state = T_state.RLT;
				break;
			case WLC:
				state = T_state.RLT_WLC;
				break;
			default:
				System.out.println("Choix incorrect lock_read");
				break;
		}
		prio.unlock();
	}

	// invoked by the user program on the client node
	public void lock_write() {
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		prio.lock();
		switch(state){
			case RLT_WLC: 
				state = T_state.WLC;
				break;
			case RLT:
				state = T_state.RLC;
				break;
			case WLT:
				state = T_state.WLC;
				break;
			default:
				System.out.println("Choix incorrect unlock");
				break;
		}
		prio.unlock();

	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		prio.lock();
		switch(state){
			case RLT_WLC: 
				state = T_state.RLT;
				obj = Client.lock_read(id);
				break;
			case WLT:
				state = T_state.RLC;
				break;
			case WLC:
				state = T_state.RLC;
				break;
			default:
				System.out.println("Choix incorrect reduce_lock");
				break;
		}
		prio.unlock();
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		prio.lock();
		switch(state){
			case RLT_WLC: 
				state = T_state.RLT;
				obj = Client.lock_read(id);
				break;
			case WLT:
				state = T_state.RLC;
				break;
			case WLC:
				state = T_state.RLC;
				break;
			default:
				System.out.println("Choix incorrect reduce_lock");
				break;
		}
		prio.unlock();
	}

	public synchronized Object invalidate_writer() {
		if (state == T_state.RLC)
	}

    public int getId() {
        return this.id;
    }
}
