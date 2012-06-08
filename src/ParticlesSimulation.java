import com.cycling74.max.*;
import com.cycling74.jitter.*;

/**
 * @author	CreArtCom's Studio
 * @author	Léo LEFEBVRE
 * @version	1.0
 */
public class ParticlesSimulation extends FluidParticleSimulation
{
    // Définition des messages d'entrée
	protected static String MSG_BLOBRADIUS	= "blob_radius";
	protected static String MSG_BLOBWIDTH	= "blob_width";
	protected static String MSG_BLOBHEIGHT	= "blob_height";
	protected static String MSG_BLOBFORM	= "blob_form";
    protected static String MSG_STIFFNESS	= "stiffness";
    protected static String MSG_MOMENTUM	= "momentum";
	protected static String MSG_PARTSEUIL	= "seuil";
	protected static String MSG_NBPARTICLES	= "particles";
	protected static String MSG_FRICTION	= "friction";
	protected static String MSG_FLUIDFORCE	= "fluid_force";
	protected static String MSG_FLUIDAPPLY	= "fluid_apply";
	protected static String MSG_ADDPART		= "add_particles";
	protected static String MSG_ADDFORCE	= "add_force";
	protected static String MSG_MEMORY		= "memory";
	protected static String MSG_FREEMAX		= "free_max";
	protected static String MSG_FREEGEN		= "free_gen";
	protected static String MSG_FREEERASER	= "free_erase";
	protected static String MSG_EDGES		= "edges";
	
	// Paramètres par défaut
    protected static final float	FLUID_FORCE			= 0.6f;
    protected static final boolean	FLUID_FORCE_APPLY	= false;
    protected static final boolean	BLOB_FORCE_APPLY	= false;
    protected static final boolean	BLOB_ERASER_APPLY	= false;
    protected static final int		PART_NBTOADD		= 0;
	
	// Outlets
	protected static final int	OUTLET_MGRID	= 0;
	protected static final int	OUTLET_MFREE	= 2;
	protected static final int	OUTLET_MINIT	= 1;
	
	
	// Attributs
	private float fluidForce;
    private ParticlesSystem particlesSystem;
	private MaxObject fluidSimulation;
	private int fluidCells;
	private int fluidWidth;
	private int fluidHeight;
	private float[] fluidU;
	private float[] fluidV;
	private float stepWidth;
	private float stepHeight;
	private int particlesToAdd;
	private boolean applyFluidForce;
	private boolean applyBlobForce;
	private boolean applyAttractivity = true;
	private boolean applyBlobEraser;
	
    // Jitter Objects
    JitterMatrix outGridMatrix;
    JitterMatrix outFreeMatrix;
	JitterMatrix initGridMatrix;
	JitterMatrix nowhereMatrix;
	
    public ParticlesSimulation(Atom[] args)
    {
		super(args);
		
        // 2 inputs - 3 outputs
        declareIO(2, 3);
        setInletAssist(0, "Bang and setting's messages");
		setInletAssist(1, "Matrix of initials free particles");
        setOutletAssist(0, "Matrix of deplaced points in grid");
		setOutletAssist(1, "Matrix of initial points' positions in grid");
        setOutletAssist(2, "Matrix of deplaced free points");
		
		// Initialisation des matrices
		outGridMatrix	= new JitterMatrix(2, "float32", 0, 0);
		outFreeMatrix	= new JitterMatrix(2, "float32", 0, 0);
		initGridMatrix	= new JitterMatrix(2, "float32", 0, 0);
		nowhereMatrix	= new JitterMatrix(2, "float32", 1, 1);
		nowhereMatrix.setcell2d(0, 0, new float[]{Float.MAX_VALUE, Float.MAX_VALUE});
		
		// Initialisation des Attributs
		fluidForce		= FLUID_FORCE;
		applyFluidForce	= FLUID_FORCE_APPLY;
		applyBlobForce	= BLOB_FORCE_APPLY;
		fluidSimulation = null;
		particlesToAdd	= PART_NBTOADD;
		applyBlobEraser	= BLOB_ERASER_APPLY;

    	// On créer le système de particules
		particlesSystem = new ParticlesSystem(this);
		particlesSystem.setCircleRadius(BLOB_RADIUS).setBoxHeight(BLOB_HEIGHT).setBoxWidth(BLOB_WIDTH);
    }
 
    @Override
    protected void bang()
    {
        // Message de Bang sur l'inlet 0
        if(getInlet() == 0)
        {
			if(applyFluidForce)
			{
				fluidU = fluidSimulation.getAttrFloatArray("fluid_u");
				fluidV = fluidSimulation.getAttrFloatArray("fluid_v");
			}
			
			particlesSystem.update();
			
			if(particlesSystem.hasFreeParticles())
				outlet(OUTLET_MFREE, MSG_MATRIX, outFreeMatrix.getName());
			
			if(particlesSystem.hasGridParticles())
				outlet(OUTLET_MGRID, MSG_MATRIX, outGridMatrix.getName());
        }
    }
	
    @Override
    // Contrôles les messages d'entrée (bon message sur bonne entrée)
    // Ne peut pas recevoir de bang (voir méthode bang)
    protected void anything(String message, Atom[] args)
    {
		boolean unknownMessage = false;
		
		if(TreatMessage(message, args))
		{
			if(getInlet() == 0)
			{
				// Messages de paramétrage
				if(args.length == 0)
				{
					if(message.contentEquals(MSG_RESET))
					{
						particlesSystem.reset();
						outlet(OUTLET_MINIT, MSG_MATRIX, initGridMatrix.getName());
					}

					else if(message.contentEquals(MSG_FREEGEN))
						particlesSystem.genFreeParticles();

					else
						unknownMessage = true;
				}

				// Messages de paramétrage
				else if(args.length == 1)
				{			
					if(message.contentEquals(MSG_STIFFNESS))
						particlesSystem.setStiffness(args[0].toFloat());

					else if(message.contentEquals(MSG_MOMENTUM))
						particlesSystem.setMomentum(args[0].toFloat());

					else if(message.contentEquals(MSG_FRICTION))
						particlesSystem.setFriction(args[0].toFloat());

					else if(message.contentEquals(MSG_BLOBRADIUS))
						particlesSystem.setCircleRadius(args[0].toFloat());

					else if(message.contentEquals(MSG_BLOBWIDTH))
						particlesSystem.setBoxWidth(args[0].toFloat());

					else if(message.contentEquals(MSG_BLOBHEIGHT))
						particlesSystem.setBoxHeight(args[0].toFloat());

					else if(message.contentEquals(MSG_BLOBFORM))
						particlesSystem.setBrush(args[0].toInt());

					else if(message.contentEquals(MSG_FLUIDFORCE))
						fluidForce = args[0].toFloat();

					else if(message.contentEquals(MSG_FLUIDAPPLY))
					{
						fluidSimulation = MaxObject.getContext().getMaxObject("FluidSimulation");
						applyFluidForce = args[0].toBoolean() && fluidSimulation != null;
						particlesSystem.setApplyFluidForce(applyFluidForce);
					}

					else if(message.contentEquals(MSG_PARTSEUIL))
						particlesSystem.setThreshold(args[0].toFloat());

					else if(message.contentEquals(MSG_ADDPART))
						particlesToAdd = args[0].toInt();

					else if(message.contentEquals(MSG_ADDFORCE))
						applyBlobForce = args[0].toBoolean();

					else if(message.contentEquals(MSG_MEMORY))
						particlesSystem.setMemory(args[0].toInt());

					else if(message.contentEquals(MSG_FREEMAX))
						particlesSystem.setMaxFreeParticles(args[0].toInt());

					else if(message.contentEquals(MSG_FREEERASER))
						applyBlobEraser = args[0].toBoolean();

					else
						unknownMessage = true;

				}

				else if(args.length == 2)
				{
					if(message.contentEquals(MSG_NBPARTICLES))
					{
						setGridMatrixDim(args[0].toInt(), args[1].toInt());
						particlesSystem.setNbParticles(args[0].toInt(), args[1].toInt());
					}

					else if(message.contentEquals(MSG_FLUIDDIM))
					{
						fluidWidth	= args[0].toInt();
						fluidHeight	= args[1].toInt();
						fluidCells	= fluidWidth * fluidHeight;
						stepWidth	= 1.f / (float) fluidWidth;
						stepHeight	= 1.f / (float) fluidHeight;
					}

					else
						unknownMessage = true;
				}
				
				else if(args.length == 4)
				{
					if(message.contentEquals(MSG_EDGES))
						particlesSystem.setEdges(args[0].toInt(), args[1].toInt(), args[2].toInt(), args[3].toInt());
					
					else
						unknownMessage = true;
				}

				else
					unknownMessage = true;
			}
			else if(getInlet() == 1)
			{
				if(args.length == 1 && message.contentEquals(MSG_MATRIX))
				{
					JitterMatrix jm = new JitterMatrix(args[0].toString());
					particlesSystem.loadFreeParticles(jm);
				}
				
				else
					unknownMessage = true;
			}
		}
		
		if(unknownMessage)
			unknownMessage(message, args);
    }
    
	@Override
	protected void applyBlob(int index, float posX, float posY)
	{
		// On récupère le blob existant et on applique la différence de positions
		if(blobs.containsKey(index))
		{
			Blob blob = blobs.get(index);
			blob.Move(posX, posY);
			
			// On ajoute des particles
			if(particlesToAdd > 0)
				particlesSystem.addFreeParticles(blob.getX(), blob.getY(), particlesToAdd);
		}
		
		// On initialise un nouveau blob
		else
			blobs.put(index, new Blob(posX, posY, this));
	}
	
	protected float[] applyFluid(float posX, float posY)
	{
		int w = (int) Math.floor(posX / stepWidth);
		int h = (int) Math.floor(posY / stepHeight);
		int index = ( h * fluidWidth) + w;

		if(index < fluidCells)
			return new float[]{fluidU[index] * fluidForce, fluidV[index] * fluidForce};
		else
			return new float[]{0, 0};
	}

    @Override
    public void notifyDeleted()
    {
		outGridMatrix.freePeer();
		outFreeMatrix.freePeer();
		initGridMatrix.freePeer();
		particlesSystem.destroy();
    }

	public boolean applyBlobForce() {
		return applyBlobForce && blobForce != 0.f;
	}
	
	public boolean applyAttractivity() {
		return applyAttractivity;
	}
	
	boolean applyBlobEraser() {
		return applyBlobEraser;
	}
	
	/********************************* GETTERS *********************************/

	/**
	 * Getter : outGridMatrix
	 * @return	Matrice contenant le centre des particules liés à la grille 
	 *			déplacés après une itération du système.
     * @since	1.0
     */
	public JitterMatrix getGridMatrix() {
		return outGridMatrix;
	}

	/**
	 * Getter : outFreeMatrix
	 * @return	Matrice contenant le centre des particules libres déplacés après
	 *			une itération du système.
     * @since	1.0
     */
	public JitterMatrix getFreeMatrix() {
		return outFreeMatrix;
	}
    
	/**
	 * Getter : 
	 * @return	Matrice contenant le centre des particules liés à la grille 
	 *			avant toute itération du système (position initiale).
     * @since	1.0
     */
	public JitterMatrix getInitMatrix() {
		return initGridMatrix;
	}

	void setFreeMatrixDim(int width, int height)
	{
		if(width > 0 && height > 0)
			outFreeMatrix.setDim(new int[]{width, height});
		else
		{
			outFreeMatrix.clear();
			outFreeMatrix.setDim(new int[]{0, 0});
			outlet(OUTLET_MFREE, MSG_MATRIX, nowhereMatrix.getName());
		}
	}

	void setGridMatrixDim(int width, int height)
	{
		if(width > 0 && height > 0)
		{
			outGridMatrix.setDim(new int[]{width, height});
			initGridMatrix.setDim(new int[]{width, height});
		}
		else
		{
			outGridMatrix.clear();
			initGridMatrix.clear();
			outGridMatrix.setDim(new int[]{0, 0});
			initGridMatrix.setDim(new int[]{0, 0});
			outlet(OUTLET_MGRID, MSG_MATRIX, nowhereMatrix.getName());
			outlet(OUTLET_MINIT, MSG_MATRIX, nowhereMatrix.getName());
		}
	}
}
