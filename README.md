# Factory Layout Problem

This program was designed to solve a factory floor layout problem. A parallel genetic algorithm to find the most efficient way to lay out machines
on a factory floor. The factory floor is represented by a 2D array. Each row in the matrix is its own assembly line where products would travel from left
to right. There are two types of stations on the floor: assembly station and inspection station. There are also holes allowed on the factory floor. 
The best floor is calculated based on the following metric:

Each row has its own score, and all of the row scores are added up to get the total score. 
- If two assembly stations are next to each other going from left to right, then one point is given to the row score. 
- If a hole is on either end of a row, 10 points are added to the row score.
- If a hole is next to an assembly station and not on either end, 10 points are deducted from the row score.
- If a hole is next to an inspection station and not on either end, 3 points are deducted from the row score.
- If two assembly stations are next to each other going from left to right, 1 point is added to the row score. 
- If an assembly station and an inspection station are next to each other going from left to right, 5 points is added to the row score. 



