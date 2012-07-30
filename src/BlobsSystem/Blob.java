package BlobsSystem;

import Utils.Vector;
import java.util.ArrayList;
import java.util.List;

/**
 * A blob is something that interfere with the simulation.
 * A blob is located in a 2D plane by its center.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class Blob
{
	/** Blob's last center coordinate scaled into ParticlesSystem.ENGINE_X */
	protected Vector position;
	
	/** Difference between the two last blob's position */
	protected Vector delta;
	
	/** List of all delta values since the last call to getMouvements() */
	protected List<Vector> deltaList;
	
	/** List of all positions since the last call to getMouvements() */
	protected List<Vector> positionList;
	
	/** Force of this blob : use to weight delta */
	protected double force;
	
	/** Attractive Force of this blob */
	protected double attractiveForce;
	
	/** A Brush can be attached to a blob in order to interract with environnement */
	protected Brush brush;
	
	/** Force is a mode in which you can "push" something in contact with the blob (using the brush) */
	protected boolean applyForce;
	
	/** Erase is a mode in which you can erase or remove something in contact with the blob (using the brush) */
	protected boolean applyEraser;
	
	/** Attractivity is a mode in which you can attract something in contact with the blob (using the brush) */
	protected boolean applyAttractivity;
	
	/** Add permits to add <code>toAdd</code> of something each time the blob appears. */
	protected int toAdd;
	
	/** Threshold to determine if movement is significant and not to much between to iterations. Vector2(min, max) */
	protected Vector threshold;
	
	/**
	 * Construct a blob from normalized coordinates
     * @param position	Blob's center scaled in MSG_XSCALE
     * @since			1.0
     */
	public Blob(Vector position)
	{
		this.position			= new Vector(position);
		this.delta				= new Vector();
		this.positionList		= new ArrayList<Vector>();
		this.deltaList			= new ArrayList<Vector>();
		this.force				= 0.f;
		this.brush				= new CircleBrush();
		this.applyEraser		= false;
		this.applyForce			= false;
		this.applyAttractivity	= false;
		this.attractiveForce	= 0;
		this.toAdd				= 0;
		this.threshold			= new Vector(0., 1.);
	}

	/**
	 * Default constructor : construct a blob initialized at (0,0)
     * @since	2.0
	 */
	public Blob() {
		this(new Vector());
	}

	/**
	 * Move a blob to a new position
	 * @param newPosition	New blob's position
	 */
	public void Move(Vector newPosition)
	{
		Vector newDelta = new Vector(newPosition).Subtract(position);
		double norm = newDelta.getNorm();

		// Maximum threshold is overpassed
		if(norm > threshold.y) {
			newPosition.Scalar(threshold.y / norm);
		}
		
		// Minimum threshold is overpassed
		if(norm > threshold.x) {
			delta = newDelta;
			position = new Vector(newPosition);
			positionList.add(position);
			deltaList.add(delta);
		}
	}
	
	/**
	 * Determine if the blob has moved by using a threshold
	 * Significant means in threshold boundaries (using threshold)
	 * @return <code>true</code> if the blob's last movements are significants, otherwise <code>false</code>
	 */
	public boolean hasMoved() {
		return !positionList.isEmpty();
	}

	/**
	 * Get current position
	 * @return	position
     * @since	1.0
     */
	public Vector getPosition() {
		return position;
	}

	/**
	 * Get delta's value
	 * @return	delta Weighted by force
     * @since	1.0
     */
	public Vector getDelta() {
		return delta.Scalar(force);
	}
	
	/**
	 * List all mouvements of this blob since the last call of this method.
	 * This method helps to treat asynchronous blobs (event based).
	 * Mouvements are describes as a position (x, y) and a deplacement (deltaX, deltaY).
	 * Please note that deltas are weighted by simulation.blobForce()
	 * @return {..., [position(i), delta(i)], ...} for every i since last call
	 */
	public List<Vector[]> getMouvements()
	{
		List<Vector[]> result = new ArrayList<Vector[]>();
		int size = positionList.size();
		
		for(int i = 0; i < size; i++)
		{
			Vector[] mouvement = {positionList.remove(0), deltaList.remove(0).Scalar(force)};
			result.add(mouvement);
		}
		
		return result;
	}

	/**
	 * Set the force value
	 * @param force New force
	 * @since 2.0
	 */
	public void setForce(double force) {
		this.force = force;
	}

	/**
	 * Set the brush
	 * @param brush New brush
	 * @since 2.0
	 */
	public void setBrush(Brush brush) {
		this.brush = brush;
	}
	
	/**
	 * Set the toAdd value
	 * @param toAdd New toAdd value
	 * @since 2.0
	 */
	public void setAdder(int toAdd) {
		this.toAdd = toAdd;
	}
	
	/**
	 * Get the brush
	 * @return Current brush (could be null)
	 * @since 2.0
	 */
	public Brush getBrush() {
		return this.brush;
	}

	/**
	 * Determine if the erase mode is enable
	 * @return <code>true</code> if the erase mode is enable, <code>else</code> otherwise
	 * @since 2.0
	 */
	public boolean applyEraser() {
		return applyEraser;
	}

	/**
	 * Enable/Disable the erase mode
	 * @param applyForce <code>true</code> for enable the erase mode, <code>else</code> otherwise
	 */
	public void setApplyEraser(boolean applyEraser) {
		this.applyEraser = applyEraser;
	}

	/**
	 * Determine if the force mode is enable
	 * @return <code>true</code> if the force mode is enable, <code>else</code> otherwise
	 */
	public boolean applyForce() {
		return applyForce;
	}

	/**
	 * Enable/Disable the force mode
	 * @param applyForce <code>true</code> for enable the force mode, <code>else</code> otherwise
	 */
	public void setApplyForce(boolean applyForce) {
		this.applyForce = applyForce;
	}
	
	/**
	 * Determine if the attractivity mode is enable
	 * @return <code>true</code> if the attractivity mode is enable, <code>else</code> otherwise
	 */
	public boolean applyAttractivity() {
		return applyAttractivity;
	}

	/**
	 * Enable/Disable the attractive mode
	 * @param applyAttractivity <code>true</code> for enable the attractive mode, <code>else</code> otherwise
	 */
	public void setApplyAttractivity(boolean applyAttractivity) {
		this.applyAttractivity = applyAttractivity;
	}
	
	/**
	 * Get the toAdd value
	 * @return Current toAdd value
	 * @since 2.0
	 */
	public int getToAdd() {
		return toAdd;
	}

	/**
	 * Set the threshold
	 * @param min New threshold min
	 * @param max New threshold max
	 * @since 2.0
	 */
	public void setThreshold(double min, double max) {
		this.threshold = new Vector(min, max);
	}
	
	/**
	* Get the current attractive force
	* @return Current attractive force
	* @since 2.0
	*/
	public double getAttractiveForce() {
		return attractiveForce;
	}

	/**
	* Set the attractive force
	* @param attractiveForce New attractive force
	* @since 2.0
	*/
	public void setAttractiveForce(double attractiveForce) {
		this.attractiveForce = attractiveForce;
	}
	
	/**
	 * Return a string describing the blob.
	 * @return The describing string
	 */
	@Override
	public String toString() {
		return "Blob " + brush.toString();
	}
}
