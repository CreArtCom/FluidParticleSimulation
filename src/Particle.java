
import com.cycling74.jitter.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A particle is a little 2D circle in a mechanical system.
 * A particle could be free, ie without any initial attachment, or tied up.
 * Tied up particles are linked up to a grid, which means it has a spring attached to an initial position.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class Particle
{
    private JitterMatrix outMatrix;
	private ParticlesSystem particlesSystem;
    private Random generator;
	private boolean isFree;
    private int i, j;
    private float initX, initY;
	private List<Float> xHistory;
	private List<Float> yHistory;
	private float vx, vy;
	
	/**
	 * Current abscissa of the particle scaled by ParticlesSystem.ENGINE_X
	 */
    protected float x;
	
	/**
	 * Current ordinate of the particle scaled by ParticlesSystem.ENGINE_Y
	 */
	protected float y;
	
	/**
	 * Construct a grid's tied up particle.
	 * @param i Column index in the grid
	 * @param j Line index in the grid
	 * @param coordinates Initial's coordinates of the particle scaled by ParticlesSystem.ENGINE...
	 * @param particlesSystem Particle's System
	 */
    public Particle(int i, int j, float[] coordinates, ParticlesSystem particlesSystem)
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

	/**
	 * Construct a free particle.
	 * @param i Index in free particles' list
	 * @param x Abscissa of the particle scaled by ParticlesSystem.ENGINE_X
	 * @param y Ordinate of the particle scaled by ParticlesSystem.ENGINE_Y
	 * @param particlesSystem Particle's System
	 */
	public Particle(int i, float x, float y, ParticlesSystem particlesSystem)
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
	
	private void changeMemory(int memory)
	{
		xHistory.clear();
		yHistory.clear();
		
		for(int k = 0; k < memory; k++)
		{
			xHistory.add(x);
			yHistory.add(y);
		}
	}
    
	/**
	 * Compute the new particle's position
	 */
	public void update() 
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

	/**
	 * Notify the particle's position to the particle's System
	 */
	public void notifyPosition() 
    {
		if(!isFree)
			particlesSystem.setParticlePosition(i, j, x, y);
		else
		{
			Float[] xArray = new Float[xHistory.size()];
			Float[] yArray = new Float[yHistory.size()];
			
			for(int k = 0; k < xHistory.size(); k++)
			{
				xArray[k] = (float) xHistory.get(k);
				yArray[k] = (float) yHistory.get(k);
			}
			
			particlesSystem.setParticlePosition(i, xArray, yArray);
		}
    }
	
	/**
	 * Apply a force for the next update (erase others)
	 * @param dx Force value on x axis
	 * @param dy Force value on y axis
	 */
	public void applyForce(float dx, float dy) {
		vx = dx;
		vy = dy;
	}

	/**
	 * Add a force for the next update (added to others)
	 * @param dx Force value on x axis
	 * @param dy Force value on y axis
	 */
	public void addForce(float dx, float dy) {
		vx += dx;
		vy += dy;
	}
	
	/**
	 * @return	x
     * @since	1.0
     */
	float getX() {
		return x;
	}

	/**
	 * @return	y
     * @since	1.0
     */
	float getY() {
		return y;
	}
}
