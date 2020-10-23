package factory;

import java.util.concurrent.Exchanger;

public class Mutator implements Runnable {
	Factory parent;
	Exchanger<Factory> exchanger;

	public Mutator(Factory parent, Exchanger<Factory> ex) {
		this.parent = parent;
		exchanger = ex;
	}
	
	@Override
	public void run() {
		//swaps elements
		try {
			Factory tempFac = exchanger.exchange(parent);
			System.out.println(Thread.currentThread().getId() + " comparing "+ tempFac.getScore() + " " + parent.getScore());
			if(parent.getScore() < tempFac.getScore()) {
				parent.setFactory(tempFac.getFactory());
				parent.setScore(tempFac.getScore());
			}
		}
		catch(InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}
}
