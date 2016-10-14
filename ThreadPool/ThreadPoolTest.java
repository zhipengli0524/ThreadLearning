package ThreadPool;

public class ThreadPoolTest {
	public static void main(String[] args){
		DefaultThreadPool executors=new DefaultThreadPool();
		for(int i=0;i<1000;i++){
			Job job=new Job(i);
			executors.execute(job);
		}
	}
}
