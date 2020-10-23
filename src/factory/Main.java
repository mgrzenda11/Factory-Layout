package factory;

//import javax.swing.Timer;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main {
	static ExecutorService pool = Executors.newFixedThreadPool(32);
	static CountDownLatch endGate;
	static ReentrantLock lock = new ReentrantLock();
	static Exchanger<Factory> ex = new Exchanger<>();
	static Timer timer;
	static GUI gui = new GUI();
	
	public static void main(String[] args) throws InterruptedException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.createAndShowGUI();
			}
		});
		geneticAlg();
	}
	
	public static void startGenAlg() throws InterruptedException {
		geneticAlg();
	}
	
	public static void geneticAlg() throws InterruptedException {
		Factory best = null;
		Factory[] shitTon = new Factory[32];
		for(int i = 0; i<32; i++) {
			shitTon[i] = new Factory(32, 6, 6, endGate, gui);
		}
		
		for(int r = 0; r<100; r++) {
			endGate = new CountDownLatch(32);
			for(int j = 0; j<shitTon.length; j++) {
				shitTon[j].setLatch(endGate);
				//shitTon[j].setScore(0);
				pool.execute(shitTon[j]);
			}
			
			endGate.await();
			
			if(best == null) best = shitTon[0];
			
			int index = 0;
			int bestScore = 0;
			for(int j = 0; j<shitTon.length; j++) {
				if(shitTon[j].getScore() > bestScore) {
					bestScore = shitTon[j].getScore();
					index = j;
				}
			}
			
			
			Thread.currentThread().sleep(500);
			gui.updateGUI(shitTon[index].getFactory());
			
			if(best != null && shitTon[index].getScore()>best.getScore()) {
				best = copyFactory(shitTon[index]);
			}
		
			
			for(int m = 0; m<shitTon.length; m++) {
				if(shitTon[m].getScore()<bestScore) {
					shitTon[m].setFactory(shitTon[index].getFactory());
					shitTon[m].setScore(bestScore);
				}
			}
		}
		gui.updateGUI(best.getFactory());
		System.out.println("Best Score: "+best.getScore());
		pool.shutdown();
	}
	
	public static Factory copyFactory(Factory fac) {
		Factory temp = new Factory(32, 6, 6, endGate, gui);
		temp.setFactory(fac.getFactory());
		temp.setScore(fac.getScore());
		Station [][] tempFac = temp.getFactory();
		for(int row = 0; row<tempFac.length; row++) {
			for(int col = 0; col<tempFac[0].length; col++) {
				temp.getFactory()[row][col] = fac.getFactory()[row][col];
			}
		}
		return temp;
	}
}

