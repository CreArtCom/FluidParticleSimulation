package MagnetsSystem;

import ParticlesSystem.Particle;
import Simulation.Particles;
import Utils.Line;
import Utils.Vector;
import java.util.HashMap;
import java.util.Map;

/**
 * A magnets' System is a mechanical 3D system which contains magnets.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class MagnetsSystem
{
	/** Current list of all magnets in this system */
	protected Map<Integer, Magnet> magnets;
	
	/** Object responsible for the simulation */
    protected Particles simulation;
	
	/** Determine if magnets are appliable on particles */
	protected boolean enable;

	public MagnetsSystem(Simulation.Particles simulation)
	{
		this.simulation	= simulation;
		this.magnets	= new HashMap<Integer, Magnet>();
		this.enable		= false;
	}
	
	public String getInfo(int index)
	{
		Magnet magnet = getMagnet(index);
		
		if(magnet == null)
			return "There is no magnet n°" + index + ".";
		else
			return magnet.toString();
	}
	
	public String listIndexes()
	{
		if(magnets.isEmpty())
			return "Currently, there is no magnet in this simulation.";
		
		String result = "MagnetsIndexes";
		
		for(Map.Entry<Integer, Magnet> pair : magnets.entrySet())
			result += " " + pair.getKey();
		
		return result;
	}

	/**
	 * Apply every attractives forces for each magnet on this particle
	 * @param particle Particle on which apply magnets
	 */
	public void apply(Particle particle)
	{
		if(enable)
		{
			for(Map.Entry<Integer, Magnet> pair : magnets.entrySet())
				pair.getValue().applyMagnetForce(particle);
		}
	}
	
	/**	Remove all magnets */
	public void resetMagnets() {
		magnets.clear();
	}

	/**
	 * Delete a magnet by its index
	 * @param index Index of the magnet to delete
	 */
	public void deleteMagnet(int index) {
		if(magnets.containsKey(index))
			magnets.remove(index);
		else
			simulation.printOut("Unable to remove : There is no magnet n°" + index);
	}

	public void setMagnetForce(int index, float newForce)
	{
		if(index == -1)
			for(Map.Entry<Integer, Magnet> pair : magnets.entrySet())
				pair.getValue().setForce(newForce);
		
		else
		{
			if(magnets.containsKey(index))
				magnets.get(index).setForce(newForce);
			else
				simulation.printOut("Unable to change force : There is no magnet n°" + index);
		}
	}

	public Magnet getMagnet(int index) {
		if(magnets.containsKey(index))
			return magnets.get(index);
		return null;
	}

	public void setMagnet(int index, Magnet magnet) {
		magnets.put(index, magnet);
	}

	public void setPointMagnet(int index, Vector position, double force) {
		setMagnet(index, new PointMagnet(position, force));
	}

	public void setLineMagnet(int index, Line line, double force) {
		setMagnet(index, new LineMagnet(line, force));
	}

	/**
	 * Determine if magnets forces are applied on particles
	 * @return <code>true</code> if magnets forces are applied on particles, 
	 * <code>false</code> otherwise
	 */
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
