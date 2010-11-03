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
package org.mzd.shap.spring.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.analysis.AnnotatorDao;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Taxonomy;
import org.mzd.shap.domain.dao.SampleDao;
import org.mzd.shap.io.ReportWriter;
import org.mzd.shap.spring.NotFoundException;


public class AnnotatorHistographicReportWriter implements ReportWriter {
	private SampleDao sampleDao;
	private AnnotatorDao annotatorDao;
	
	@SuppressWarnings("unchecked")
	public void write(OutputStream outputStream, Object... objects) throws IOException, NotFoundException {
		
		Sample sample = (Sample)objects[0];
		String annotatorName = (String)objects[1];
		Double confidence = (Double)objects[2];
		Collection<Taxonomy> excludedTaxons = (Collection<Taxonomy>)objects[3];

		Annotator ar = getAnnotatorDao().findByField("name", annotatorName);
		if (ar == null) {
			throw new NotFoundException("An annotator named [" + annotatorName + "] was not found");
		}

		// Create an informative preamble.
		StringBuffer header = new StringBuffer();
		header.append("#Annotation Histogram\n");
		header.append("#Sample: " + sample.getName() + "\n");
		header.append("#Annotator: " + ar.getName() + "\n");
		header.append("#Confidence: " + confidence + "\n");
		header.append("#Excluded Taxons:");
		if (excludedTaxons != null && excludedTaxons.size() > 0) {
			for (Taxonomy tx : excludedTaxons) {
				header.append(" " + tx.name());
			}
			header.append("\n");
		}
		else {
			header.append(" none\n");
		}
		header.append("#ACCESSION\tFREQUENCY\tWEIGHTED_FREQUENCY\tDESCRIPTION\n");
		outputStream.write(header.toString().getBytes());
		
		// TODO ADD CONFIDENCE
		
		// Write out table rows
		List<AnnotationHistogramDTO> resultSet = getSampleDao()
			.annotationHistogram(sample, ar, confidence, excludedTaxons);
		
		for (AnnotationHistogramDTO row : resultSet) {
			row.write(outputStream);
		}
	}

	public void write(File outputFile, Object... objects) throws IOException, NotFoundException {
		BufferedOutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(outputFile));
			write(os,objects);
		}
		finally {
			if (os != null) {
				os.close();
			}
		}
	}
	
	public SampleDao getSampleDao() {
		return sampleDao;
	}
	public void setSampleDao(SampleDao sampleDao) {
		this.sampleDao = sampleDao;
	}

	public AnnotatorDao getAnnotatorDao() {
		return annotatorDao;
	}
	public void setAnnotatorDao(AnnotatorDao annotatorDao) {
		this.annotatorDao = annotatorDao;
	}
	
}
