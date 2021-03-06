package marf.Classification.Stochastic;

import java.io.Serializable;
import java.util.Vector;

import marf.MARF;
import marf.Classification.ClassificationException;
import marf.FeatureExtraction.IFeatureExtraction;
import marf.MARF.NLP;
import marf.Stats.StatisticalEstimators.StatisticalEstimator;
import marf.Storage.Result;
import marf.Storage.StorageException;
import marf.util.Debug;


/**
 * <p>Maximum Probability Classification Module.
 *
 * Originally came with the <code>LangIdentApp</code> NLP application
 * of Serguei Mokhov.
 * </p>
 *
 * @author Serguei Mokhov
 */
public class MaxProbabilityClassifier
extends Stochastic
{
	/**
	 * Local reference to some instance of a statistical
	 * estimator for probability computation.
	 */
	protected StatisticalEstimator oStatisticalEstimator = null;

	/**
	 * A collection of available, typically NLP, subjects.
	 */
	protected Vector<String> oAvailLanguages = null;

	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 */
	private static final long serialVersionUID = 8665926058819588355L;

	/**
	 * NLP constructor that takes directly a statistical estimator.
	 * @param poStatisticalEstimator statistical estimator to use
	 */
	public MaxProbabilityClassifier(StatisticalEstimator poStatisticalEstimator)
	{
		super(null);
		init(poStatisticalEstimator);
	}

	/**
	 * Implements Classification API.
	 * @param poFeatureExtraction FeatureExtraction module reference
	 */
	public MaxProbabilityClassifier(IFeatureExtraction poFeatureExtraction)
	{
		super(poFeatureExtraction);

		// See if there is a request for a specific
		// statistical estimator.
		if(MARF.getModuleParams() != null)
		{
			Vector<Serializable> oParams = MARF.getModuleParams().getClassificationParams();

			if(oParams != null && oParams.size() > 1)
			{
				this.oStatisticalEstimator = (StatisticalEstimator)oParams.elementAt(1);
			}
		}

		init(this.oStatisticalEstimator);
	}

	/**
	 * Initializes the classifier with all member variables.
	 * @param poStatisticalEstimator statistical estimator to use
	 * @throws IllegalArgumentException if poStatisticalEstimator is null
	 */
	public void init(StatisticalEstimator poStatisticalEstimator)
	{
		if(poStatisticalEstimator == null)
		{
			throw new IllegalArgumentException("MaxProbabilityClassifier: StatisticalEstimator is null!");
		}

		this.oStatisticalEstimator = poStatisticalEstimator;

		this.oAvailLanguages = new Vector<String>();
		this.oObjectToSerialize = this.oAvailLanguages;
		this.strFilename
			= MARF.getTrainingSetFilenamePrefix()
			+ getClass().getName()
			+ ".gzbin";
	}

	/**
	 * Performs training of underlying statistical estimator
	 * and goes through restore/dump cycle to save the available
	 * languages. Implements Classification API.
	 * @return <code>true</code>
	 * @throws ClassificationException should there be a problem with dump/restore
	 */
	public boolean train()
	throws ClassificationException
	{
		try
		{
			this.oStatisticalEstimator.train();

			restore();

			Debug.debug("tr.before.oAvailLanguages=" + this.oAvailLanguages);
			System.out.println("Adding language [" + NLP.getLanguage() + "] ---- ");

			if(this.oAvailLanguages.contains(NLP.getLanguage()) == false)
			{
				this.oAvailLanguages.add(NLP.getLanguage());
				Debug.debug("tr.after.oAvailLanguages=" + this.oAvailLanguages);

				dump();
			}

			return true;
		}
		catch(StorageException e)
		{
			e.printStackTrace(System.err);
			throw new ClassificationException(e);
		}
	}

	/**
	 * Performs language classification.
	 * Implements Classification API.
	 * @return <code>true</code> if classification was successful
	 * @throws ClassificationException if there was a problem with I/O
	 * or if there are no available languages
	 */
	public boolean classify()
	throws ClassificationException
	{
		try
		{
			restore();

			Debug.debug("oAvailLanguages=" + this.oAvailLanguages);

			if(this.oAvailLanguages.size() == 0)
			{
				throw new ClassificationException("MaxProbabilityClassifier: there are no languages available.");
			}

			for(int i = 0; i < this.oAvailLanguages.size(); i++)
			{
				String strSubject = this.oAvailLanguages.elementAt(i);
				//oStatisticalEstimator.setLang(strLang);

				NLP.setLanguage(strSubject);
				this.oStatisticalEstimator.resetFilename();

				double dProbability = this.oStatisticalEstimator.p();
				
				this.oStatisticalEstimator.getStreamTokenizer().reset();

				System.out.println("subject=" + strSubject + ", P=" + dProbability);
				
				// See if the String subject happened to be a number actually, which would be the ID
				// If so, set it for the general pipelining mechanism.
				try
				{
					Integer iID = Integer.parseInt(strSubject);
					this.oResultSet.addResult(new Result(iID, dProbability, strSubject));
				}
				catch(NumberFormatException e)
				{
					// Old-fashioned IDless result
					this.oResultSet.addResult(new Result(dProbability, strSubject));
				}
			}

			return true;
		}
		catch(ClassificationException e)
		{
			e.printStackTrace(System.err);
			throw e;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			throw new ClassificationException(e);
		}
	}

	/**
	 * Add a piece of general StorageManager contract.
	 * Resets available languages vector from the
	 * object-to-serialize reference.
	 * @since 0.3.0.5
	 */
	@SuppressWarnings("unchecked")
	public synchronized void backSynchronizeObject()
	{
		this.oAvailLanguages = (Vector<String>)this.oObjectToSerialize;
	}

	/**
	 * An object must know how dump itself or its data structures to a file.
	 * It only uses <code>DUMP_GZIP_BINARY</code> and <code>DUMP_BINARY</code> modes.
	 *
	 * @throws StorageException if saving to a file for some reason fails or
	 * the dump mode set to an unsupported value
	 *
	 * @see #dumpGzipBinary()
	 * @see #dumpBinary()
	 * @see #backSynchronizeObject()
	 * @since 0.3.0.5
	 */
	public synchronized void dump()
	throws StorageException
	{
		switch(this.iCurrentDumpMode)
		{
			case DUMP_GZIP_BINARY:
				dumpGzipBinary();
				break;

			case DUMP_BINARY:
				dumpBinary();
				break;

			default:
				throw new StorageException("Unsupported dump mode: " + this.iCurrentDumpMode);
		}
	}

	/**
	 * An object must know how restore itself or its data structures from a file.
	 * Options are: Object serialization and CSV, HTML. Internally, the method
	 * calls all the <code>restore*()</code> methods based on the current dump mode.
	 *
	 * @throws StorageException if loading from a file for some reason fails or
	 * the dump mode set to an unsupported value
	 *
	 * @see #DUMP_GZIP_BINARY
	 * @see #DUMP_BINARY
	 * @see #dumpGzipBinary()
	 * @see #dumpBinary()
	 * @see #backSynchronizeObject()
	 * @see #iCurrentDumpMode
	 * @since 0.3.0.5
	 */
	public synchronized void restore()
	throws StorageException
	{
		switch(this.iCurrentDumpMode)
		{
			case DUMP_GZIP_BINARY:
				restoreGzipBinary();
				break;

			case DUMP_BINARY:
				restoreBinary();
				break;

			default:
				throw new StorageException("Unsupported dump mode: " + this.iCurrentDumpMode);
		}
	}
}

// EOF
