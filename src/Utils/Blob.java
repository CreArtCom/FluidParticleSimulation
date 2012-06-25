package Utils;

import Simulation.AbstractMax;
import java.util.ArrayList;
import java.util.List;

/**
 * A blob is something that interfere with the simulation.
 * Blobs are located in 2D planes.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class Blob
{
	/**
	 * Blob's center abscissa scaled into ParticlesSystem.ENGINE_X
	 */
	protected float x;
	
	/**
	 * Blob's center ordinate scaled into ParticlesSystem.ENGINE_X
	 */
	protected float y;
	
	/**
	 * Difference between the two last blob's abcissas
	 */
	protected float deltaX;
	
	/**
	 * Difference between the two last blob's ordinates
	 */
	protected float deltaY;
	
	/**
	 * Object responsible for conducting the simulation, contains few parameters
	 */
	protected AbstractMax fluidParticleSimulation;

	private List<Float> deltaXList;
	private List<Float> deltaYList;
	private List<Float> xList;
	private List<Float> yList;
	
	/**
	 * Construct a blob from normalized coordinates
     * @param x				Blob's center abscissa scaled in MSG_XSCALE
	 * @param y				Blob's center ordinate scaled in MSG_YSCALE
	 * @param simulation	Object responsible for conducting the simulation
     * @since				1.0
     */
	public Blob(float x, float y, AbstractMax simulation)
	{
		this.fluidParticleSimulation = simulation;
		float[] norms = scale(x, y);
		this.x = norms[0];
		this.y = norms[1];
		
		xList = new ArrayList<Float>();
		yList = new ArrayList<Float>();
		deltaXList = new ArrayList<Float>();
		deltaYList = new ArrayList<Float>();
	}

	/**
	 * Move a blob to a new position
	 * @param posX	New blob's abscissa scaled in MSG_XSCALE
	 * @param posY	New blob's ordinate scaled in MSG_YSCALE
	 */
	public void Move(float posX, float posY)
	{
		boolean updated = false;
		float[] norms = scale(posX, posY);
		
		float dX = norms[0] - x;
		dX = Math.abs(dX) > fluidParticleSimulation.getBlobSeuilMax() ? (dX / (float)Math.abs(dX) * fluidParticleSimulation.getBlobSeuilMax()) : dX;
		
		float dY = norms[1] - y;
		dY = Math.abs(dY) > fluidParticleSimulation.getBlobSeuilMax() ? (dY / (float)Math.abs(dY) * fluidParticleSimulation.getBlobSeuilMax()) : dY;
		
		// Le déplacement en X n'est pas inférieur au seuil
		if(Math.abs(dX) > fluidParticleSimulation.getBlobSeuilMin())
		{
			deltaX = dX;
			x = norms[0];
			updated = true;
		}

		// Le déplacement en Y n'est pas inférieur au seuil
		if(Math.abs(dY) > fluidParticleSimulation.getBlobSeuilMin())
		{
			deltaY = dY;
			y = norms[1];
			updated = true;
		}

		// L'une des coordonnées au moins a été mise à jour
		if(updated)
		{
			xList.add(x);
			yList.add(y);
			deltaXList.add(deltaX);
			deltaYList.add(deltaY);
		}
	}
	
	private float[] scale(float posX, float posY)
	{
		float[] xCoefs = fluidParticleSimulation.getXCoefs();
		float[] yCoefs = fluidParticleSimulation.getYCoefs();
		
		return new float[]{((xCoefs[0] * posX) + xCoefs[1]), ((yCoefs[0] * posY) + yCoefs[1])};
	}
	
	/**
	 * Determine if the blob has moved by using a threshold
	 * @return <code>true</code> if the blob's last movements are significants, otherwise <code>false</code>
	 */
	public boolean hasMoved() {
		return xList.size() > 0;//Math.abs(getDeltaX()) > fluidParticleSimulation.getBlobSeuilMin() || Math.abs(getDeltaY()) > fluidParticleSimulation.getBlobSeuilMin();
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
	
	/**
	 * @return	deltaX weighted by fluidParticleSimulation.blobForce
     * @since	1.0
     */
	public float getDeltaX() {
		return deltaX * fluidParticleSimulation.getBlobForce();
	}

	/**
	 * @return	deltaY weighted by fluidParticleSimulation.blobForce
     * @since	1.0
     */
	public float getDeltaY() {
		return deltaY * fluidParticleSimulation.getBlobForce();
	}
	
	/**
	 * 
	 * @return 
	 */
	public List<Float[]> getMouvements()
	{
		List<Float[]> result = new ArrayList<Float[]>();
		int size = xList.size();
		
		for(int i = 0; i < size; i++)
		{
			Float[] mouvement = {xList.remove(0), yList.remove(0), deltaXList.remove(0) * fluidParticleSimulation.getBlobForce(), deltaYList.remove(0) * fluidParticleSimulation.getBlobForce()};
			result.add(mouvement);
		}
		
		return result;
	}
}
