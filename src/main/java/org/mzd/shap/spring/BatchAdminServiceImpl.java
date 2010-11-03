/**
 *
 * Copyright 2010 Matthew Z DeMaere.
 * 
 * This file is part of SHAP.
 *
 * SHAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SHAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SHAP.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.mzd.shap.spring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.analysis.AnnotatorDao;
import org.mzd.shap.analysis.Detector;
import org.mzd.shap.analysis.DetectorDao;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.FeatureException;
import org.mzd.shap.domain.FeatureType;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.dao.FeatureDao;
import org.mzd.shap.domain.dao.SequenceDao;
import org.mzd.shap.spring.plan.Step;
import org.mzd.shap.spring.plan.Target;
import org.mzd.shap.spring.task.AnnotationTask;
import org.mzd.shap.spring.task.DetectionTask;
import org.mzd.shap.spring.task.Job;
import org.mzd.shap.spring.task.JobDao;
import org.mzd.shap.spring.task.Task;
import org.mzd.shap.spring.task.TaskDao;
import org.mzd.shap.util.BaseObservable;
import org.mzd.shap.util.Notification;
import org.mzd.shap.util.Observable;

public class BatchAdminServiceImpl extends BaseObservable implements BatchAdminService, Observable {
	private DataViewService dataViewService;
	private DetectorDao detectorDao;
	private AnnotatorDao annotatorDao;
	private SequenceDao sequenceDao;
	private FeatureDao featureDao;
	private TaskDao taskDao;
	private JobDao jobDao;
	
	protected Detector getDetector(String detectorName) throws NotFoundException {
		Detector detector = getDetectorDao().findByField("name", detectorName);
		if (detector == null) {
			throw new NotFoundException("Detector named [" + detectorName + "] not found");
		}
		return detector;
	}
	
	protected Annotator getAnnotator(String annotatorName) throws NotFoundException {
		Annotator annotator = getAnnotatorDao().findByField("name", annotatorName);
		if (annotator == null) {
			throw new NotFoundException("Annotator named [" + annotatorName + "] not found");
		}
		return annotator;
	}
	
	protected Job createDetectionJob(List<Sequence> sequences, Detector detector) {
		Job job = new Job();
		
		int count = 0;
		int first = 0;
		while (first < sequences.size()) {
			
			int last = first + detector.getBatchSize() > sequences.size() ?
					sequences.size() : first + detector.getBatchSize();

			System.out.println("\t##Batch from " + first + "," + last);
			for (int n=first; n<last; n++) {
				System.out.println("\t\tsequence " + sequences.get(n).getId());
			}

			DetectionTask t = new DetectionTask();
			t.setTarget(sequences.subList(first, last));
			t.setDetector(detector);
			job.addTask(t);
			
			count += last - first;
			first = last;
		}
		
		notifyObservers(new Notification("shap.batchservice", this, 
				String.format("processed %5d sequences",count)));
		
		return job;
	}

	public Job createDetectionJobForProject(String projectName, String detectorName) throws NotFoundException {
		Detector detector = getDetector(detectorName);
		Project project = getDataViewService().getProject(projectName);
		List<Sequence> sequences = getSequenceDao().findByProject(project, true);
		return createDetectionJob(sequences, detector);
	}
	
	public Job createDetectionJobForSample(String projectName, String sampleName, String detectorName) throws NotFoundException {
		Detector detector = getDetector(detectorName);
		Project project = getDataViewService().getProject(projectName);
		Sample sample = getDataViewService().getSample(project, sampleName);
		List<Sequence> sequences = getSequenceDao().findBySample(sample, true);
		return createDetectionJob(sequences, detector);
	}
	
	public Job createDetectionJobForSequence(Integer sequenceId, String detectorName) throws NotFoundException {
		Detector detector = getDetector(detectorName);
		List<Sequence> seqList = new ArrayList<Sequence>(1);
		seqList.add(getSequenceDao().findById(sequenceId, true));
		return createDetectionJob(seqList,detector);
	}

	public Job createDetectionJob(File sequenceList, String detectorName) throws IOException, NotFoundException {
		
		if (!sequenceList.exists()) {
			throw new IOException("File '" + sequenceList.getPath() 
					+ "' was not found.");
		}
		else if (!sequenceList.isFile()) {
			throw new IOException("Path '" + sequenceList.getPath() + 
					"' was not a file.");
		}
		
		List<Sequence> sequences = new ArrayList<Sequence>();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(sequenceList));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				line.trim();
				if (line.length() <= 0) {
					continue;
				}
				Integer sequenceId = Integer.parseInt(line);
				Sequence seq = getSequenceDao().findById(sequenceId, true);
				if (seq == null) {
					throw new NotFoundException("The sequence [" + sequenceId + "] was not found");
				}
				sequences.add(seq);
			}
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}		
		
		Detector detector = getDetector(detectorName);

		return createDetectionJob(sequences, detector);
	}
	
	protected Job createAnnotationJob(List<Feature> features, Annotator annotator) {
		Job job = new Job();
		
		int count = 0;
		int first = 0;
		while (first < features.size()) {

			int last = first + annotator.getBatchSize() > features.size() ?
					features.size() : first + annotator.getBatchSize();

			AnnotationTask t = new AnnotationTask();
			t.setTarget(features.subList(first, last));
			t.setAnnotator(annotator);
			job.addTask(t);
			
			count += last - first;
			first = last;
		}

		notifyObservers(new Notification("shap.batchservice", this, 
				String.format("processed %5d features",count)));
		
		return job;
	}
	
	public Job createAnnotationJobForFeature(FeatureType type, Integer featureId, String annotatorName) 
		throws NotFoundException,FeatureException 
	{
		Annotator annotator = getAnnotator(annotatorName);
		List<Feature> featureList = new ArrayList<Feature>(1);
		featureList.add(getFeatureDao().findByID(featureId));
		return createAnnotationJob(featureList, annotator);
	}
	
	public Job createAnnotationJobForSequence(FeatureType type, Integer sequenceId, String annotatorName) 
		throws NotFoundException 
	{
		Annotator annotator = getAnnotator(annotatorName);
		Sequence seq = getSequenceDao().findById(sequenceId, true);
		List<Feature> featureList = getFeatureDao().findBySequenceAndType(seq, type, true);
		if (featureList.size() == 0) {
			return null;
		}
		return createAnnotationJob(featureList, annotator);
	}
	
	public Job createAnnotationJobForSample(FeatureType type, String projectName, String sampleName, String annotatorName) 
		throws NotFoundException 
	{
		Project project = getDataViewService().getProject(projectName);
		Sample sample = getDataViewService().getSample(project, sampleName);
		Annotator annotator = getAnnotator(annotatorName);
		List<Feature> featureList = getFeatureDao().findBySampleAndType(sample, type, true);
		if (featureList.size() == 0) {
			return null;
		}
		return createAnnotationJob(featureList, annotator);
	}

	public Job newDetectionStep(Target target, Step step) throws NotFoundException {
		Job combinedJob = new Job();
		for (String analyzer : step.getAnalyzers()) {
			if (target.isSequence()) {
				Job job = createDetectionJobForSequence(target.getSequenceId(), analyzer);
				combinedJob.addTasks(job);
			}
			else if (target.isSample()) {
				Job job = createDetectionJobForSample(target.getProjectName(),target.getSampleName(), analyzer);
				combinedJob.addTasks(job);
			}
			else {
				Job job = createDetectionJobForProject(target.getProjectName(), analyzer);
				combinedJob.addTasks(job);
			}
			
			notifyObservers(new Notification("shap.batchservice", this, "added analysis by: " + analyzer));
		}
		return combinedJob;
	}
	
	public Job newAnnotationStep(Target target, Step step) throws NotFoundException, FeatureException {
		Job combinedJob = new Job();
		for (String analyzer : step.getAnalyzers()) {
			
			if (target.isFeature()) {
				Job job = createAnnotationJobForFeature(FeatureType.OpenReadingFrame, 
						target.getFeatureId(), analyzer);
				combinedJob.addTasks(job);
			}
			else if (target.isSequence()) {
				Job job = createAnnotationJobForSequence(FeatureType.OpenReadingFrame, 
						target.getSequenceId(), analyzer);
				combinedJob.addTasks(job);
			}
			else {
				Job job = createAnnotationJobForSample(FeatureType.OpenReadingFrame, 
						target.getProjectName(), target.getSampleName(), analyzer);
				combinedJob.addTasks(job);
			}
			
			notifyObservers(new Notification("shap.batchservice", this,"added analysis by: " + analyzer));
		}
		return combinedJob;
	}

	public Job loadUnfinishedJob(Integer jobId) throws NotFoundException {
		Job unfinished = getJobDao().findUnfinishedById(jobId);
		if (unfinished == null) {
			throw new NotFoundException("No unfinished job with id [" + jobId + "] was found");
		}
		return unfinished;
	}
	
	public List<Task> prepareNewTasks(int maxResults) {
		List<Task> tasks = getTaskDao().findNewInStartedJobs(maxResults);
		for (Task task : tasks) {
			task.markQueued();
			getTaskDao().makePersistent(task);
		}
		return tasks;
	}
	
	public Job prepareNewJob() {
		Job job = getJobDao().findNextNew();
		if (job == null) {
			return null;
		}
		job.markStart();
		getJobDao().makePersistent(job);
		return job;
	}
	
	
	public Job saveNewJob(Job job) {
		job = getJobDao().makePersistent(job);
		getTaskDao().saveOrUpdateAll(job.getTasks());
		return job;
	}
	
	public List<Job> getStartedJobs() {
		return getJobDao().findStarted();
	}
	
	public Job finishJob(Job job) {
		job.markFinish();
		return getJobDao().makePersistent(job);
	}

	public long countNewTasks() {
		return getTaskDao().countNew();
	}
	
	public long countIncompleteTasks() {
		return getTaskDao().countIncomplete();
	}
	
	public long countIncompleteTasks(Job job) {
		return getJobDao().countIncompleteTasks(job);
	}
	
	public long countIncompleteJobs() {
		return getJobDao().countIncomplete();
	}

	public DataViewService getDataViewService() {
		return dataViewService;
	}
	public void setDataViewService(DataViewService dataViewService) {
		this.dataViewService = dataViewService;
	}

	public DetectorDao getDetectorDao() {
		return detectorDao;
	}
	public void setDetectorDao(DetectorDao detectorDao) {
		this.detectorDao = detectorDao;
	}

	public AnnotatorDao getAnnotatorDao() {
		return annotatorDao;
	}
	public void setAnnotatorDao(AnnotatorDao annotatorDao) {
		this.annotatorDao = annotatorDao;
	}

	public SequenceDao getSequenceDao() {
		return sequenceDao;
	}
	public void setSequenceDao(SequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}

	public FeatureDao getFeatureDao() {
		return featureDao;
	}
	public void setFeatureDao(FeatureDao featureDao) {
		this.featureDao = featureDao;
	}

	public TaskDao getTaskDao() {
		return taskDao;
	}
	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public JobDao getJobDao() {
		return jobDao;
	}
	public void setJobDao(JobDao jobDao) {
		this.jobDao = jobDao;
	}

}
