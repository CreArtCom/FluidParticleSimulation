package Simulation;

import BlobsSystem.BlobsSystem;
import Utils.Vector;
import com.cycling74.max.Atom;

/**
 * Simulation is a Max's abstraction that provide blobs system for it's children
 * Please note that Simulation is an abstract class and each children will be 
 * an instantiable Max Object.
 * 
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public abstract class Simulation extends Max
{
	// Common Blobs settings messages
	/** Blob's reset message : ~ */
	protected static String MSG_BLOB_RESET = "blob_reset";
	
	/** Blob's threshold message : ~ blob_index min max */
	protected static String MSG_BLOB_THRESHOLD = "blob_seuil";
	
	/** Blob's position message : ~ blob_index x y */
	protected static String MSG_BLOB_POSITION = "blob";
	
	/** Blob's force message : ~ blob_index force */
	protected static String MSG_BLOB_FORCE = "blob_force";
	
	/** Blob's attractive's force message : ~ blob_index attractive_force */
	protected static String MSG_BLOB_ATTRACTIVE_FORCE = "blob_attractive_force";
	
	/** Blob's circle brush message : ~ blob_index radius */
	protected static String MSG_BLOB_CIRCLE = "blob_circle";
	
	/** Blob's rectangle brush message : ~ blob_index width height */
	protected static String MSG_BLOB_RECTANGLE = "blob_rectangle";
	
	/** Blob's apply eraser message : ~ blob_index apply_eraser */
	protected static String MSG_BLOB_APPLY_ERASER = "blob_apply_eraser";
	
	/** Blob's apply force message : ~ blob_index apply_force */
	protected static String MSG_BLOB_APPLY_FORCE = "blob_apply_force";
	
	/** Blob's apply attractivity message : ~ blob_index apply_attractivity */
	protected static String MSG_BLOB_APPLY_ATTRACTIVITY = "blob_apply_attractivity";
	
	/** Blob's toAdd message : ~ blob_index toAdd */
	protected static String MSG_BLOB_ADD = "blob_add";
	
	/** Blob's toAdd message : ~ */
	protected static String MSG_BLOB_LIST = "blob_list";

	/** Blob's toAdd message : ~ blob_index */
	protected static String MSG_BLOB_INFO = "blob_info";
	
	/** Blob's toAdd message : ~ blob_index brush */
	protected static String MSG_BLOB_BRUSH = "blob_brush";
	
	// Attributes
	protected BlobsSystem blobsSystem;
	
	/**
	 * Abstract constructor.
	 * Initialise its members.
	 * @param args Max Object's  arguments - not use but required
	 */
	public Simulation(Atom[] args) {
		super(args);
		blobsSystem = new BlobsSystem(this);
	}
	
	/**
	 * Execute appropriates routines for known messages
	 * Call first Max.TreatMessage.
	 * @param message Max's header message
	 * @param args Max's parameters message
	 * @return <code>true</code> if the message in unknown, <code>false</code> otherwise
	 */
	@Override
    protected boolean TreatMessage(String message, Atom[] args)
    {
		if(super.TreatMessage(message, args))
			return false;
		
		boolean unknownMessage = false;
		
        // Messages de paramétrage
		if(args.length == 0)
		{
			if(message.contentEquals(MSG_BLOB_RESET))
				blobsSystem.reset();
			
			else if(message.contentEquals(MSG_BLOB_LIST))
				outBlob(blobsSystem.getList());

			else
				unknownMessage = true;
		}
		
		else if(args.length == 1)
		{
			if(message.contentEquals(MSG_BLOB_INFO))
				outBlob(blobsSystem.getInfo(args[0].toInt()));
			
			else
				unknownMessage = true;
		}

		
		else if(args.length == 2)
		{			
			if(message.contentEquals(MSG_BLOB_FORCE))
				blobsSystem.setForce(args[0].toInt(), args[1].toFloat());
			
			else if(message.contentEquals(MSG_BLOB_ATTRACTIVE_FORCE))
				blobsSystem.setAttractiveForce(args[0].toInt(), args[1].toFloat());
			
			else if(message.contentEquals(MSG_BLOB_BRUSH))
				blobsSystem.setBrush(args[0].toInt(), args[1].toInt());
			
			else if(message.contentEquals(MSG_BLOB_CIRCLE))
				blobsSystem.setCircleBrush(args[0].toInt(), args[1].toDouble());
			
			else if(message.contentEquals(MSG_BLOB_APPLY_ERASER))
				blobsSystem.setApplyEraser(args[0].toInt(), args[1].toBoolean());
			
			else if(message.contentEquals(MSG_BLOB_APPLY_FORCE))
				blobsSystem.setApplyForce(args[0].toInt(), args[1].toBoolean());
			
			else if(message.contentEquals(MSG_BLOB_APPLY_ATTRACTIVITY))
				blobsSystem.setApplyAttractivity(args[0].toInt(), args[1].toBoolean());
			
			else if(message.contentEquals(MSG_BLOB_ADD))
				blobsSystem.setAdder(args[0].toInt(), args[1].toInt());
				
			else
				unknownMessage = true;
		}
		
		else if(args.length == 3)
		{			
			if(message.contentEquals(MSG_BLOB_THRESHOLD))
				blobsSystem.setThreshold(args[0].toInt(), args[1].toDouble(), args[2].toDouble());
			
			else if(message.contentEquals(MSG_BLOB_RECTANGLE))
				blobsSystem.setRectangleBrush(args[0].toInt(), args[1].toDouble(), args[2].toDouble());
			
			else if(message.contentEquals(MSG_BLOB_POSITION))
				applyBlob(args[0].toInt(), new Vector(args[1].toDouble(), args[2].toDouble()));
			
			else
				unknownMessage = true;
		}
		else
			unknownMessage = true;
		
		return unknownMessage;
    }
	
	/**
	 * Define whatever to do when a blob occurs.
	 * Abstract : Must be overrode
	 * @param index Blob's index
	 * @param position Position on which blob occurs
	 */
	protected abstract void applyBlob(int index, Vector position);
	
	protected abstract void outBlob(String message);
}
