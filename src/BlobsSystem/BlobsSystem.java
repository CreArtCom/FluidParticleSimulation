package BlobsSystem;

import Simulation.Simulation;
import Utils.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class BlobsSystem
{
	protected static final int CIRCLEBRUSH = 0;
	protected static final int RECTANGLEBRUSH = 1;
	
	// Attributes
	/** Map of all blobs : blob_index => blob */
	protected Map<Integer, Blob> blobs;
	
	/** List of last updated blob indexes since last call to getUpdatedBlobs() */
	protected List<Integer> updatedIndexes;
	
	/** Object responsible for the simulation */
	protected Simulation simulation;
	
	/**
	 * BlobsSystem constructor
	 * @param simulation Object responsible for the simulation
	 */
	public BlobsSystem(Simulation simulation) {
		this.simulation		= simulation;
		this.blobs			= new HashMap<Integer, Blob>();
		this.updatedIndexes	= new ArrayList<Integer>();
	}
	
	/**
	 * Getter : UpdatedBlobs
	 * @return	A list of all blobs that have moved since the last call
     * @since	1.0
     */
	protected List<Blob> getUpdatedBlobs()
	{
		List<Blob> result = new ArrayList<Blob>();
		
		for(Integer index : updatedIndexes)
		{
			Blob blob = blobs.get(index);
			if(blob.hasMoved())
				result.add(blob);
		}
		
		updatedIndexes.clear();
		
		return result;
	}
	
	/**
	 * Getter : blobMouvements
	 * @return	A list of all blob's mouvements since last UpdatedBlobs() call
     * @since	1.0
     */
	public Map<Blob, List<Vector[]>> getBlobsMouvements()
	{
		Map<Blob, List<Vector[]>> result = new HashMap<Blob, List<Vector[]>>();
		
		for(Blob blob : getUpdatedBlobs())
			result.put(blob, blob.getMouvements());
		
		return result;
	}

	public List<Blob> getBlobs(int index)
	{
		List<Blob> result = new ArrayList<Blob>();
		
		if(index == -1) {
			result.addAll(blobs.values());
		}
		else {
			if(!blobs.containsKey(index))
				blobs.put(index, new Blob());
			result.add(blobs.get(index));
		}
		
		return result;
	}
	
	public void setRectangleBrush(int index, double width, double height)
	{
		for(Blob blob : getBlobs(index))
		{
			Brush brush = blob.getBrush();

			if(brush instanceof RectangleBrush) {
				((RectangleBrush) brush).setWidth(width);
				((RectangleBrush) brush).setHeight(height);
			}
			else
				simulation.printOut("Blob n°" + index + " has no a Rectangle Brush.");
		}
	}
	
	public void setCircleBrush(int index, double radius)
	{
		for(Blob blob : getBlobs(index))
		{
			Brush brush = blob.getBrush();

			if(brush instanceof CircleBrush)
				((CircleBrush) brush).setRadius(radius);
			else
				simulation.printOut("Blob n°" + index + " has no a Circle Brush.");
		}
	}

	public void setThreshold(int index, double min, double max)
	{
		for(Blob blob : getBlobs(index))
			blob.setThreshold(min, max);
	}

	public void setAdder(int index, int nbToAdd)
	{
		for(Blob blob : getBlobs(index))
		{
			blob.setAdder(nbToAdd);

			if(blob.applyEraser() && blob.getToAdd() > 0)
				simulation.printOut("Warning : It does not seem brilliant to add and delete particles at the same time for blob.");
		}
	}

	public void setApplyEraser(int index, boolean apply)
	{
		for(Blob blob : getBlobs(index))
		{
			blob.setApplyEraser(apply);

			if(blob.applyEraser() && blob.getToAdd() > 0)
				simulation.printOut("Warning : It does not seem brilliant to add and delete particles at the same time.");
		}
	}

	public void setApplyAttractivity(int index, boolean apply)
	{
		for(Blob blob : getBlobs(index))
		{
			blob.setApplyAttractivity(apply);

			if(blob.applyAttractivity() && blob.applyForce())
				simulation.printOut("Warning : You are applying attractivity and force at the same time.");
		}
	}

	public void setApplyForce(int index, boolean apply)
	{
		for(Blob blob : getBlobs(index))
		{
			blob.setApplyForce(apply);

			if(blob.applyAttractivity() && blob.applyForce())
				simulation.printOut("Warning : You are applying attractivity and force at the same time.");
		}
	}

	public void setBrush(int index, int brushIndex)
	{
		for(Blob blob : getBlobs(index))
		{
			switch(brushIndex)
			{
				case CIRCLEBRUSH:
					blob.setBrush(new CircleBrush());
					break;

				case RECTANGLEBRUSH:
					blob.setBrush(new RectangleBrush());
					break;

				default:
					simulation.printOut("Unknown brush type for blob n°" + index + ".");
			}
		}
	}

	public void setAttractiveForce(int index, float force)
	{
		for(Blob blob : getBlobs(index))
			blob.setAttractiveForce(force);
	}

	public void setForce(int index, float force)
	{
		for(Blob blob : getBlobs(index))
			blob.setForce(force);
	}

	public String getInfo(int index)
	{
		if(!blobs.containsKey(index))
			return "There is no blob n°" + index + ".";

		return getBlobs(index).toString();
	}

	public String getList()
	{
		if(blobs.isEmpty())
			return "Currently, there is no blob in this simulation.";

		String result = "BlobsIndexes";

		for(Map.Entry<Integer, Blob> pair : blobs.entrySet())
			result += " " + pair.getKey();

		return result;
	}

	public void reset() {
		blobs.clear();
		updatedIndexes.clear();
	}

	public Blob setPosition(int index, Vector position)
	{
		Blob blob = null;
		
		if(blobs.containsKey(index))
		{
			blob = blobs.get(index);
			blob.Move(position);
		}
		else
			blobs.put(index, new Blob(position));
		
		if(!updatedIndexes.contains(index))
			updatedIndexes.add(index);
		
		return blob;
	}
}
