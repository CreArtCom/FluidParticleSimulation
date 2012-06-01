/**
 * Something about Licence
 * 
 * @author		Léo LEFEBVRE
 * @version		1.0
 */

import com.cycling74.jitter.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Particle
{
    JitterMatrix outMatrix;
	ParticlesSystem particlesSystem;
    Random generator;
	boolean isFree;
    
    int i, j;
    float initX, initY;
	List<Float> xHistory;
	List<Float> yHistory;
    float x, y;
	float vx, vy;

	// Constructeur pour les particules liées à la grille
    Particle(int i, int j, float[] coordinates, ParticlesSystem particlesSystem)
    {
		this.particlesSystem = particlesSystem;
		generator = new Random();
		
		isFree = false;
		
		this.x = coordinates[0];
        this.y = coordinates[1];
		this.initX = x;
        this.initY = y;
		
        this.i = i;
        this.j = j;
		
		particlesSystem.setParticleInitPosition(i, j, initX, initY);
    }

	// Constructeur pour les particules libres
	Particle(int i, float x, float y, ParticlesSystem particlesSystem)
	{
		isFree = true;
		this.particlesSystem = particlesSystem;
		generator = new Random();
		
		this.i = i;
		this.x = x;
        this.y = y;
		
		int memory = particlesSystem.getMemory();
		this.xHistory = new ArrayList<Float>(memory);
        this.yHistory = new ArrayList<Float>(memory);
		changeMemory(memory);
	}
	
	public final void changeMemory(int memory)
	{
		xHistory.clear();
		yHistory.clear();
		for(int k = 0; k < memory; k++)
		{
			xHistory.add(x);
			yHistory.add(y);
		}
	}
    
	void update() 
	{
		// On applique un grain de sel
		vx += (generator.nextFloat() - 0.5f) * particlesSystem.getMomentum();
		vy += (generator.nextFloat() - 0.5f) * particlesSystem.getMomentum();

		// On applique le ressort sur les particules liées à la grille
		if(!isFree)
		{
			float dx = (x - initX);
			float dy = (y - initY);
			vx -= particlesSystem.getStiffness() * dx;
			vy -= particlesSystem.getStiffness() * dy;
		}

		// On applique les frottements
		vx -= particlesSystem.getFriction() * vx;
		vy -= particlesSystem.getFriction() * vy;
		
		// On met à jour la position
		x += Math.abs(vx) > particlesSystem.getSeuil() ? vx : 0;
		y += Math.abs(vy) > particlesSystem.getSeuil() ? vy : 0;
		
		if(isFree)
		{
			if(xHistory.size() == particlesSystem.getMemory())
			{
				xHistory.remove(0);
				yHistory.remove(0);
			}
			xHistory.add(x);
			yHistory.add(y);
		}

		// bounce of edges
		if(x < 0) {
			x = 0;//x - vx;
			//vx *= -1;
		}
		else if(x > 1) {
			x = 1;//(1 - x) - vx;
			//vx *= -1;
		}

		if(y < 0) {
			y = 0;//y - vy;
			//vy *= -1;
		}
		else if(y > 1) {
			y = 1;//(1 - y) - vy;
			//vy *= -1;
		}

		// hackish way to make particles glitter when the slow down a lot
//		if(vx * vx + vy * vy < 1) {
//			vx = 2.f*generator.nextFloat()-1.f;
//			vy = 2.f*generator.nextFloat()-1.f;
//		}
	}

    void setNewPosition() 
    {
		if(!isFree)
			particlesSystem.setParticlePosition(i, j, x, y);
		else
		{
			Float[] xArray = new Float[xHistory.size()];
			Float[] yArray = new Float[yHistory.size()];
			int memory = particlesSystem.getMemory();
			
			for(int k = 0; k < xHistory.size(); k++)
			{
				xArray[k] = (float) xHistory.get(k);
				yArray[k] = (float) yHistory.get(k);
			}
			
			particlesSystem.setParticlePosition(i, xArray, yArray);
		}
    }
	

	void Move(float dx, float dy) {
		vx = dx;
		vy = dy;
	}

	/********************************* GETTERS *********************************/
	
	/**
	 * Getter : x
	 * @return	Position courante de la particule sur l'axe X normalisée entre 
	 *			ParticlesSystem.ENGINE_X[0]	et ParticlesSystem.ENGINE_X[1]
     * @since	1.0
     */
	float getX() {
		return x;
	}

	/**
	 * Getter : y
	 * @return	Position courante de la particule sur l'axe Y normalisée entre 
	 *			ParticlesSystem.ENGINE_Y[0]	et ParticlesSystem.ENGINE_Y[1]
     * @since	1.0
     */
	float getY() {
		return y;
	}
}
