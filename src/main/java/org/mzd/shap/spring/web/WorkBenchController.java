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

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

@Controller
@RequestMapping("/bench")
public class WorkBenchController extends AbstractControllerSupport {
	private static final String SESSION_ATTR_ITEMID = "workbenchItems";
	
	@SuppressWarnings("unchecked")
	protected Collection<Integer> getSessionItemIds(HttpSession session) {
		Object mutex = WebUtils.getSessionMutex(session);
		synchronized (mutex) {
			Object obj = session.getAttribute(SESSION_ATTR_ITEMID);
			if (obj == null) {
				obj = new TreeSet<Integer>();
				session.setAttribute(SESSION_ATTR_ITEMID, obj);
			}
			return (Collection<Integer>)obj;
		}
	}
	
	@RequestMapping("/clear_ajax")
	@ResponseBody
	public Integer clearIds(HttpSession session) {
		Object mutex = WebUtils.getSessionMutex(session);
		synchronized (mutex) {
			getSessionItemIds(session).clear();
		}
		return countActive(session);
	}

	@RequestMapping("/del_ajax")
	@ResponseBody
	public Integer deleteIds(@RequestParam List<Integer> itemIds, HttpSession session) {
		Object mutex = WebUtils.getSessionMutex(session);
		synchronized (mutex) {
			getSessionItemIds(session).removeAll(itemIds);
		}
		return countActive(session);
	}
	
	@RequestMapping("/add_ajax")
	@ResponseBody
	public Integer addIds(@RequestParam List<Integer> itemIds, HttpSession session) {
		Object mutex = WebUtils.getSessionMutex(session);
		synchronized (mutex) {
			getSessionItemIds(session).addAll(itemIds);
		}
		return countActive(session);
	}
	
//	public Integer addIds(@RequestParam List<Integer> itemIds, HttpSession session) {
//		Object mutex = WebUtils.getSessionMutex(session);
//		synchronized (mutex) {
//			if (itemIds.size() > 0) {
//				getSessionItemIds(session).addAll(itemIds);
//			}
//		}
//		return countActive(session);
//	}
	
	@RequestMapping("/count_ajax")
	@ResponseBody
	public Integer countActive(HttpSession session) {
		return getSessionItemIds(session).size();
	}
}
