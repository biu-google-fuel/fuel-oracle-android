package org.biu.ufo.control.ml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.androidannotations.annotations.EBean;
import org.biu.ufo.control.utils.Calculator;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

/**
 * Implementation of KNN algorithm.
 * @author Danny Karmon
 *
 */
@EBean
public class KNN {

	ArrayList<DataInstance> trainingSet;
	DataInstance testData;
	List<Object> sortedTraining;
	
	
	public void setTrainingSet(ArrayList<DataInstance> trainingSet){
		this.trainingSet = trainingSet;
	}
	
	public void evaluate(ArrayList<Double> testAttributes){
		evaluate(new DataInstance(testAttributes, null));
	}

	public void evaluate(DataInstance testData){
		this.testData = testData;
		
		//Calculate the euclidean distance for each trainning distance		
		HashMap<Object, Double> evaluateMap = new HashMap<Object, Double>();
		for (DataInstance instance: trainingSet){
			evaluateMap.put(instance.target, Calculator.distance(instance.attributes, this.testData.attributes));
		}
		
		//list holds the target class sorted by similarity
		sortedTraining = Ordering.natural().onResultOf(Functions.forMap(evaluateMap))
				   .sortedCopy(evaluateMap.keySet());
	}
	
	
	/**
	 * @return estimation using 1-nearest neighbour 
	 */
	public Object getEstimation(){
		return sortedTraining.get(0);
	}
	
	/**
	 * @param kNeighbours
	 * @return estimation using k (given parameter)-nearest neighbour
	 */
	public Object getEstimation(int kNeighbours){
		
		int size = (kNeighbours > sortedTraining.size())?sortedTraining.size():kNeighbours;
		List<Object> kNeighboursLst = sortedTraining.subList(0, size);		
		int max_times = 0;
		int current_frequency;
		Object estimation = null;
		
		//locate the target class that appears the most
		for (Object item : kNeighboursLst){
			current_frequency = Collections.frequency(kNeighboursLst, item);
			if (max_times < current_frequency){
				max_times = current_frequency;
				estimation = item;
			}
		}
		
		return estimation;
	}
	
	
	/**
	 * @return list of the target objects sorted by similarity
	 */
	public List<?> getTrainingListSorted(){
		return sortedTraining;
	}

	
	
	
}
