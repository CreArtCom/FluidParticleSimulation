package ParticlesSystem;

/**
 * A tied up particles is linked up to a grid, which means it has a spring 
 * attached to an initial position.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class GridParticle extends Particle
{
    private int i, j;
    private float initX, initY;
	
	/**
	 * Construct a grid's tied up particle.
	 * @param i Column index in the grid
	 * @param j Line index in the grid
	 * @param coordinates Initial's coordinates of the particle scaled by ParticlesSystem.ENGINE...
	 * @param particlesSystem Particle's System
	 */
    public GridParticle(int i, int j, float[] coordinates, ParticlesSystem particlesSystem)
    {
		super(particlesSystem);
		
		this.x = coordinates[0];
        this.y = coordinates[1];
		this.initX = x;
        this.initY = y;
		
        this.i = i;
        this.j = j;
		
		particlesSystem.setParticleInitPosition(i, j, initX, initY);
    }
    
	// On applique le ressort sur les particules liées à la grille
	protected void applyStiffness()
	{
		float dx = (x - initX);
		float dy = (y - initY);
		vx -= particlesSystem.getStiffness() * dx;
		vy -= particlesSystem.getStiffness() * dy;
	}
	
	/**
	 * Compute the new particle's position
	 */
	public void update() 
	{
		// On applique les forces
		applyMoment();
		applyStiffness();
		applyFriction();
		
		// On met à jour la position
		updatePosition();
	}

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}
}
