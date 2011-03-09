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
package org.mzd.shap.spring.web;

import java.util.List;

import javax.validation.Valid;

import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.spring.NotFoundException;
import org.mzd.shap.spring.web.json.DataTableRequest;
import org.mzd.shap.spring.web.json.DataTableResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/browse")
public class DataBrowseController extends AbstractControllerSupport {
	
	public DataBrowseController() {}
	
	public class ProjectTableResponse extends DataTableResponse {
		public ProjectTableResponse() {
			super("id","name","description","creation","samples");
		}
		
		@Override
		public void addAll(List<Object[]> rows) {
			for (Object[] row : rows) {
				row[3] = dateFormat(row[3]);
			}
			setAaData(rows);
		}
	}

	public class SampleTableResponse extends DataTableResponse {
		public SampleTableResponse() {
			super("id","name","description","creation","sequences");
		}

		@Override
		public void addAll(List<Object[]> rows) {
			for (Object[] row : rows) {
				row[3] = dateFormat(row[3]);
			}
			setAaData(rows);
		}
	}
	
	public class SequenceTableResponse extends DataTableResponse {
		public SequenceTableResponse() {
			super("id","name","description","coverage","taxonomy","length","features");
		}

		@Override
		public void addAll(List<Object[]> rows) {
			for (Object[] row : rows) {
				row[3] = deciFormat(row[3]);
			}
			setAaData(rows);
		}
	}

	public class FeatureTableResponse extends DataTableResponse {
		public FeatureTableResponse() {
			super("id","start","end","strand","frame","partial","confidence","type","length","annotations");
		}

		@Override
		public void addAll(List<Object[]> rows) {
			for (Object[] row : rows) {
				row[6] = sciFormat(row[6]);
			}
			setAaData(rows);
		}
}

	public class AnnotationTableResponse extends DataTableResponse {
		public AnnotationTableResponse() {
			super("accession", "description", "confidence", "annotator");
		}

		@Override
		public void addAll(List<Object[]> rows) {
			for (Object[] row : rows) {
				row[2] = sciFormat(row[2]);
			}
			setAaData(rows);
		}
	}

	@RequestMapping(method=RequestMethod.GET)
	public String getProjects(Model model) throws NotFoundException {
		addSessionUser(model);
		return "browse/projectsTab";
	}

	@RequestMapping(value="/ajax",method=RequestMethod.POST)
	@ResponseBody
	public ProjectTableResponse getProjects(@Valid DataTableRequest data) throws NotFoundException {
		User user = getSessionUser();
		ProjectTableResponse response = new ProjectTableResponse();
		List<Object[]> rows = getDataAdmin().getProjectTable(
				data.getIDisplayStart(), 
				data.getIDisplayLength(),
				data.getSortedColumnIndex(),
				data.getSortingDirection());
		response.addAll(rows);
		response.setsEcho(data.getSEcho());
		Long numRec = getDataAdmin().getProjectCount(user.getId());
		response.setiTotalRecords(numRec);
		response.setiTotalDisplayRecords(numRec);
		return response;
	}

	@RequestMapping(value="/object/{id}")
	public String getObject(@PathVariable Integer id, Model model) throws NotFoundException {
		
		/*
		 * If there is a check here for ownership then the subsequent naive tranversal
		 * of the domain model will not enter into data not owned by the user.
		 */
		Object obj = getDataAdmin().getObject(id, null);
		if (obj == null) {
			throw new NotFoundException("Failed to retrieve an object using the identifier [" + id + "]");
		}
		
		addSessionUser(model);
		
		if (obj instanceof Project) {
			model.addAttribute("project", obj);
			return "browse/samplesTab";
		}
		else if (obj instanceof Sample) {
			Sample s = (Sample)obj;
			model.addAttribute("sample", s);
			model.addAttribute("project", s.getProject());
			return "browse/sequencesTab";
		}
		else if (obj instanceof Sequence) {
			Sequence s = (Sequence)obj;
			model.addAttribute("sequence", s);
			model.addAttribute("sample",s.getSample());
			model.addAttribute("project",s.getSample().getProject());
			return "browse/featuresTab";
		}
		else if (obj instanceof Feature) {
			Feature f = (Feature)obj;
			model.addAttribute("feature", f);
			model.addAttribute("sequence", f.getSequence());
			model.addAttribute("sample",f.getSequence().getSample());
			model.addAttribute("project",f.getSequence().getSample().getProject());
			return "browse/annotationsTab";
		}
		else {
			throw new NotFoundException("Requested object is not supported for viewing");
		}
	}
	
	@RequestMapping(value="/object/{id}/ajax",method=RequestMethod.POST)
	@ResponseBody
	public DataTableResponse getObjects(@Valid DataTableRequest data, @PathVariable Integer id) throws NotFoundException {

		Object obj = getDataAdmin().getObject(id, null);
		
		if (obj == null) {
			throw new NotFoundException("Failed to retrieve an object using the identifier [" + id + "]");
		}

		DataTableResponse response = null;
		
		if (obj instanceof Project) {
			Integer projectId = ((Project)obj).getId();
			response = new SampleTableResponse();
			List<Object[]> rows = getDataAdmin().getSampleTable(
					projectId,
					data.getIDisplayStart(), 
					data.getIDisplayLength(),
					response.getColumnIndex(data.getSortedColumn()),
					data.getSortingDirection());
			response.addAll(rows);
			response.setsEcho(data.getSEcho());
			Long numRec = getDataAdmin().getSampleCount(projectId);
			response.setiTotalRecords(numRec);
			response.setiTotalDisplayRecords(numRec);
		}
		else if (obj instanceof Sample) {
			Integer sampleId = ((Sample)obj).getId();
			response = new SequenceTableResponse();
			List<Object[]> rows = getDataAdmin().getSequenceTable(
					sampleId, 
					data.getIDisplayStart(), 
					data.getIDisplayLength(),
					response.getColumnIndex(data.getSortedColumn()),
					data.getSortingDirection());
			response.addAll(rows);
			response.setsEcho(data.getSEcho());
			Long numRec = getDataAdmin().getSequenceCount(sampleId);
			response.setiTotalRecords(numRec);
			response.setiTotalDisplayRecords(numRec);
		}
		else if (obj instanceof Sequence) {
			Integer sequenceId = ((Sequence)obj).getId();
			response = new FeatureTableResponse();
			List<Object[]> rows = getDataAdmin().getFeatureTable(
					sequenceId, 
					data.getIDisplayStart(), 
					data.getIDisplayLength(),
					response.getColumnIndex(data.getSortedColumn()),
					data.getSortingDirection());
			response.addAll(rows);
			response.setsEcho(data.getSEcho());
			Long numRec = getDataAdmin().getFeatureCount(sequenceId);
			response.setiTotalRecords(numRec);
			response.setiTotalDisplayRecords(numRec);
		}
		else if (obj instanceof Feature) {
			Integer featureId = ((Feature)obj).getId();
			response = new AnnotationTableResponse();
			List<Object[]> rows = getDataAdmin().getAnnotationTable(
					featureId,
					data.getIDisplayStart(),
					data.getIDisplayLength(),
					response.getColumnIndex(data.getSortedColumn()),
					data.getSortingDirection());
			response.addAll(rows);
			response.setsEcho(data.getSEcho());
			Long numRec = getDataAdmin().getAnnotationCount(featureId);
			response.setiTotalRecords(numRec);
			response.setiTotalDisplayRecords(numRec);
		}
		else {
			throw new NotFoundException("Requested object is not supported for viewing");
		}
		
		return response;
	}
	
	//	@RequestMapping(value="/project/{projectId}",method=RequestMethod.GET)
//	public String getSamples(@PathVariable Integer projectId, Model model) throws NotFoundException {
//		getObjectGraph(model, projectId, null, null, null);
//		return "samples";
//	}
//	
//	@RequestMapping(value="/project/{projectId}/ajax",method=RequestMethod.POST)
//	@ResponseBody
//	public SampleTableResponse getSamples(@Valid DataTableRequest data, @PathVariable Integer projectId) throws NotFoundException {
//		// Make sure we don't expose data from outside the User's object graph
//		checkOwnership(projectId, null, null, null);
//		
//		SampleTableResponse response = new SampleTableResponse();
//		List<Object[]> rows = getDataAdmin().getSampleTable(
//				projectId, 
//				data.getIDisplayStart(), 
//				data.getIDisplayLength(),
//				data.getSortedColumnIndex(),
//				data.getSortingDirection());
//		response.setAaData(rows);
//		response.setsEcho(data.getSEcho());
//		Long numRec = getDataAdmin().getSampleCount(projectId);
//		response.setiTotalRecords(numRec);
//		response.setiTotalDisplayRecords(numRec);
//		return response;
//	}
	
//	@RequestMapping(value="/project/{projectId}/sample/{sampleId}", method=RequestMethod.GET)
//	public String getSequences(@PathVariable Integer projectId, @PathVariable Integer sampleId, Model model) throws NotFoundException {
//		getObjectGraph(model, projectId, sampleId, null, null);
//		return "sequences";
//	}
//
//	@RequestMapping(value="/project/{projectId}/sample/{sampleId}/ajax", method=RequestMethod.POST)
//	@ResponseBody
//	public SequenceTableResponse getSequences(@Valid DataTableRequest data, @PathVariable Integer projectId, 
//			@PathVariable Integer sampleId) throws NotFoundException {
//		
//		// Make sure we don't expose data from outside the User's object graph
//		checkOwnership(projectId, sampleId, null, null);
//
//		SequenceTableResponse response = new SequenceTableResponse();
//		List<Object[]> rows = getDataAdmin().getSequenceTable(
//				sampleId, 
//				data.getIDisplayStart(), 
//				data.getIDisplayLength(),
//				data.getSortedColumnIndex(),
//				data.getSortingDirection());
//		response.setAaData(rows);
//		response.setsEcho(data.getSEcho());
//		Long numRec = getDataAdmin().getSequenceCount(sampleId);
//		response.setiTotalRecords(numRec);
//		response.setiTotalDisplayRecords(numRec);
//		return response;
//	}
	
//	@RequestMapping(value="/project/{projectId}/sample/{sampleId}/sequence/{sequenceId}",method=RequestMethod.GET)
//	public String getFeatures(@PathVariable Integer projectId, @PathVariable Integer sampleId, 
//			@PathVariable Integer sequenceId, Model model) throws NotFoundException {
//		getObjectGraph(model, projectId, sampleId, sequenceId, null);
//		return "features";
//	}
//	
//	@RequestMapping(value="/project/{projectId}/sample/{sampleId}/sequence/{sequenceId}/ajax", method=RequestMethod.POST)
//	@ResponseBody
//	public FeatureTableResponse getFeatures(@Valid DataTableRequest data, @PathVariable Integer projectId, 
//			@PathVariable Integer sampleId,@PathVariable Integer sequenceId) throws NotFoundException {
//		
//		// Make sure we don't expose data from outside the User's object graph
//		checkOwnership(projectId, sampleId, sequenceId, null);
//
//		FeatureTableResponse response = new FeatureTableResponse();
//		List<Object[]> rows = getDataAdmin().getFeatureTable(
//				sequenceId, 
//				data.getIDisplayStart(), 
//				data.getIDisplayLength(),
//				data.getSortedColumnIndex(),
//				data.getSortingDirection());
//		response.setAaData(rows);
//		response.setsEcho(data.getSEcho());
//		Long numRec = getDataAdmin().getFeatureCount(sequenceId);
//		response.setiTotalRecords(numRec);
//		response.setiTotalDisplayRecords(numRec);
//		return response;
//	}	

//	@RequestMapping(value="/project/{projectId}/sample/{sampleId}/sequence/{sequenceId}/feature/{featureId}",method=RequestMethod.GET)
//	public String getAnnotations(@PathVariable Integer projectId, @PathVariable Integer sampleId, @PathVariable Integer sequenceId,
//			@PathVariable Integer featureId, Model model) throws NotFoundException {
//		getObjectGraph(model, projectId, sampleId, sequenceId, featureId);
//		return "annotations";
//	}
//	
//	@RequestMapping(value="/project/{projectId}/sample/{sampleId}/sequence/{sequenceId}/feature/{featureId}/ajax",method=RequestMethod.POST)
//	@ResponseBody
//	public AnnotationTableResponse getAnnotations(@Valid DataTableRequest data, @PathVariable Integer projectId, @PathVariable Integer sampleId,
//			@PathVariable Integer sequenceId, @PathVariable Integer featureId) throws NotFoundException {
//		
//		// Make sure we don't expose data from outside the User's object graph
//		checkOwnership(projectId, sampleId, sequenceId, featureId);
//
//		AnnotationTableResponse response = new AnnotationTableResponse();
//		// do table get
//		List<Object[]> rows = getDataAdmin().getAnnotationTable(
//				featureId,
//				data.getIDisplayStart(),
//				data.getIDisplayLength(),
//				data.getSortedColumnIndex(),
//				data.getSortingDirection());
//		response.setAaData(rows);
//		response.setsEcho(data.getSEcho());
//		Long numRec = getDataAdmin().getAnnotationCount(featureId);
//		response.setiTotalRecords(numRec);
//		response.setiTotalDisplayRecords(numRec);
//		return response;
//	}
}
