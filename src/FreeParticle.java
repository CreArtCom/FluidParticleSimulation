import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A free particle ise without any initial attachment.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class FreeParticle extends Particle
{
	private List<Float> xHistory;
	private List<Float> yHistory;
	
	/**
	 * Construct a free particle.
	 * @param x Abscissa of the particle scaled by ParticlesSystem.ENGINE_X
	 * @param y Ordinate of the particle scaled by ParticlesSystem.ENGINE_Y
	 * @param particlesSystem Particle's System
	 */
	public FreeParticle(float x, float y, ParticlesSystem particlesSystem)
	{
		super(particlesSystem);
		
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
		// On applique les forces
		applyMoment();
		applyFriction();
		
		// On met à jour la position
		updatePosition();
		
		// On dépile le trop plein et/ou le plus vieux
		while(xHistory.size() >= particlesSystem.getMemory())
		{
			xHistory.remove(0);
			yHistory.remove(0);
		}

		// On empile le manquement et/ou le nouveau
		while(xHistory.size() < particlesSystem.getMemory())
		{
			xHistory.add(x);
			yHistory.add(y);
		}
	}

	public List<Float> getXHistory() {
		return xHistory;
	}

	public List<Float> getYHistory() {
		return yHistory;
	}
}
