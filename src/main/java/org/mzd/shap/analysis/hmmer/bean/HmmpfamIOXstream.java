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
package org.mzd.shap.analysis.hmmer.bean;

import org.mzd.shap.analysis.hmmer.bean.Hmmpfam;
import org.mzd.shap.io.bean.BeanIOXstream;
import org.mzd.shap.io.bean.NamedCollectionConverter;

import com.thoughtworks.xstream.mapper.Mapper;

public class HmmpfamIOXstream extends BeanIOXstream<Hmmpfam> implements HmmpfamIO {
	public HmmpfamIOXstream() {
		super(Hmmpfam.class,Result.class,Query.class,GlobalHit.class,DomainHit.class);
		/*
		 *  Must register two explicit local converters for DomainHit and GlobalHit.
		 *  
		 *  As they contain the identical type alias "hit" they break XStream's 
		 *  assumption that all aliases are unique per XML instance. Therefore we 
		 *  explicit describe which class used be used in each case.
		 */
		Mapper mapper = getDelegate().getMapper();
		getDelegate().registerLocalConverter(Result.class, "domainHits", 
				new NamedCollectionConverter(mapper, "hit", DomainHit.class));
		getDelegate().registerLocalConverter(Result.class, "globalHits", 
				new NamedCollectionConverter(mapper, "hit", GlobalHit.class));
	}
}
