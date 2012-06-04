import com.cycling74.max.Atom;
import com.cycling74.max.MaxObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class FluidParticleSimulation extends MaxObject
{
	// Bornes coordonnées GL
	protected static float[] GL_X = {-1.f, 1.f};
	protected static float[] GL_Y = {-1.f, 1.f};
	
    // Définition des messages d'entrée
    protected static String MSG_MATRIX		= "jit_matrix";
	protected static String MSG_FLUIDDIM	= "fluid_dim";
	protected static String MSG_BLOBSEUIL	= "blob_seuil";
	protected static String MSG_BLOB		= "blob";
	protected static String MSG_BLOBFORCE	= "blob_force";
	protected static String MSG_XSCALE		= "x_scale";
	protected static String MSG_YSCALE		= "y_scale";
	protected static String MSG_RESET		= "reset";
	
    // Paramètres par défaut
    protected static final float BLOB_SEUIL		= 0.1f;
	protected static final float BLOB_FORCE		= 0.5f;
	protected static final float BLOB_RADIUS	= 0.5f;
	protected static final float BLOB_WIDTH		= 0.5f;
	protected static final float BLOB_HEIGHT	= 0.5f;
	protected static final float[] XCOEFS		= {0.f, 1.f};
	protected static final float[] YCOEFS		= {0.f, 1.f};	
	
	// Attributs
	protected Map<Integer, Blob> blobs;
	protected List<Integer> updatedIndexes;
	protected float blobSeuil;
	protected float blobForce;
	protected float[] xCoefs;
	protected float[] yCoefs;
	
	public FluidParticleSimulation(Atom[] args)
	{
		// Initialisation des Attributs
		blobSeuil		= BLOB_SEUIL;
		blobForce		= BLOB_FORCE;
		xCoefs			= XCOEFS;
		yCoefs			= YCOEFS;
		blobs			= new HashMap<Integer, Blob>();
		updatedIndexes	= new ArrayList<Integer>();
		
		createInfoOutlet(true);
	}
	
	public void printOut(String msg)
	{
		outlet(getInfoIdx(), msg);
	}
	
	protected void unknownMessage(String message, Atom[] args)
	{
		String argsError = "";

		for(Atom atom : args)
			argsError += " " + atom.toString();

		error("Unknown message : " + message + argsError);
	}
	
    protected boolean TreatMessage(String message, Atom[] args)
    {
		boolean unknownMessage = false;
		
        // Messages de paramétrage
        if(args.length == 1)
        {
			if(message.contentEquals(MSG_BLOBSEUIL))
                blobSeuil = args[0].toFloat();
			
			else if(message.contentEquals(MSG_BLOBFORCE))
                blobForce = args[0].toFloat();
			
			else
				unknownMessage = true;
        }
		
		else if(args.length == 2)
		{
			if(message.contentEquals(MSG_XSCALE))
				xCoefs = computeCoefs(args[0].toFloat(), args[1].toFloat(), ParticlesSystem.ENGINE_X);
			
			else if(message.contentEquals(MSG_YSCALE))
				yCoefs = computeCoefs(args[0].toFloat(), args[1].toFloat(), ParticlesSystem.ENGINE_Y);
			
			else
				unknownMessage = true;
		}
		
		else if(args.length == 3)
		{
			if(message.contentEquals(MSG_BLOB))
			{
				if(!updatedIndexes.contains(args[0].toInt()))
					updatedIndexes.add(args[0].toInt());
				applyBlob(args[0].toInt(), args[1].toFloat(), args[2].toFloat());
			}
			
			else
				unknownMessage = true;
		}
		
		else
			unknownMessage = true;
		
		return unknownMessage;
    }
		
	protected void applyBlob(int index, float posX, float posY) {
		// Doit être surchargée
	}
	
	// Alias
	protected static float[] computeCoefs(float[] from, float[] to) {
		return computeCoefs(from[0], from[1], to[0], to[1]);
	}
	
	// Alias
	protected static float[] computeCoefs(float minFrom, float maxFrom, float[] to) {
		return computeCoefs(minFrom, maxFrom, to[0], to[1]);
	}

	// Alias
	protected static float[] computeCoefs(float[] from, float minTo, float maxTo) {
		return computeCoefs(from[0], from[1], minTo, maxTo);
	}
	
	// Calcule les coefs A et B dans y = A * x + B pour un scale linéaire
	protected static float[] computeCoefs(float minFrom, float maxFrom, float minTo, float maxTo) {
		float A, B;
		
		if(minFrom == maxFrom || minTo == maxTo)
		{
			A = 0;
			B = minTo;
		}
		// minFrom != 0 && minFrom != maxFrom
		else if(minFrom != 0)
		{
			float r = maxFrom / minFrom;
			B = ((maxTo - (minTo*r))/(1 - r));
			A = ((minTo - B) / minFrom);
		}
		// minFrom == 0 && minFrom != maxFrom
		else
		{
			B = minTo;
			A = ((maxTo - B) / maxFrom);
		}
		
		return new float[]{A, B};
	}
	
	public List<Blob> getUpdatedBlobs()
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
	
	public List<Float[]> getBlobsMouvements()
	{
		List<Float[]> result = new ArrayList<Float[]>();
		
		for(Blob blob : getUpdatedBlobs())
			result.addAll(blob.getMouvements());
		
		return result;
	}
	
	/********************************* GETTERS *********************************/
	
	/**
	 * Getter : blobSeuil
	 * @return	La valeur du seuil minimal de détection du mouvement d'un blob
     * @since	1.0
     */
	public float getBlobSeuil() {
		return blobSeuil;
	}

	/**
	 * Getter : blobForce
	 * @return	La valeur de force pondérant le déplacement d'un blob (facteur)
     * @since	1.0
     */
	public float getBlobForce() {
		return blobForce;
	}
	
	/**
	 * Getter : xCoefs
	 * @return	Les coefficients en X utilisés pour normaliser les positions des
	 *			blobs : msg_x_scale -> ParticlesSystem.ENGINE_X
     * @since	1.0
     */
	float[] getXCoefs() {
		return xCoefs;
	}

	/**
	 * Getter : yCoefs
	 * @return	Les coefficients en Y utilisés pour normaliser les positions des
	 *			blobs : msg_y_scale -> ParticlesSystem.ENGINE_Y
     * @since	1.0
     */
	float[] getYCoefs() {
		return yCoefs;
	}
}
