package marf.Classification.Similarity;

import java.util.List;

import marf.Classification.Classification;
import marf.Classification.ClassificationException;
import marf.FeatureExtraction.IFeatureExtraction;
import marf.Storage.ITrainingSample;
import marf.Storage.Result;
import marf.Storage.StorageException;
import marf.math.Vector;
import marf.util.Debug;


/**
 * <p>Cosine Similarity Measure Classifier.</p>
 *
 * @author Serguei Mokhov
 * @since 0.3.0.6
 */
public class CosineSimilarityMeasure
extends Classification
{
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 */
	private static final long serialVersionUID = -6012156624213951005L;

	/**
	 * Distance Constructor.
	 * @param poFeatureExtraction FeatureExtraction module reference
	 */
	public CosineSimilarityMeasure(IFeatureExtraction poFeatureExtraction)
	{
		super(poFeatureExtraction);
	}

	/**
	 * Classify the feature vector based on whatever
	 * <code>similarity()</code> derivatives implement.
	 *
	 * @return <code>true</code> if classification successful whatever that means
	 *
	 * @throws ClassificationException if sanity checks fail. The checks include
	 * verifying nullness of the mean vector and its length compared to the
	 * feature vector or encapsulation of StorageException when dumping/restoring.
	 *
	 * @see #similarity(double[], double[])
	 * @see Classification#classify()
	 */
	public boolean classify(double[] padFeatureVector)
	throws ClassificationException
	{
		try
		{
			// Features of the incoming sample
			double[] adIncomingFeatures = padFeatureVector;

			// Restore training model from the disk
			restore();

			// Features in the training set
			//Vector oTrainingSamples = this.oTrainingSet.getClusters();
			List<ITrainingSample> oTrainingSamples = this.oTrainingSet.getClusters();

			// Our maximum similarity measure.
			double dMaxSimilarity = Double.MIN_VALUE;

			/*
			 * Run through the stored training samples set (mean vectors)
			 * and determine the two closest subjects to the incoming features sample
			 */
			for(int i = 0; i < oTrainingSamples.size(); i++)
			{
				//Cluster oTrainingSample = (Cluster)oTrainingSamples.get(i);
				ITrainingSample oTrainingSample = oTrainingSamples.get(i);

				//double[] adDataVector = oTrainingSample.getMeanVector();
//				double[] adMeanVector = oTrainingSample.getMedianVector();
				for(double[] adDataVector: oTrainingSample.getDataVectors())
				{
					// Sanity check: stored mean vector must never be null
					if(adDataVector == null)
					{
						throw new ClassificationException
						(
							"similarity() - Stored mean vector is null for subject (" + oTrainingSample.getSubjectID() +
							", preprocessing method: " + this.oTrainingSet.getPreprocessingMethod() +
							", feature extraction methods: "  + this.oTrainingSet.getFeatureExtractionMethod()
						);
					}
	
					// Sanity check: vectors must be of the same length
					if(adDataVector.length != adIncomingFeatures.length)
					{
						throw new ClassificationException
						(
							"similarity() - Mean vector length (" + adDataVector.length +
							") is not same as of incoming feature vector (" + adIncomingFeatures.length + ")"
						);
					}
	
					/*
					 * We have a mean vector of the samples for this iCurrentSubjectID
					 * Compare using whatever distance classifier it is...
					 */
					double dCurrentSimilarity = similarity(adDataVector, adIncomingFeatures);
	
					Debug.debug("Similarity for subject " + oTrainingSample.getSubjectID() + " = " + dCurrentSimilarity);
	
					// XXX: What should we do in this (very rare and subtle) case?
					if(dCurrentSimilarity == dMaxSimilarity)
					{
						Debug.debug("This similarity had happened before!");
					}
	
					if(dCurrentSimilarity > dMaxSimilarity)
					{
						dMaxSimilarity = dCurrentSimilarity;
					}
				}
				
				// Collect for stats
				// XXX: Move to StatsCollector
				this.oResultSet.addResult
				(
					oTrainingSample.getSubjectID(),
					dMaxSimilarity
				);
			}

			return true;
		}
		catch(StorageException e)
		{
			throw new ClassificationException(e);
		}
	}

	/**
	 * Generic similarity routine. May be overridden.
	 * @param padVector1 first vector for similarity calculation
	 * @param padVector2 second vector for similarity calculation
	 * @return similarity between the two vectors
	 * @throws ClassificationException
	 */
	public double similarity(final double[] padVector1, final double[] padVector2)
	throws ClassificationException
	{
		double dSimilarity = 0;
		double dDenominator = 0;

		Vector oVector1 = new Vector(padVector1);
		Vector oVector2 = new Vector(padVector2);

		dDenominator = oVector1.getLength() * oVector2.getLength();
		
		if(dDenominator != 0)
		{
			dSimilarity = Vector.getDotProduct(oVector1, oVector2) / dDenominator;
		}
		else
		{
			// Kept 0; as it is unlikely to be similar
			Debug.debug("WARNING: denominator is 0 while computing similarity measure.");
		}
		
		return dSimilarity;
	}

	/**
	 * Retrieves the maximum-similarity classification result.
	 * @return Result object
	 */
	public Result getResult()
	{
		return this.oResultSet.getMaximumResult();
	}
}

// EOF
