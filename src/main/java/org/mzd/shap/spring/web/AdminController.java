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

import org.mzd.shap.domain.authentication.UserAdminService;
import org.mzd.shap.domain.authentication.UserAlreadyExistsException;
import org.mzd.shap.domain.authentication.UserView;
import org.mzd.shap.spring.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminController extends AbstractControllerSupport {
	private UserAdminService userAdminService;
	
	public static class CommandResponse {
		private static final String SUCCESS_MSG = "Command was successful";
		private Boolean statusOk;
		private String message;
		
		public CommandResponse(Boolean statusOk, String message) {
			this.statusOk = statusOk;
			this.message = message;
		}
		
		public Boolean getStatusOk() {
			return statusOk;
		}
		public void setStatusOk(Boolean statusOk) {
			this.statusOk = statusOk;
		}
		
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		
		public static CommandResponse success() {
			return new CommandResponse(true, SUCCESS_MSG);
		}
		public static CommandResponse failure(String message) {
			return new CommandResponse(false, message);
		}
	}
	
	@RequestMapping
	public String getUserAdmin(Model model) throws NotFoundException {
		addSessionUser(model);
		return "admin/user";
	}
	
	@RequestMapping("/userlist")
	@ResponseBody
	public List<UserView> getExistingUserList() {
		return getUserAdminService().listExistingUsers();
	}

	@RequestMapping("/rolelist")
	@ResponseBody
	public List<String> getRoleList() {
		return getUserAdminService().listRoles();
	}

	@RequestMapping("/delete")
	@ResponseBody
	public CommandResponse deleteUser(@RequestParam(required=true) String username) {
		try {
			getUserAdminService().deleteUser(username);
			return CommandResponse.success();
		} catch (NotFoundException ex) {
			getLogger().warn(ex.getMessage());
			return CommandResponse.failure(ex.getMessage());
		}
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@ResponseBody
	public CommandResponse updateUser(@Valid UserView user) {
		try {
			getUserAdminService().updateUser(user);
			return CommandResponse.success();
		} catch (NotFoundException ex) {
			getLogger().warn(ex.getMessage());
			return CommandResponse.failure(ex.getMessage());
		}
	}

	@RequestMapping(value="/create",method=RequestMethod.POST)
	@ResponseBody
	public CommandResponse createUser(@Valid UserView user) {
		try {
			getUserAdminService().createUser(user);
			return CommandResponse.success();
		} catch (UserAlreadyExistsException ex) {
			getLogger().warn(ex.getMessage());
			return CommandResponse.failure(ex.getMessage());
		} catch (NotFoundException ex) {
			getLogger().warn(ex.getMessage());
			return CommandResponse.failure(ex.getMessage());
		}
	}
	
	@Autowired
	public void setUserAdminService(UserAdminService userAdminService) {
		this.userAdminService = userAdminService;
	}
	public UserAdminService getUserAdminService() {
		return userAdminService;
	}
}
