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

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.mzd.shap.ApplicationException;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.MoleculeType;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.io.FastaWriter;
import org.mzd.shap.io.GenbankWriter;
import org.mzd.shap.io.TableWriter;
import org.mzd.shap.spring.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/export")
public class DataExportController extends AbstractControllerSupport {
	private GenbankWriter genbankWriter;
	private TableWriter genbankTableWriter;
	private TableWriter tableWriter;
	private FastaWriter fastaWriter;
	
	protected MoleculeType getMoleculeType(boolean isProtein) {
		return isProtein ? MoleculeType.Protein : MoleculeType.DNA;
	}

	protected void setAsAttachment(String fileName, HttpServletResponse response) {
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
	}
	
	protected String buildName(String baseName, boolean isSequence, boolean isProtein) {
		if (isSequence) {
			return baseName + ".fna";
		}
		else {
			if (isProtein) {
				return baseName + "-protein.faa";
			}
			else {
				return baseName + "-gene.fna";
			}
		}
	}
	
	@RequestMapping("/object/{id}/fasta")
	public void getObjectFasta(@PathVariable Integer id,
			@RequestParam(value="seq",required=false,defaultValue="true") Boolean isSequence, 
			@RequestParam(value="aa",required=false,defaultValue="false") Boolean isProtein,
			HttpServletResponse response) throws IOException, ApplicationException {
		
		Object obj = getDataAdmin().getObject(id, null);
		if (obj == null) {
			throw new NotFoundException("Failed to retrieve an object using the identifier [" + id + "]");
		}
		
		if (obj instanceof Sample) {
			Sample sample = (Sample)obj;
			
			setAsAttachment(
					buildName(sample.getName(), isSequence, isProtein),
					response);
			
			if (isSequence) {
				// Request is for source DNA
				getFastaWriter().writeSequences(sample, response.getWriter(), null);
			}
			else {
				// Request is for genes
				MoleculeType molType = getMoleculeType(isProtein);
				getFastaWriter().writeFeatures(sample, molType, response.getWriter(), false, null);
			}
		}
		else if (obj instanceof Sequence) {
			Sequence sequence = (Sequence)obj;
			
			setAsAttachment(
					buildName(sequence.getId().toString(), isSequence, isProtein),
					response);
			
			if (isSequence) {
				// Request is for source DNA
				getFastaWriter().writeSequence(sequence,response.getWriter());
			}
			else {
				// Request is for genes
				MoleculeType molType = getMoleculeType(isProtein);
				getFastaWriter().writeFeatures(sequence, molType, response.getWriter(), false);
			}
		}
		else if (obj instanceof Feature) {
			Feature feature = (Feature)obj;
			
			setAsAttachment(
					buildName(feature.getId().toString(), isSequence, isProtein),
					response);
			
			MoleculeType molType = getMoleculeType(isProtein);
			getFastaWriter().write(feature, molType, response.getWriter(), false);
		}
		else {
			throw new ApplicationException("Requested object is not supported for export");
		}
	}

	@RequestMapping("/object/{id}/genbanktable")
	public void getObjectGenbankAnnotationTable(
			@PathVariable Integer id, 
			HttpServletResponse response) throws IOException, ApplicationException {
		
		Object obj = getDataAdmin().getObject(id,null);
		if (obj == null) {
			throw new NotFoundException("Failed to retrieve an object using the identifier [" + id + "]");
		}
		
		if (obj instanceof Sequence) {
			Sequence sequence = (Sequence)obj;
			setAsAttachment(sequence.getId() + ".tbl", response);
			getGenbankTableWriter().writeFeatures(sequence,response.getWriter());
		}
		else {
			throw new ApplicationException("Requested object is not supported for export");
		}
	}

	@RequestMapping("/object/{id}/table")
	public void getObjectAnnotationTable(
			@PathVariable Integer id, 
			HttpServletResponse response) throws IOException, ApplicationException {
		
		Object obj = getDataAdmin().getObject(id,null);
		if (obj == null) {
			throw new NotFoundException("Failed to retrieve an object using the identifier [" + id + "]");
		}
		
		if (obj instanceof Sample) {
			Sample sample = (Sample)obj;
			setAsAttachment(sample.getName() + ".csv", response);
			getTableWriter().writeFeatures(sample,null,response.getWriter());
		}
		else if (obj instanceof Sequence) {
			Sequence sequence = (Sequence)obj;
			setAsAttachment(sequence.getId() + ".csv", response);
			getTableWriter().writeFeatures(sequence,response.getWriter());
		}
		else {
			throw new ApplicationException("Requested object is not supported for export");
		}
	}

	@RequestMapping("/object/{id}/genbank")
	public void getObjectGenbank(
			@PathVariable Integer id, 
			HttpServletResponse response) throws IOException, ApplicationException {
		
		Object obj = getDataAdmin().getObject(id,null);
		if (obj == null) {
			throw new NotFoundException("Failed to retrieve an object using the identifier [" + id + "]");
		}
		
		if (obj instanceof Sequence) {
			Sequence sequence = (Sequence)obj;
			setAsAttachment(sequence.getId() + ".gbf", response);
			getGenbankWriter().write(sequence,response.getWriter());
		}
		else {
			throw new ApplicationException("Requested object is not supported for export");
		}
	}

//	@RequestMapping("/project/{projectId}/sample/{sampleId}/fasta")
//	public void getSampleFasta(
//			@PathVariable Integer projectId, 
//			@PathVariable Integer sampleId,
//			@RequestParam("seq") Boolean isSequence, 
//			@RequestParam(value="aa",required=false,defaultValue="false") Boolean isProtein,
//			HttpServletResponse response) throws IOException, ApplicationException {
//		
//		checkOwnership(projectId, sampleId, null, null);
//		
//		Sample sample = getDataAdmin().getSample(sampleId);
//		setAsAttachment(sample.getName() + ".fasta", response);
//		if (isSequence) {
//			getFastaWriter().writeSequences(sample, response.getWriter(), null);
//		}
//		else {
//			MoleculeType molType = getMoleculeType(isProtein);
//			getFastaWriter().writeFeatures(sample, molType, response.getWriter(), false, null);
//		}
//	}
		
//	@RequestMapping("/project/{projectId}/sample/{sampleId}/table")
//	public void getSampleAnnotationTable(
//			@PathVariable Integer projectId,
//			@PathVariable Integer sampleId, 
//			HttpServletResponse response) throws IOException, ApplicationException {
//		
//		checkOwnership(projectId, sampleId, null, null);
//
//		Sample sample = getDataAdmin().getSample(sampleId);
//		setAsAttachment(sample.getName() + ".csv", response);
//		getTableWriter().writeFeatures(sample,null,response.getWriter());
//	}
	
//	@RequestMapping("/project/{projectId}/sample/{sampleId}/sequence/{sequenceId}/fasta")
//	public void getSequenceFasta(
//			@PathVariable Integer projectId,
//			@PathVariable Integer sampleId, 
//			@PathVariable Integer sequenceId,
//			@RequestParam("seq") Boolean isSequence,
//			@RequestParam(value="aa",required=false,defaultValue="false") Boolean isProtein,
//			HttpServletResponse response) throws IOException, ApplicationException {
//		
//		checkOwnership(projectId, sampleId, sequenceId, null);
//
//		Sequence sequence = getDataAdmin().getSequence(sequenceId);
//		setAsAttachment(sequence.getId() + ".fasta", response);
//		if (isSequence) {
//			getFastaWriter().writeSequence(sequence,response.getWriter());
//		}
//		else {
//			MoleculeType molType = getMoleculeType(isProtein);
//			getFastaWriter().writeFeatures(sequence, molType, response.getWriter(), false);
//		}
//	}

//	@RequestMapping("/project/{projectId}/sample/{sampleId}/sequence/{sequenceId}/genbank")
//	public void getSequenceGenbank(
//			@PathVariable Integer projectId,
//			@PathVariable Integer sampleId, 
//			@PathVariable Integer sequenceId, 
//			HttpServletResponse response) throws IOException, ApplicationException {
//		
//		checkOwnership(projectId, sampleId, sequenceId, null);
//
//		Sequence sequence = getDataAdmin().getSequence(sequenceId);
//		setAsAttachment(sequence.getId() + ".gbk", response);
//		getGenbankWriter().write(sequence,response.getWriter());
//	}
	
//	@RequestMapping("/project/{projectId}/sample/{sampleId}/sequence/{sequenceId}/table")
//	public void getSequenceAnnotationTable(
//			@PathVariable Integer projectId, 
//			@PathVariable Integer sampleId, 
//			@PathVariable Integer sequenceId,
//			HttpServletResponse response) throws IOException, ApplicationException {
//		
//		checkOwnership(projectId, sampleId, sequenceId, null);
//
//		Sequence sequence = getDataAdmin().getSequence(sequenceId);
//		setAsAttachment(sequence.getId() + ".csv", response);
//		getTableWriter().writeFeatures(sequence,response.getWriter());
//	}

//	@RequestMapping("/project/{projectId}/sample/{sampleId}/sequence/{sequenceId}/feature/{featureId}/fasta")
//	public void getFeatureFasta(
//			@PathVariable Integer projectId,
//			@PathVariable Integer sampleId, 
//			@PathVariable Integer sequenceId,
//			@PathVariable Integer featureId,
//			@RequestParam(value="aa",required=false,defaultValue="false") Boolean isProtein,
//			HttpServletResponse response)
//			throws NotFoundException, FeatureException, SequenceException, IOException {
//
//		checkOwnership(projectId, sampleId, sequenceId, featureId);
//		
//		Feature feature = getDataAdmin().getFeature(featureId);
//		setAsAttachment(feature.getId() + ".fasta", response);
//		MoleculeType molType = getMoleculeType(isProtein);
//		getFastaWriter().write(feature, molType, response.getWriter(), false);
//	}

	@Autowired
	public void setGenbankWriter(GenbankWriter genbankWriter) {
		this.genbankWriter = genbankWriter;
	}
	public GenbankWriter getGenbankWriter() {
		return genbankWriter;
	}

	@Autowired
	public void setGenbankTableWriter(TableWriter genbankTableWriter) {
		this.genbankTableWriter = genbankTableWriter;
	}
	public TableWriter getGenbankTableWriter() {
		return genbankTableWriter;
	}
	
	@Autowired
	public void setTableWriter(TableWriter tableWriter) {
		this.tableWriter = tableWriter;
	}
	public TableWriter getTableWriter() {
		return tableWriter;
	}

	@Autowired
	public void setFastaWriter(FastaWriter fastaWriter) {
		this.fastaWriter = fastaWriter;
	}
	public FastaWriter getFastaWriter() {
		return fastaWriter;
	}
	
}
