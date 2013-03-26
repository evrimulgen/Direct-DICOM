package ua.ieeta.context;

public abstract class CTask implements Runnable {
	protected final ContextData data;
	
	public CTask(ContextData data) {
		this.data = data;
	}
	
	@Override
	public void run() {
		Context.setData(data);
		execute();
	}

	public abstract void execute();
}
