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
		this.obj = object;
		this.state = T_state.NL;
		this.attendre = false;
		this.prio = new ReentrantLock();
		this.cond = prio.newCondition();
    }

    // invoked by the user program on the client node
	public void lock_read() {
		prio.lock();
        boolean bool = false;

		switch(state){
			case NL: 
				state = T_state.RLT;
                bool = true;
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

        if (bool){
				obj = Client.lock_read(id);
        }
	}

	// invoked by the user program on the client node
	public void lock_write() {
		prio.lock();
        boolean bool = false;

		switch(state){
			case NL: 
				state = T_state.WLT;
                bool = true;
				break;
			case RLC:
				state = T_state.WLT;
                bool = true;
				break;
			case WLC:
				state = T_state.WLT;
				break;
			default:
				System.out.println("Choix incorrect lock_write");
				break;
		}
		prio.unlock();

        if (bool){
            obj = Client.lock_write(id);
        }
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

		cond.signal(); //signaler les fil d'attente en invalidation

		prio.unlock();

	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		
		switch(state){
			case RLT_WLC: 
				state = T_state.RLT;
				break;
			case WLT:
				
				while (state == T_state.WLT) {
					try {
						cond.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				state = T_state.RLC;
				break;
			case WLC:
				state = T_state.RLC;
				break;
			default:
				System.out.println("Choix incorrect reduce_lock");
				break;
		}
		
		return obj;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		
		switch(state){
			case RLC: 
				state = T_state.NL;
				break;
			case RLT:

				while (state == T_state.RLT) {
					try {
						cond.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				if (state == T_state.RLC) { //a quoi sert le if ?
					state = T_state.NL;
				}
				break;
            
			default:
				System.out.println("Choix incorrect invalidate_reader");
		}
		prio.unlock();
	}

	public synchronized Object invalidate_writer() {
		
		switch(state){
			case WLC:
				state = T_state.NL;
				break;
			case WLT:
				while(state==T_state.WLT||state==T_state.RLT_WLC){
					try{		   
					 	cond.await();	
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
				state = T_state.NL;
				break;
			case RLT_WLC:
				while(state==T_state.WLT||state==T_state.RLT_WLC){
					try{		   
						 cond.await();	
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
				state = T_state.NL;
				break;
			default:
				System.out.println("Choix incorrect invalidate_reader");

				
		}
		
		return obj;
	}

    public int getId() {
        return this.id;
    }

	public Object getSharedObject() {
        return this.obj;
    }
}
