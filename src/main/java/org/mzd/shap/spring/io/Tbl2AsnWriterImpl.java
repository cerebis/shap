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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Sequence;
import org.mzd.shap.domain.SequenceException;
import org.mzd.shap.domain.dao.SequenceDao;
import org.mzd.shap.io.Tbl2AsnWriter;


public class Tbl2AsnWriterImpl implements Tbl2AsnWriter {
	private SequenceDao sequenceDao;
	
	public void writeAll(File outputDir) throws Exception {
		
		List<Sequence> seqList = getSequenceDao().findAll(null);
		
		for (Sequence seq : seqList) {
			String baseName = seq.getQueryId();
			
			// Write Fasta File
			seq.toFastaFile(new File(outputDir, baseName + ".fsa"), false);
			
			// Write Feature Table
			BufferedWriter wr = new BufferedWriter(new FileWriter(new File(outputDir, baseName + ".tbl")));
			wr.write(">Feature " + baseName + "\n");
			for (Feature feat : seq.getFeatures()) {
				int start,end;
				switch (feat.getLocation().getStrand()) {
				case Forward:
					start = feat.getLocation().getStart() + 1;
					end = feat.getLocation().getEnd() + 1;
					break;
				case Reverse:
					end = feat.getLocation().getStart() + 1;
					start = feat.getLocation().getEnd() + 1;
					break;
				default:
					throw new SequenceException("Strand enum [" + feat.getLocation().getStrand() + "] not supported");
				}
				
				String geneName = String.format("orf%d.%d", seq.getId(), feat.getId());
				String product = "unknown";
				for (Annotation an : feat.getAnnotations()) {
					if (an.getDescription() != null) {
						product = an.getDescription();
						break;
					}
				}
				wr.write(start + "\t" + end + "\tgene\n");
				wr.write("\t\t\tgene\t" + geneName + "\n");
				wr.write(start + "\t" + end + "\tCDS\n");
				wr.write("\t\t\tproduct " + product + "\n");
			}
			wr.close();
		}
	}

	public SequenceDao getSequenceDao() {
		return sequenceDao;
	}
	public void setSequenceDao(SequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}
}
