package WaitNotify;

import java.text.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
public class WaitNotify {
	public static Object lock=new Object();
	public static volatile boolean flag=true;
	public static void main(String[] args){
		Thread waitTh=new Thread(new Wait(), "waitThread");
		Thread notifyTh=new Thread(new Notify(),"notifyThread");
		waitTh.start();
		SleepUtils.second(1);
		notifyTh.start();
	}
	static class Wait implements Runnable{
		public void run(){
			synchronized(lock){
				while(flag){
					try{
						System.out.println(Thread.currentThread().getName()+" wait @"
							+new SimpleDateFormat("hh:mm:ss").format(new Date()));
							lock.wait();
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					System.out.println(Thread.currentThread().getName()+" wake up @"
					    +new SimpleDateFormat("hh:mm:ss").format(new Date()));
				}
			}
		}
	}
	static class Notify implements Runnable{
		public void run(){
			synchronized(lock){
				System.out.println(Thread.currentThread().getName()+" hold lock @"
			       +new SimpleDateFormat("hh:mm:ss").format(new Date()));
				flag=false;
				lock.notifyAll();
				SleepUtils.second(2);
			}
			synchronized(lock){
				System.out.println(Thread.currentThread().getName()+" hold lock "
						+ "again @"+new SimpleDateFormat("hh:mm:ss").format(new Date()));
				SleepUtils.second(2);
			}
		}
	}
}

class SleepUtils{
	public static final void second(long seconds){
		try{
			TimeUnit.SECONDS.sleep(seconds);
		}catch(InterruptedException e){}
	}
}

