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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.mzd.shap.ApplicationException;
import org.mzd.shap.constraints.MinimalQuery;
import org.mzd.shap.hibernate.search.SearchResult;
import org.mzd.shap.hibernate.search.view.Report;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/search")
public class DataSearchController extends AbstractControllerSupport {
	
	/**
	 * Command Object for Lucene based general search.
	 */
	public static class LuceneQuery {
		@MinimalQuery
		private String queryText;
		public String getQueryText() {return queryText;}
		public void setQueryText(String queryText) {this.queryText = queryText;}
	}
	
	public static class LuceneQueryPaged {
		@MinimalQuery
		private String queryText;
		@Min(0)
		private Integer first = 0;
		@Min(10)
		@Max(100)
		private Integer max = 10;
		
		public String getQueryText() {
			return queryText;
		}
		public void setQueryText(String queryText) {
			this.queryText = queryText;
		}
		
		public Integer getFirst() {
			return first;
		}
		public void setFirst(Integer first) {
			this.first = first;
		}

		public Integer getMax() {
			return max;
		}
		public void setMax(Integer max) {
			this.max = max;
		}
	}

	@RequestMapping("/form")
	public String getMain(LuceneQuery query) {
		return "search/searchTab";
	}
	
	@RequestMapping("/validate_ajax")
	@ResponseBody
	public Boolean validateQuery(@Valid LuceneQuery luceneQuery, BindingResult result) {
		return !result.hasErrors();
	}

	@RequestMapping("/query_json")
	@ResponseBody
	public SearchResult<Report> searchJson(@Valid LuceneQueryPaged luceneQuery, BindingResult result) throws ApplicationException {
		if (result.hasErrors()) {
			List<ObjectError> errors = result.getAllErrors();
			for (ObjectError e : errors) {
				getLogger().warn(e);
			}
			return null;
		}
		return getDataAdmin().getReports(luceneQuery.getQueryText(), luceneQuery.getFirst(), luceneQuery.getMax());
	}
	
	//	@ModelAttribute("targetTypes")
//	public List<DomainTarget> getTargetTypes() {
//		return Arrays.asList(DomainTarget.values());
//	}
	
//	@ModelAttribute("fetchQuery")
//	public FindByIdQuery getFetchQuery() {
//		return new FindByIdQuery();
//	}
	
	/**
	 * Convert data from reader into a list of Integers.
	 * 
	 * @param reader
	 * @param fieldName
	 * @param result
	 * @return
	 * @throws IOException
	 */
//	protected Set<Integer> readIdList(BufferedReader reader, String fieldName, BindingResult result) throws IOException {
//		Set<Integer> idList = new HashSet<Integer>();
//		while (true) {
//			String line = reader.readLine();
//			if (line == null) {
//				break;
//			}
//			line = line.trim();
//			try {
//				idList.add(Integer.parseInt(line));
//			}
//			catch (NumberFormatException ex) {
//				result.rejectValue(fieldName, "validation.data.number","Conversion error on line: " + line);
//			}
//		}
//		return idList;
//	}
	
//	@RequestMapping(value="/id",method=RequestMethod.GET)
//	public String findFeatures(FindByIdQuery query) {
//		return "findById";
//	}
	
//	@RequestMapping(value="/id",method=RequestMethod.POST)
//	public String findFeatures(
//			@Valid FindByIdQuery query, BindingResult result, Model model) throws IOException, ApplicationException {
//
//		BufferedReader reader = null;
//		Set<Integer> idSet = new HashSet<Integer>();
//		boolean isValid = true;
//		
//		// Must supply at least one of either field Ids or textIds
//		if (query.getFileIds() == null && query.getTextIds() == null) {
//			result.reject("validation.data.null","Submission contained no data");
//			isValid = false;
//		}
//		else {
//			try {
//				if (query.getFileIds() != null && !query.getFileIds().isEmpty()) {
//					reader = new BufferedReader(new InputStreamReader(query.getFileIds().getInputStream()));
//					idSet.addAll(readIdList(reader,"fileIds",result));
//				}
//				
//				if (query.getTextIds() != null) {
//					reader = new BufferedReader(new StringReader(query.getTextIds()));
//					idSet.addAll(readIdList(reader,"textIds",result));
//				}
//				
//				if (result.hasErrors()) {
//					isValid = false;
//				}
//				if (idSet.size() == 0) {
//					result.reject("validation.data.empty","No accepted identifiers in submission");
//					isValid = false;
//				}
//			}
//			finally {
//				if (reader != null) {
//					reader.close();
//				}
//			}
//		}
//
//		if (!isValid) {
//			return "findById";
//		}
//
//		// If we used a projection with generic columns, we could return one view. TODO
//		switch (query.getTarget()) {
//		case FEATURE:
//			model.addAttribute("features",getDataAdmin().getFeatures(new ArrayList<Integer>(idSet)));
//			return "resultFeatureTable";
//		case SEQUENCE:
//			model.addAttribute("sequences",getDataAdmin().getSequences(new ArrayList<Integer>(idSet)));
//			return "resultSequenceTable";
//		default:
//			return "findById";
//		}
//	}
	
//	@RequestMapping(value="/fetch_ajax",method=RequestMethod.POST)
//	public String fetchAjax(@Valid FindByIdQuery query, BindingResult result, Model model) throws IOException, ApplicationException {
//
//		BufferedReader reader = null;
//		Set<Integer> idSet = new HashSet<Integer>();
//		boolean isValid = true;
//		
//		// Must supply at least one of either field Ids or textIds
//		if (query.getFileIds() == null && query.getTextIds() == null) {
//			result.reject("validation.data.null","Submission contained no data");
//			isValid = false;
//		}
//		else {
//			try {
//				if (query.getFileIds() != null && !query.getFileIds().isEmpty()) {
//					reader = new BufferedReader(new InputStreamReader(query.getFileIds().getInputStream()));
//					idSet.addAll(readIdList(reader,"fileIds",result));
//				}
//				
//				if (query.getTextIds() != null) {
//					reader = new BufferedReader(new StringReader(query.getTextIds()));
//					idSet.addAll(readIdList(reader,"textIds",result));
//				}
//				
//				if (result.hasErrors()) {
//					isValid = false;
//				}
//				if (idSet.size() == 0) {
//					result.reject("validation.data.empty","No accepted identifiers in submission");
//					isValid = false;
//				}
//			}
//			finally {
//				if (reader != null) {
//					reader.close();
//				}
//			}
//		}
//
//		if (!isValid) {
//			return "main";
//		}
//
//		// If we used a projection with generic columns, we could return one view. TODO
//		switch (query.getTarget()) {
//		case FEATURE:
//			model.addAttribute("features",getDataAdmin().getFeatures(new ArrayList<Integer>(idSet)));
//			return "resultFeatureTable";
//		case SEQUENCE:
//			model.addAttribute("sequences",getDataAdmin().getSequences(new ArrayList<Integer>(idSet)));
//			return "resultSequenceTable";
//		default:
//			return "main";
//		}
//	}

}
