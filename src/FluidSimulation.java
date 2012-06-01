/**
 * Something about Licence
 * 
 * @author		Léo LEFEBVRE
 * @version		1.0
 */

import com.cycling74.max.*;
import java.awt.Color;
 
public class FluidSimulation extends FluidParticleSimulation
{
    // Définition des messages d'entrée
    protected static String MSG_DELTA		= "deltaT";
    protected static String MSG_VISCOSITY	= "viscosity";
	protected static String MSG_FADESPEED	= "fade_speed";
	protected static String MSG_VELOCITY	= "velocity";
	protected static String MSG_COLOR		= "color";
	protected static String MSG_NOCOLOR		= "no_color";
	protected static String MSG_RANDOMCOLOR	= "random_color";
	protected static String MSG_ITERATIONS	= "iterations";
    
	// Attributs
    private FluidSolver fluidSolver;
	private float[] fluid_u;
	private float[] fluid_v;
	private boolean firstTime;
	
    public FluidSimulation(Atom[] args)
    {
		super(args);
		
        // 1 input - 2 outputs
        declareIO(1, 2);
        setInletAssist(0, "Bang and setting's messages");
        setOutletAssist(0, "Matrix of fluid image");
		setOutletAssist(1, "Send Fluid state to a particles' system");
		
		// Déclaration des attributs
		declareAttribute("fluid_u");
		declareAttribute("fluid_v");
		
        // On récupère le resolveur de fluide
        fluidSolver = new FluidSolver();

		// On initialise les paramètres
		firstTime = true;
		fluid_u = new float[]{};
		fluid_v = new float[]{};
    }

    @Override
    protected void bang()
    {
        // Message de Bang sur l'inlet 0
        if(getInlet() == 0)
        {
            fluidSolver.update();
			fluid_u = fluidSolver.getUArray();
			fluid_v = fluidSolver.getVArray();
			
			String outPutMatrixName = fluidSolver.getImgFluidName();
			if(outPutMatrixName != null)
				outlet(0, MSG_MATRIX, outPutMatrixName);
			
			if(firstTime)
			{
				notifyFluidDim();
				firstTime = false;
			}
        }
    }
	
	protected void notifyFluidDim()
	{
		Atom[] out = new Atom[]{Atom.newAtom(fluidSolver.getWidth()), Atom.newAtom(fluidSolver.getHeight())};
		outlet(1, MSG_FLUIDDIM, out);
	}
	
    @Override
    // Contrôles les messages d'entrée (bon message sur bonne entrée)
    // Ne peut pas recevoir de bang (voir méthode bang)
    protected void anything(String message, Atom[] args)
    {		
		boolean unknownMessage = false;
		
		if(TreatMessage(message, args))
		{
			// Messages de paramétrage
			if(args.length == 0)
			{
				if(message.contentEquals(MSG_RESET))
				{
					fluidSolver.reset();
					outlet(0, MSG_MATRIX, fluidSolver.getImgFluidName());
				}

				else
					unknownMessage = true;
			}

			// Messages de paramétrage
			else if(args.length == 1)
			{
				if(message.contentEquals(MSG_DELTA))
					fluidSolver.setDeltaT(args[0].toFloat());

				else if(message.contentEquals(MSG_VISCOSITY))
					fluidSolver.setViscosity(args[0].toFloat());

				else if(message.contentEquals(MSG_FADESPEED))
					fluidSolver.setFadeSpeed(args[0].toFloat());

				else if(message.contentEquals(MSG_VELOCITY))
					fluidSolver.setVelocity(args[0].toFloat());

				else if(message.contentEquals(MSG_NOCOLOR))
					fluidSolver.setColored(!args[0].toBoolean());

				else if(message.contentEquals(MSG_RANDOMCOLOR))
					fluidSolver.setRandomizeColor(args[0].toBoolean());

				else if(message.contentEquals(MSG_ITERATIONS))
					fluidSolver.setSolverIterations(args[0].toInt());

				else
					unknownMessage = true;
			}

			else if(args.length == 2)
			{
				if(message.contentEquals(MSG_FLUIDDIM))
				{
					fluidSolver.setCells(args[0].toInt(), args[1].toInt());
					notifyFluidDim();
				}

				else
					unknownMessage = true;
			}

			else if(args.length == 3)
			{
				if(message.contentEquals(MSG_COLOR))
					fluidSolver.setColor(new Color(args[0].toInt(), args[1].toInt(), args[2].toInt()));

				else
					unknownMessage = true;
			}
			
			else
				unknownMessage = true;
		}

		if(unknownMessage)
			unknownMessage(message, args);
    }
    
	@Override
	protected void applyBlob(int index, float posX, float posY)
	{
		if(blobs.containsKey(index))
		{
			Blob blob = blobs.get(index);
			blob.Move(posX, posY);
			fluidSolver.addForce(blob.getX(), blob.getY(), blob.getDeltaX(), blob.getDeltaY());
		}
		else
			blobs.put(index, new Blob(posX, posY, this));
	}

    @Override
    public void notifyDeleted()
    {
        fluidSolver.destroy();
    }
}
