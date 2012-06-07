import java.util.Random;

/**
 * A particle is a little 2D circle in a mechanical system.
 *
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public abstract class Particle
{
	protected ParticlesSystem particlesSystem;
    protected Random generator;
	protected float vx, vy;
	
	/**
	 * Current abscissa of the particle scaled by ParticlesSystem.ENGINE_X
	 */
    protected float x;
	
	/**
	 * Current ordinate of the particle scaled by ParticlesSystem.ENGINE_Y
	 */
	protected float y;
    
	/**
	 * Abstract constructor of a particle
	 * @param particlesSystem Particle's System
	 */
    public Particle(ParticlesSystem particlesSystem)
    {
		this.particlesSystem = particlesSystem;
		generator = new Random();
    }
	
	// On applique un grain de sel
	protected void applyMoment()
	{
		vx += (generator.nextFloat() - 0.5f) * particlesSystem.getMomentum();
		vy += (generator.nextFloat() - 0.5f) * particlesSystem.getMomentum();
	}
	
	// On applique les frottements
	protected void applyFriction()
	{
		vx -= particlesSystem.getFriction() * vx;
		vy -= particlesSystem.getFriction() * vy;
	}
	
	// On met à jour la position
	protected void updatePosition()
	{
		x += Math.abs(vx) > particlesSystem.getSeuil() ? vx : 0;
		y += Math.abs(vy) > particlesSystem.getSeuil() ? vy : 0;
		
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
