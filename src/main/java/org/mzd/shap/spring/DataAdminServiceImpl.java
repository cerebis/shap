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
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.ObjectNotFoundException;
import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.analysis.AnnotatorDao;
import org.mzd.shap.analysis.Detector;
import org.mzd.shap.analysis.DetectorDao;
import org.mzd.shap.domain.DuplicateException;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.domain.dao.AnnotationDao;
import org.mzd.shap.domain.dao.FeatureDao;
import org.mzd.shap.domain.dao.ProjectDao;
import org.mzd.shap.domain.dao.SampleDao;
import org.mzd.shap.domain.dao.SequenceDao;
import org.mzd.shap.domain.dao.FeatureDaoSpringHibernate.FeatureBreakdownDTO;
import org.mzd.shap.domain.io.FeatureIOXstream;
import org.mzd.shap.domain.io.SequenceIOXstream;
import org.mzd.shap.hibernate.search.FullTextSearch;
import org.mzd.shap.hibernate.search.FullTextSearchImpl.SearchResult;
import org.mzd.shap.hibernate.search.view.Report;
import org.mzd.shap.io.Fasta;
import org.mzd.shap.io.FastaReader;
import org.mzd.shap.io.bean.BeanIOException;
import org.mzd.shap.util.BaseObservable;
import org.mzd.shap.util.Notification;
import org.mzd.shap.util.Observable;

public class DataAdminServiceImpl extends BaseObservable implements DataAdminService, Observable {
	private final static String NOTIFICATION_TYPE = "shap.dataadminservice";
	private ProjectDao projectDao;
	private SampleDao sampleDao;
	private SequenceDao sequenceDao;
	private FeatureDao featureDao;
	private AnnotationDao annotationDao;
	private DetectorDao detectorDao;
	private AnnotatorDao annotatorDao;
	private FullTextSearch fullTextSearch;
	private int batchSize = 40;
	
	public SearchResult<Report> getReports(String queryText, int firstResult, int maxResults) {
		Class<?>[] allowedClasses = {Project.class,Sample.class,Sequence.class,Feature.class};
		return getFullTextSearch().find(queryText, allowedClasses, firstResult, maxResults);
	}

	public Object getObject(Integer id, User user) {
		return getFullTextSearch().findUnique(id.toString(), user);
	}
	
	public Map<String,List<?>> getFullTextResult(String queryText, User user) {
		Map<String,List<?>> resultMap = new HashMap<String, List<?>>();
		resultMap.put("projects", getProjectDao().findByFullText(queryText,user));
		resultMap.put("samples", getSampleDao().findByFullText(queryText,user));
		resultMap.put("sequences", getSequenceDao().findByFullText(queryText,user));
		resultMap.put("features", getFeatureDao().findByFullText(queryText,user));
		return resultMap;
	}
	
	public Project getProject(User user, Integer projectId) throws NotFoundException {
		return getProjectDao().findByUserAndId(user, projectId);
	}
	
	public Sample getSample(Project project, Integer sampleId) throws NotFoundException {
		return getSampleDao().findByProjectAndId(project, sampleId);
	}	
	
	public Project getProject(Integer projectId) throws NotFoundException {
		return getProjectDao().findByID(projectId);
	}
	
	public Sequence getSequence(Sample sample, Integer sequenceId) throws NotFoundException {
		return getSequenceDao().findBySampleAndId(sample, sequenceId);
	}
	
	public Feature getFeature(Sequence sequence, Integer featureId) throws NotFoundException {
		return getFeatureDao().findBySequenceAndId(sequence, featureId);
	}
	
	public Project getProject(String projectName) throws NotFoundException {
		Project proj = getProjectDao().findByField("name", projectName);
		if (proj == null) {
			throw new NotFoundException("No project with name [" + projectName + "]");
		}
		return proj;
	}

	public Detector getDetector(String detectorName) throws NotFoundException {
		Detector det = getDetectorDao().findByField("name", detectorName);
		if (det == null) {
			throw new NotFoundException("No detector with name [" + detectorName + "]");
		}
		return det;
	}
	
	public Annotator getAnnotator(String annotatorName) throws NotFoundException {
		Annotator anno = getAnnotatorDao().findByField("name", annotatorName);
		if (anno == null) {
			throw new NotFoundException("No annotator with name [" + annotatorName + "]");
		}
		return anno;
	}

	public List<Object[]> getProjectTable(Integer userId, int firstResult, int maxResults, int sortFieldIndex, String sortDirection) throws NotFoundException {
		return getProjectDao().findPageRowsByUser(userId,firstResult,maxResults,sortFieldIndex,sortDirection);
	}
	
	public Long getProjectCount(Integer userId) {
		return getProjectDao().countByUser(userId);
	}
	
	public Sample getSample(Integer sampleId) throws NotFoundException {
		return getSampleDao().findByID(sampleId);
	}
	
	public Sample getSample(Project project, String sampleName) throws NotFoundException  {
		Sample smp = getSampleDao().findByProjectAndName(project, sampleName);
		if (smp == null) {
			throw new NotFoundException("Project [" + project.getName() + "] does not contain sample named [" + sampleName + "]");	
		}
		return smp;
	}
	
	public Sample getSample(String projectName, String sampleName) throws NotFoundException  {
		return getSample(getProject(projectName),sampleName);
	}
	
	public List<Object[]> getSampleTable(Integer projectId, int firstResult, int maxResults, 
			int sortFieldIndex, String sortDirection) throws NotFoundException {
		
		return getSampleDao().findPageRowsByProject(projectId,firstResult,maxResults,sortFieldIndex,sortDirection);
	}
	
	public Long getSampleCount(Integer projectId) {
		return getSampleDao().countByProject(projectId);
	}

	public Sequence getSequence(Integer sequenceId) throws NotFoundException {
		return getSequenceDao().findByID(sequenceId);
	}
	
	public Sequence getSequence(Sample sample, String sequenceName) throws NotFoundException  {
		Sequence seq = getSequenceDao().findBySampleAndName(sample, sequenceName, true);
		if (seq == null) {
			throw new NotFoundException("Sample [" + sample.getName() + "] does not contain sequence named [" + sequenceName + "]");	
		}
		return seq;
	}

	public Sequence getSequence(String projectName, String sampleName, String sequenceName) throws NotFoundException {
		return getSequence(getSample(projectName,sampleName),sequenceName);
	}
	
	public List<Object[]> getSequenceTable(Integer sampleId, int firstResult,	int maxResults, 
			int sortFieldIndex, String sortDirection) throws NotFoundException {
		
		return getSequenceDao().findPageRowsBySample(sampleId, firstResult, maxResults, sortFieldIndex, sortDirection);
	}

	public Long getSequenceCount(Integer sampleId) {
		Sample s = new Sample();
		s.setId(sampleId);
		return getSequenceDao().countBySample(s);
	}
	
	public List<Sequence> getSequences(List<Integer> sequenceIds) throws NotFoundException {
		List<Sequence> sequences = new ArrayList<Sequence>();
		for (Integer id : sequenceIds) {
			try {
				sequences.add(getSequenceDao().findByID(id));
			}
			catch (ObjectNotFoundException ex) {/*...*/}
		}
		return sequences;
	}

	public Feature getFeature(Integer featureId) throws NotFoundException {
		return getFeatureDao().findByID(featureId);
	}
 
	public List<Object[]> getFeatureTable(Integer sequenceId, int firstResult,
			int maxResults, int sortFieldIndex, String sortDirection) throws NotFoundException {
		return getFeatureDao().findPageRowsBySequence(sequenceId,firstResult,maxResults,sortFieldIndex,sortDirection);
	}
	
	public Long getFeatureCount(Integer sequenceId) {
		return getFeatureDao().countBySequence(sequenceId);
	}
	
	public List<FeatureBreakdownDTO> getFeatureBreakdown(Integer featureId) {
		return getFeatureDao().findTypeBreakdown(featureId);
	}
	
	public List<Feature> getFeatures(List<Integer> featureIds) throws NotFoundException {
		List<Feature> features = new ArrayList<Feature>();
		for (Integer id : featureIds) {
			try {
				features.add(getFeatureDao().findByID(id));
			}
			catch (ObjectNotFoundException ex) {/*...*/}
		}
		return features;
	}

	public List<Object[]> getAnnotationTable(Integer featureId, int firstResult, int maxResults, 
			int sortFieldIndex, String sortDirection) throws NotFoundException {
		return getAnnotationDao().findPageRowsByFeature(featureId,firstResult,maxResults,sortFieldIndex,sortDirection);
	}
	
	public Long getAnnotationCount(Integer featureId) {
		return getAnnotationDao().countByFeature(featureId);
	}
	
	public Project addProject(String projectName, String description) throws DuplicateException {
		if (getProjectDao().findByField("name", projectName) != null) {
			throw new DuplicateException("A project with the name [" + projectName + "] already exists");
		}
		Project project = new Project(projectName,description,new Date());
		return getProjectDao().saveOrUpdate(project);
	}
	
	public Sample addSample(String projectName, String sampleName, String description) throws DuplicateException, NotFoundException {
		Project project = getProject(projectName);
		if (getSampleDao().findByProjectAndName(project, sampleName) != null) {
			throw new DuplicateException("A sample with the name [" + sampleName + 
					"] already exists for project [" + project.getName() + "]");
		}
		Sample sample = new Sample(sampleName,description,new Date());
		project.addSample(sample);
		return getSampleDao().saveOrUpdate(sample);
	}
	
	public Sequence addSequence(String projectName, String sampleName, String sequenceName, String description) throws NotFoundException, DuplicateException {
		Project project = getProject(projectName);
		Sample sample = getSample(project,sampleName);
		if (getSequenceDao().findBySampleAndName(sample, sequenceName, true) != null) {
			throw new DuplicateException("A sequence with the name [" + sequenceName + 
					"] already exists for sample [" + sample.getName() + "]");
		}
		Sequence sequence = new Sequence();
		sequence.setName(sequenceName);
		sequence.setDescription(description);
		sample.addSequence(sequence);
		return getSequenceDao().saveOrUpdate(sequence);
	}
	
	public void addSequencesFromFasta(String projectName, String sampleName, File fastaFile) throws NotFoundException, IOException {
		// Read sequences from file and send to persistent store
		FastaReader reader = null;
		try {
			// Get containing sample
			Sample sample = getSample(projectName,sampleName);

			reader = new FastaReader(fastaFile);
			List<Sequence> seqBatch = new ArrayList<Sequence>(getBatchSize());
			
			Integer totalSeq = 0;
			Boolean complete = false;
			while (!complete) {
				// Read a batch of sequences
				int n = 0;
				while (n++ < getBatchSize()) {
					Fasta f = reader.readFasta();
					if (f == null) {
						complete = true;
						break;
					}
					Sequence seq = Sequence.fromFasta(f);
					
					sample.addSequence(seq);
					seqBatch.add(seq);
				}
				// Save a batch of sequences
				if (seqBatch.size() > 0) {
					totalSeq += seqBatch.size();
					getSequenceDao().batchSave(seqBatch);
					seqBatch.clear();
					
					notifyObservers(new Notification(NOTIFICATION_TYPE, this, 
							String.format("processed %5d sequences",totalSeq)));
				}
			}
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public void setCoverage(String projectName, String sampleName, Map<String,Double> coverageMap) throws IOException, NotFoundException {
		Sample sample = getSample(projectName, sampleName);
		Integer count = 0;
		for (String seqName : coverageMap.keySet()) {
			Sequence seq = getSequence(sample, seqName);
			seq.setCoverage(coverageMap.get(seqName));
			getSequenceDao().saveOrUpdate(seq);
			getSequenceDao().evict(seq);
			
			if (++count % getBatchSize() == 0) {
				notifyObservers(new Notification(NOTIFICATION_TYPE, this, 
						String.format("processed %5d sequences",count)));
			}
		}
	}
	
	public void addSequencesFromXml(String projectName, String sampleName, String detectorName, File sequenceXml) throws NotFoundException, IOException, BeanIOException, ClassNotFoundException {
		int nSeq = 0;
		int nFeat = 0;
		BufferedReader xmlReader = null;
		ObjectInputStream istream = null;
		try {
			Sample sample = getSample(projectName, sampleName);
			Detector detector = getDetector(detectorName);
			xmlReader = new BufferedReader(new FileReader(sequenceXml));
			istream = new SequenceIOXstream().getObjectInputStream(xmlReader);
			while (true) {
				Sequence seq = (Sequence)istream.readObject();
				try {
					getSequence(sample,seq.getName());
					throw new IOException("Sample [" + sample.getName() + "] already contains a sequence with the name [" + 
							seq.getName() + "]");
				}
				catch (NotFoundException ex) {/*...*/}
				
				++nSeq;
				for (Feature f : seq.getFeatures()) {
					f.setSequence(seq);
					f.setDetector(detector);
					++nFeat;
				}
				sample.addSequence(seq);
				getSequenceDao().saveOrUpdate(seq);
				notifyObservers(new Notification(NOTIFICATION_TYPE, this, 
						String.format("Sequence %s had %d features",seq.getName(),seq.getFeatures().size())));
			}
		}
		catch (EOFException ex) {
			notifyObservers(new Notification(NOTIFICATION_TYPE, this, 
					String.format("Read %d sequences and %d features",nSeq,nFeat)));
		}
		catch (org.springframework.dao.DataIntegrityViolationException ex) {
			notifyObservers(new Notification(NOTIFICATION_TYPE, this, 
					"Data integrity violation. Check that the XML defines all neccessary properties and obeys " +
					"constraints such as uniqueness."));
		}
		finally {
			if (istream != null) {
				istream.close();
			}
			if (xmlReader != null) {
				xmlReader.close();
			}
		}
	}

	public void addFeaturesFromXml(String projectName, String sampleName, String sequenceName, String detectorName, File featureXml) throws NotFoundException, IOException, BeanIOException, ClassNotFoundException {
		BufferedReader xmlReader = null; 
		ObjectInputStream istream = null;
		int nFeat = 0;
		try {
			Sequence sequence = getSequence(projectName, sampleName, sequenceName);
			Detector detector = getDetector(detectorName);
			xmlReader = new BufferedReader(new FileReader(featureXml));
			istream = new FeatureIOXstream().getObjectInputStream(xmlReader);
			while (true) {
				Feature feat = (Feature)istream.readObject();
				nFeat++;
				feat.setDetector(detector);
				sequence.addFeature(feat);
				getFeatureDao().saveOrUpdate(feat);
				notifyObservers(new Notification(NOTIFICATION_TYPE, this, 
						String.format("Read %d features",nFeat)));
			}
		}
		catch (EOFException ex) {
			notifyObservers(new Notification(NOTIFICATION_TYPE, this, 
					String.format("Read %d features",nFeat)));
		}
		catch (org.springframework.dao.DataIntegrityViolationException ex) {
			notifyObservers(new Notification(NOTIFICATION_TYPE, this, 
					"Data integrity violation. Check that the XML defines all neccessary properties and obeys " +
					"constraints such as uniqueness."));
		}
		finally {
			if (istream != null) {
				istream.close();
			}
			if (xmlReader != null) {
				xmlReader.close();
			}
		}
	}
	
	public void removeFeatures(List<Integer> featureIds) throws NotFoundException {
		for (Integer id : featureIds) {
			Feature f = getFeatureDao().findByID(id);
			getFeatureDao().delete(f);
		}
	}
	
	public void removeSequences(List<Integer> sequenceIds) throws NotFoundException {
		for (Integer id : sequenceIds) {
			Sequence s = getSequenceDao().findByID(id);
			getSequenceDao().delete(s);
		}
	}

	public ProjectDao getProjectDao() {
		return projectDao;
	}
	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	public SampleDao getSampleDao() {
		return sampleDao;
	}
	public void setSampleDao(SampleDao sampleDao) {
		this.sampleDao = sampleDao;
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
	
	public AnnotationDao getAnnotationDao() {
		return annotationDao;
	}
	public void setAnnotationDao(AnnotationDao annotationDao) {
		this.annotationDao = annotationDao;
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

	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public FullTextSearch getFullTextSearch() {
		return fullTextSearch;
	}
	public void setFullTextSearch(FullTextSearch fullTextSearch) {
		this.fullTextSearch = fullTextSearch;
	}
	
}
