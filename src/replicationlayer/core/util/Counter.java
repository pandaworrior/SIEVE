package replicationlayer.core.util;
import replicationlayer.core.util.Debug;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

public class Counter{

    int count;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    public Counter(){
	count = 0;
    }

    public int increment(){
        writeLock().lock();
        try{
            return ++count;
        }finally{writeLock().unlock();}
    }

    public int decrement(){
        writeLock().lock();
        try{
            return --count;
        }finally{writeLock().unlock();}
    }   

    public boolean isZero(){
        readLock().lock();
        try{
            return count == 0;
        }finally{readLock().unlock();}
    }

    public String toString(){
	return ""+count;
    }
    
    public Lock readLock(){
        return lock.readLock();
    }
    
    public Lock writeLock(){
        return lock.writeLock();
    }
    
}