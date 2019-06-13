import java.util.Random;
import java.util.ArrayList;
public class Algorithm {

    /* GA parameters */
    private static double mutationRate;
	private static double mutationDeviation; 
	private static double successRate;
	private static int    tournamentRounds;
	public static double fittest = 20000;
	

    /* Public methods */
    
    // Evolve a population
    public static Matrix evolvePopulation(Matrix pop)throws Exception {
		
		Random r = new Random();
		double[] indiv = new double[291];
		System.arraycopy(pop.row(0), 0, indiv, 0, 291);
		if(Game.getFitness(indiv)>0) fittest = Game.getFitness(indiv);
		
		
		double mR = pop.row(1)[291];
		double mD = pop.row(1)[292];
		double sR = pop.row(1)[293];
		int    tR = (int)pop.row(1)[294];
		
		if(mR>0.09&&mR<0.5) 
		{
			mutationRate = mR;
		}
		if(mD>0.05&& mD<0.5)
		{	
			mutationDeviation = mD;
		}
		
		if(sR>0.5&&sR<1)
		{	
			successRate = sR;
		}
		if(tR>0&&tR<10)
		{
			tournamentRounds = tR;
		}
		
		mutate(pop); 
		
		tournament(pop);  
		
        return pop;
    }

	
    //Crossover individuals
    private static double [] crossover(double[] indiv1, double[] indiv2) {
		Random r = new Random();
        double[] newSol = new double[291];
		for(int k=0; k<291; k++)
		{
			newSol[k] = r.nextBoolean()?indiv1[k]:indiv2[k];
		}
        return newSol;
    }

    // Mutate an individual
    private static void mutate(Matrix pop) throws Exception
	{
		Random r = new Random();
       //mutation
		for(int j=1; j<pop.rows(); j++)
		{
			double[] indiv = new double[291];
			System.arraycopy(pop.row(j), 0, indiv, 0, 291);
			if(r.nextDouble() < mutationRate)
			{
				int k = r.nextInt(294);
				pop.row(j)[k] += r.nextGaussian()*mutationDeviation;
			}
		}
    }

	public static void tournament(Matrix pop) throws Exception
	{
			Random r = new Random();
			int rand1 = r.nextInt(100);
			int rand2 = r.nextInt(100);
			
			double[] w1 = pop.row(rand1);
			double[] w2 = pop.row(rand2);
			
			double[] indiv1 = new double[291];
			System.arraycopy(w1, 0, indiv1, 0, 291);
			double[] indiv2 = new double[291];
			System.arraycopy(w2, 0, indiv2, 0, 291);
			
			double[] father = null;
			double[] mother = null;
			
			int fitness1 = Game.getFitness(indiv1);
			int fitness2 = Game.getFitness(indiv2);
			
			if(fitness1 > 0 && fitness2 > 0) //if both winners
			{
				
				if(fitness1 < fitness2) //if 1 is better than 2
				{
					
					if(r.nextDouble()<successRate) //with successRate chance make 1 live and kill 2
					{
						if(fitness1 < fittest)
						{
							pop.swapRows(rand1, 0);
						}
						father = pop.row(r.nextInt(100));
						mother = pop.row(r.nextInt(100));
						indiv2 = crossover(father, mother);	
					}
					else
					{
						if(fitness2 < fittest)
						{
							pop.swapRows(rand2, 0);
						}
						father = pop.row(r.nextInt(100));
						mother = pop.row(r.nextInt(100));
						indiv1 = crossover(father, mother);	
					}
				}
				
				else if(fitness1 > fitness2)
				{
					
					if(r.nextDouble()<successRate)
					{
						if(fitness2 < fittest)
						{
							pop.swapRows(rand2, 0);
						}
						father = pop.row(r.nextInt(100));
						mother = pop.row(r.nextInt(100));
						indiv1 = crossover(father, mother);	
					}
					else
					{
						if(fitness1 < fittest)
						{
							pop.swapRows(rand1, 0);
						}
						father = pop.row(r.nextInt(100));
						mother = pop.row(r.nextInt(100));
						indiv2 = crossover(father, mother);	
					}
				}
				
			}
			
			
			else if(fitness1 > 0 && fitness2 <= 0)
			{
				
				if(r.nextDouble()<successRate)
				{
					
					if(fitness1 < fittest)
					{
						pop.swapRows(rand1, 0);
					}
					father = pop.row(r.nextInt(100));
					mother = pop.row(r.nextInt(100));
					indiv2 = crossover(father, mother);	
				}
				else
				{
					father = pop.row(r.nextInt(100));
					mother = pop.row(r.nextInt(100));
					indiv1 = crossover(father, mother);	
				}
			}
			else if(fitness1 <= 0 && fitness2 > 0)
			{
				
				if(r.nextDouble()<successRate)
				{
					if(fitness2 < fittest)
					{
						pop.swapRows(rand2, 0);
					}
					father = pop.row(r.nextInt(100));
					mother = pop.row(r.nextInt(100));
					indiv1 = crossover(father, mother);	
				}
				else
				{
					father = pop.row(r.nextInt(100));
					mother = pop.row(r.nextInt(100));
					indiv2 = crossover(father, mother);	
				}
			}
			
			else
			{
				father = pop.row(r.nextInt(100));
				mother = pop.row(r.nextInt(100));
				indiv1 = crossover(father, mother);	
				indiv2 = crossover(father, mother);	
			}
		
			
	
	}
}