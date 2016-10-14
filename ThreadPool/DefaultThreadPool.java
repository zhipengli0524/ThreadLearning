package ThreadPool;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job>{
	private static final int MAX_WORKER_NUMBER=10;
	private static final int MIN_WORKER_NUMBER=1;
	private static final int DEFAULT_WORKER_NUMBER=5;
	public  LinkedList<Job> jobs = new LinkedList<Job>();
	private final List<Worker> workers= Collections.synchronizedList(new ArrayList<Worker>());
	private int workerNum= DEFAULT_WORKER_NUMBER;
	private AtomicLong threadNum=new AtomicLong();	
	public DefaultThreadPool(){
		initializeWorkers(DEFAULT_WORKER_NUMBER);
	}
	
	public DefaultThreadPool(int num){
		if(num>MAX_WORKER_NUMBER) num=MAX_WORKER_NUMBER;
		else if(num<MIN_WORKER_NUMBER) num=MIN_WORKER_NUMBER;
		initializeWorkers(num);
		workerNum=num;
	}
	
	public void execute(Job job){
		synchronized(jobs){
			jobs.addLast(job);
			jobs.notify();
		}
	}
		
	public void addWorkers(int num){
		synchronized(jobs){
			if(num+this.workers.size()>MAX_WORKER_NUMBER)
				num=MAX_WORKER_NUMBER;
			initializeWorkers(num);
			workerNum=num;
		}
	}
	
	public void removeWorkers(int num){
		synchronized(jobs){
			if(num>=workerNum)
				throw new IllegalArgumentException("beyond workNum");
			int count=0;
			while(count<num){
				Worker worker=workers.get(count);
				if(workers.remove(worker)){
					worker.shutdown();
					count++;
				}
			}
			workerNum-=count;
		}
	}
	
	public int getJobSize(){
		return workerNum;
	}
	
	public void shutdown(){
		for(Worker worker:workers){
			worker.shutdown();
			
		}
	}
	
	private void initializeWorkers(int num){
		for(int i=0;i<num;i++){
			Worker worker = new Worker(jobs);
			workers.add(worker);
			Thread thread=new Thread(worker,"ThreadPool-Worker-"+threadNum.incrementAndGet());
			thread.start();
		}
	}
	
}

class  Worker implements Runnable{
	private volatile boolean running=true;
	private LinkedList<Job> jobs;
	Worker(LinkedList jobs){
		this.jobs=(LinkedList<Job>)jobs;
	}
	public void run(){
		while(running){
			Job job=null;
			synchronized(jobs){
				while(jobs.isEmpty()){
					try{
						jobs.wait();
					}catch(InterruptedException e){
						Thread.currentThread().interrupt();
						return;
					}	
				}
				job=jobs.removeFirst();
				if(job!=null){
					job.run();
				}
			}
		}	
	}
	public void shutdown(){
		running=false;
	}
}

class Job implements Runnable{
	int jobID;
	Job(int jobID){
		this.jobID=jobID;
	}
	public void run(){
		System.out.println("job "+jobID+" executed");
	}
}