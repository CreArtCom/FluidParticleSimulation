package ParticlesSystem;

import Simulation.Max;
import Utils.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A particle is a 2D point in a mechanical system.
 *
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class Particle
{
	/** System responsible for this particle */
	protected ParticlesSystem system;
	
	/** Random generator used to apply moment */
    protected Random generator;
	
	/** Current net force */
	protected Vector force;
	
	/** Initial position of this particle, used to apply stiffness */
	protected Vector initPos;
	
	/** "History" of the particle : the system.getMemory()'th old positions of this particle */
	protected List<Vector> oldPos;
	
	/** Current position of the particle scaled by Max.ENGINE_... */
    protected Vector position;
    
	/**
	 * Abstract constructor of a particle
	 * @param position Particle's position
	 * @param system Particle's System
	 */
    public Particle(Vector position, ParticlesSystem system)
    {
		this.system = system;
		this.generator = new Random();
		this.position = new Vector(position);
		this.initPos = new Vector(position);
		this.force = new Vector();
		this.oldPos = new ArrayList<Vector>(system.getMemory());
		clearHistory();
    }
	
	/**	Apply some random mouvement to the particle weighted by system.getMomentum() */
	protected void applyMoment()
	{
		Vector moment = new Vector(generator.nextFloat() - 0.5f, generator.nextFloat() - 0.5f);
		moment.Scalar(system.getMomentum());
		force.Add(moment);
	}
	
	/** Apply some stiffness to the particle weighted by system.getStiffness() */
	protected void applyStiffness()
	{
		Vector delta = new Vector(position);
		delta.Subtract(initPos);
		delta.Scalar(system.getStiffness());
		position.Subtract(delta);
	}
	
	/** Apply somme friction to the particle weighted by system.getFriction() */
	protected void applyFriction()
	{
		Vector temp = new Vector(force);
		temp.Scalar(system.getFriction());
		force.Subtract(temp);
	}
	
	/** Compute the new particle's position */
	public void update() 
	{
		// Apply forces
		applyMoment();
		applyStiffness();
		
		// Update position
		computeRealPosition();
		applyFriction();
		
		// On dépile le trop plein et/ou le plus vieux
		while(oldPos.size() >= system.getMemory()) {
			oldPos.remove(0);
		}

		// On empile le manquement et/ou le nouveau
		while(oldPos.size() < system.getMemory()) {
			oldPos.add(new Vector(position));
		}
	}
		/** 
	 * Check if the new virtually computed position is really reachable.
	 * If it is reachable, do nothing, otherwise, computes the farest postion reachable.
	 * A position could be unreachable by the fault of system.threshold or window borders.
	 */
	protected void computeRealPosition()
	{
		Vector netForce = new Vector(force);
		double delta = netForce.getNorm();

		// Maximum threshold is overpassed
		if(delta > system.getSeuilMax()) {
			netForce.Scalar(system.getSeuilMax() / delta);
		}
		
		// Minimum threshold is overpassed
		if(delta > system.getSeuilMin())
		{
			// New position
			position.Add(netForce);

			// Left edge reached
			if(position.x < Max.ENGINE_MIN.x) {
				position.x = system.getEdgePosition(ParticlesSystem.LEFT_EDGE, position.x);
				force.x *= system.getEdgeVelocity(ParticlesSystem.LEFT_EDGE);
			}
			// Right edge reached
			else if(position.x > Max.ENGINE_MAX.x) {
				position.x = system.getEdgePosition(ParticlesSystem.RIGHT_EDGE, position.x);
				force.x *= system.getEdgeVelocity(ParticlesSystem.RIGHT_EDGE);
			}

			// Back edge reached
			if(position.y < Max.ENGINE_MIN.y) {
				position.y = system.getEdgePosition(ParticlesSystem.BOTTOM_EDGE, position.y);
				force.y *= system.getEdgeVelocity(ParticlesSystem.BOTTOM_EDGE);
			}
			// Front edge reached
			else if(position.y > Max.ENGINE_MAX.y) {
				position.y = system.getEdgePosition(ParticlesSystem.TOP_EDGE, position.y);
				force.y *= system.getEdgeVelocity(ParticlesSystem.TOP_EDGE);
			}
		}
	}
	
	/** Clear old positions' history of this particle */
	private void clearHistory()
	{
		oldPos.clear();
		
		for(int k = 0; k < system.getMemory(); k++) {
			oldPos.add(new Vector(position));
		}
	}
    

	/**
	 * Apply a force for the next update (erase others)
	 * @param delta Force value to apply
	 */
	public void applyForce(Vector delta) {
		force = new Vector(delta);
	}

	/**
	 * Add a force for the next update (added to others)
	 * @param delta Force value to add
	 */
	public void addForce(Vector delta) {
		force.Add(delta);
	}
	
	public List<Vector> getHistory() {
		return oldPos;
	}
	
	public Vector getPosition() {
		return position;
	}
}
