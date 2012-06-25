package ParticlesSystem;

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
		float delta = (float) Math.sqrt((vx * vx) + (vy * vy));
		
		// Seuil max
		//vx = Math.abs(vx) < particlesSystem.getSeuilMax() ? vx : (vx / (float)Math.abs(vx) * particlesSystem.getSeuilMax());
		//vy = Math.abs(vy) < particlesSystem.getSeuilMax() ? vy : (vy / (float)Math.abs(vy) * particlesSystem.getSeuilMax());
		if(delta > particlesSystem.getSeuilMax())
		{
			if(vy == 0.f)
				vx = Math.signum(vx) * particlesSystem.getSeuilMax();
			else
			{
				float ratio = vx / vy;
				vy = (float) (Math.signum(vy) * particlesSystem.getSeuilMax() / Math.sqrt((ratio * ratio) + 1));
				vx = ratio * vy;
			}
		}
		
		// Seuil min
		//vx = Math.abs(vx) > particlesSystem.getSeuilMin() ? vx : 0.f;
		//vy = Math.abs(vy) > particlesSystem.getSeuilMin() ? vy : 0.f;
		if(delta < particlesSystem.getSeuilMin())
		{
			vx = 0;
			vy = 0;
		}
		
		x += vx;
		y += vy;
		
		// Left edge
		if(x < 0) {
			x = particlesSystem.getEdgePosition(ParticlesSystem.LEFT_EDGE, x);
			vx *= particlesSystem.getEdgeVelocity(ParticlesSystem.LEFT_EDGE);
		}
		// Right edge
		else if(x > 1) {
			x = particlesSystem.getEdgePosition(ParticlesSystem.RIGHT_EDGE, x);
			vx *= particlesSystem.getEdgeVelocity(ParticlesSystem.RIGHT_EDGE);
		}

		// Bottom edge
		if(y < 0) {
			y = particlesSystem.getEdgePosition(ParticlesSystem.BOTTOM_EDGE, y);
			vy *= particlesSystem.getEdgeVelocity(ParticlesSystem.BOTTOM_EDGE);
		}
		// Top edge
		else if(y > 1) {
			y = particlesSystem.getEdgePosition(ParticlesSystem.TOP_EDGE, y);
			vy *= particlesSystem.getEdgeVelocity(ParticlesSystem.TOP_EDGE);
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
	 * Alias : Add a force for the next update (added to others)
	 * @param delta Force value ([0] on x axis, [1] on y axis)
	 */
	public void addForce(float[] delta) {
		addForce(delta[0], delta[1]);
	}
	
	/**
	 * Alias : Apply a force for the next update (erase others)
	 * @param delta Force value ([0] on x axis, [1] on y axis)
	 */
	public void applyForce(float[] delta) {
		applyForce(delta[0], delta[1]);
	}
	
	/**
	 * @return	x
     * @since	1.0
     */
	public float getX() {
		return x;
	}

	/**
	 * @return	y
     * @since	1.0
     */
	public float getY() {
		return y;
	}
}
