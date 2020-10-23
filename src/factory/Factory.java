package factory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;


public class Factory implements Runnable {
	private final int STATIONNUMBER;
	private final int WIDTH;
	private final int HEIGHT;
	private Station [][] factory;
	Exchanger <Station [][]> exchanger;
	CountDownLatch latch;
	private int score;
	GUI gui;
	
	public Factory(int stationNumber, int width, int height, CountDownLatch gate, GUI gui) {
		STATIONNUMBER = stationNumber;
		WIDTH = width;
		HEIGHT = height;
		latch = gate;
		factory = new Station [width][height];
		//exchanger = ex;
		this.gui = gui;
	}
	
	/*
	 * metric for genetic algorithm:
	 * think of flow rate
	 * each row is like an assembly line for a factory
	 * each "product" must pass through at least one inspection station on its way out
	 * a line is valid if a product passes through at least one inspection station and at least 3 assembly stations
	 * otherwise the line has a score of 0
	 * valid lines' scores are calculated by adding the number of assembly stations up
	 * final score is the sum of all scores for rows
	 * 
	 */
	
	//can't use atomic variables with locks! rewrite!!
	@Override
	public void run() {
		initFactory();
		score = 0;
		for(int i = 0; i<100; i++) {
			int row = ThreadLocalRandom.current().nextInt(WIDTH);
			int col = ThreadLocalRandom.current().nextInt(HEIGHT);
			for(int r = 0; r<WIDTH; r++) {
				for(int c = 0; c<HEIGHT; c++) {
					//if to-be-swapped is on the left side of pair
					if(c % 2 == 0) {
						//if station at loop generated coordinates benefits from swap
						if(doubleScore(r, c, r, c+1) < doubleScore(row, col, r, c+1)) {
							//if station at random coordinates is on left side of pair
							if(col % 2 == 0) {
								//if station at random coordinates benefits from swap
								if(doubleScore(row, col, row, col+1) < doubleScore(r, c, row, col+1)) {
									Station temp = factory[r][c];
									factory[r][c] = factory[row][col];
									factory[row][col] = temp;
								}
							}
							else {
								//if station at random coordinates benefits from swap.
								if(doubleScore(row, col-1, row, col) < doubleScore(row, col-1, r, c)) {
									Station temp = factory[r][c];
									factory[r][c] = factory[row][col];
									factory[row][col] = temp;
								}
							}
						}
					}
					//if to-be-swapped is on the left side of pair
					else {
						if(doubleScore(r, c-1, r, c)<doubleScore(r, c-1, row, col)) {
							if(col % 2 == 0) {
								if(doubleScore(row, col, row, col+1) < doubleScore(r, c, row, col+1)) {
									Station temp = factory[r][c];
									factory[r][c] = factory[row][col];
									factory[row][col] = temp;
								}
							}
							else {
								if(doubleScore(row, col-1, row, col) < doubleScore(row, col-1, r, c)) {
									Station temp = factory[r][c];
									factory[r][c] = factory[row][col];
									factory[row][col] = temp;
								}
							}
						}
					}
				}
			}
		}
		score = calcScore(factory);
		latch.countDown();
		//swap();
	}
	
	public void setLatch(CountDownLatch nl) {
		latch = nl;
	}
	
	
	public int doubleScore(int lrow, int lcol, int rrow, int rcol){
		try {
			if(factory[lrow][lcol] == null && factory[rrow][rcol] == null) {
				if(lcol == 0 || rcol == 5) {
					return 10;
				}
				return 0;
			}
			else if(factory[lrow][lcol] == null) {
				if(lcol == 0) {
					return 10;
				}
				else if(factory[rrow][rcol].getFlavor() == 1) {
					return -10;
				}
				else if(factory[rrow][rcol].getFlavor() == 2) {
					return -3;
				}
			}
			else if(factory[rrow][rcol] == null) {
				if(rcol == 5) {
					return 10;
				}
				else if(factory[lrow][lcol].getFlavor() == 1) {
					return -10;
				}
				else if(factory[lrow][lcol].getFlavor() == 2) {
					return -3;
				}
			}
			else if(factory[lrow][lcol].getFlavor() == 1 && factory[rrow][rcol].getFlavor()==1) {
				return 1;
			}
			else if(factory[lrow][lcol].getFlavor() == 1 && factory[rrow][rcol].getFlavor()==2) {
				return 5;
			}
			return 0;
		}
		catch(NullPointerException e) {
			System.out.println(lrow +"\t" + lcol+"\t"+rrow+"\t"+rcol);
			System.out.println(toString());
			e.printStackTrace();
		}
		return 0;
	}
	
	//rewrite
	public void initFactory() {
		factory = new Station [WIDTH][HEIGHT];
	    int stationCount = (STATIONNUMBER - (STATIONNUMBER/4));
	    int inspectionCount = STATIONNUMBER/4;
		int holeCount = ((WIDTH*HEIGHT)-STATIONNUMBER);
		//System.out.println(Thread.currentThread().getId());
		for(int rows = 0; rows<WIDTH; rows++) {
			for(int col = 0; col<HEIGHT; col++) {
				for(;;) {
					int rand = ThreadLocalRandom.current().nextInt(3);
					//System.out.println(rand);
					if(rand == 0 && stationCount != 0) {
						factory[rows][col] = new Station("assembly"+stationCount, 1);
						stationCount--;
						break;
					}
					else if(rand == 1 && inspectionCount != 0) {
						factory[rows][col] = new Station("inspection"+inspectionCount, 2);
						inspectionCount--;
						break;
					}
					else {
						if(holeCount!=0) {
							holeCount--;
							break;
						}
					}
				}
			}
		}
		//toString();
	}
	
	public Station [][] getFactory() {
		return factory;
	}
	
	public void setFactory(Station [][] newFac) {
		factory = newFac;
	}
		
	//rewrite tomorrow!!
	public int calcScore(Station [][] fac) {
		int rowScore = 0;
		int totalScore = 0;
		for(int r = 0; r < WIDTH; r++) {
			for(int c = 0; c < HEIGHT-1; c+=2) {
				if(fac[r][c] == null || fac[r][c+1] == null) {
					if(fac[r][c] == null) {
						if(c == 0) {rowScore += 10;}
						else if(fac[r][c+1] != null && fac[r][c+1].getFlavor() == 1) {
							rowScore -= 10;
						}
						else if(fac[r][c+1] != null && fac[r][c+1].getFlavor() == 2) {
							rowScore -= 3;
						}
					}
					else if(fac[r][c+1] == null){
						if(c+1 == 5) {rowScore += 10;}
						else if(fac[r][c] != null && fac[r][c].getFlavor() == 1) {
							rowScore -= 10;
						}
						else if(fac[r][c] != null && fac[r][c].getFlavor() == 2) {
							rowScore -= 3;
						}
					}
				}			
				else if(fac[r][c].getFlavor() == 1 && fac[r][c+1].getFlavor() == 1)rowScore++;
				else if(fac[r][c].getFlavor() == 1 && fac[r][c+1].getFlavor() == 2)rowScore+=5;	
			}
			totalScore += rowScore;
			rowScore = 0;
		}
		//System.out.println(score);
		return totalScore;
	}
	
	public int getScore() {return score;}
	
	public void setScore(int newScore) {
		score = newScore;
	}
	
	public void swap() {
		try {
			if(latch.getCount() == 0) {
				Station [][] tempFac = exchanger.exchange(factory);
				int newScore = calcScore(tempFac);
				System.out.println(Thread.currentThread().getId() + "comparing "+ newScore + " " + score);
				if(score < newScore) {
					//System.out.println("Going Here!!!");
					factory = tempFac;
					score = newScore;
				}
			}
		}
		catch(InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}
	
	@Override
	public String toString() {
		String print = "";
		for(int row = 0; row<WIDTH; row++) {
			for(int col = 0; col<HEIGHT; col++) {
				if(factory[row][col] != null) {
					print += factory[row][col].getName() + " ";
				}
				else {
					print += "Hole ";
				}
			}
			print += "\n\n";
		}
		return print;		
	}
}
