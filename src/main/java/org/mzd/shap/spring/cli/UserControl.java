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
package org.mzd.shap.spring.cli;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.mzd.shap.ApplicationException;
import org.mzd.shap.domain.authentication.UserAdminService;
import org.mzd.shap.domain.authentication.UserView;

public class UserControl extends BaseCommand {
	private final static Option NAME = CommandLineApplication.buildOption()
		.withLongName("name")
		.withDescription("Real Name")
		.withRequired(false)
		.withArgument(CommandLineApplication.buildArgument()
				.withName("name")
				.withMinimum(1)
				.withMaximum(1)
				.create())
		.create();
	
	private final static Option USERNAME = CommandLineApplication.buildOption()
		.withLongName("username")
		.withDescription("SHAP login username")
		.withRequired(true)
		.withArgument(CommandLineApplication.buildArgument()
				.withName("username")
				.withMinimum(1)
				.withMaximum(1)
				.create())
		.create();
	
	private final static Option PASSWORD = CommandLineApplication.buildOption()
		.withLongName("password")
		.withDescription("SHAP login password")
		.withRequired(true)
		.withArgument(CommandLineApplication.buildArgument()
				.withName("password")
				.withMinimum(1)
				.withMaximum(1)
				.create())
		.create();
	
	private final static Option ADMIN = CommandLineApplication.buildOption()
		.withLongName("admin")
		.withDescription("User has administration privileges")
		.withRequired(false)
		.create();
	
	private final static Option CREATE = CommandLineApplication.buildOption()
		.withLongName("create")
		.withDescription("Create a new user")
		.withChildren(CommandLineApplication.buildGroup()
				.withOption(NAME)
				.withOption(USERNAME)
				.withOption(PASSWORD)
				.withOption(ADMIN)
				.create())
		.create();
	
	private final static Option DELETE = CommandLineApplication.buildOption()
		.withLongName("delete")
		.withDescription("Delete an existing user")
		.withChildren(CommandLineApplication.buildGroup()
				.withOption(USERNAME)
				.create())
		.create();
	
	public UserControl() {
		Group actionGroup = CommandLineApplication.buildGroup()
			.withName("Action")
			.withDescription("Action to perform")
			.withOption(CREATE)
			.withOption(DELETE)
			.withMinimum(1)
			.withMaximum(1)
			.create();
		
		setApp(new CommandLineApplication(actionGroup));
	}
	
	private void validate(UserView user) throws ApplicationException {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<UserView>> violations = validator.validate(user);
		if (violations.size() > 0) {
			for (ConstraintViolation<UserView> v : violations) {
				System.err.println(v.getPropertyPath() + " " + v.getMessage());
			}
			throw new ApplicationException("Invalid definition of new user");
		}
	}
	
	@Override
	public void execute(String[] args) {
		try {
			CommandLine cl = getApp().parseArguments(args);
			
			String[] xmlPath = {
					"datasource-context.xml",
					"service-context.xml",
					"orm-context.xml"
			};
			
			getApp().startApplication(xmlPath, true);

			UserAdminService userService = (UserAdminService)getApp().getContext()
				.getBean("localUserDetailsService");
			
			if (cl.hasOption(CREATE)) {
				UserView user;
				if (cl.hasOption(ADMIN)) {
					user = UserView.createAdminUser(
							(String)cl.getValue(NAME),
							(String)cl.getValue(USERNAME),
							(String)cl.getValue(PASSWORD));
				}
				else {
					user = UserView.createUser(
							(String)cl.getValue(NAME),
							(String)cl.getValue(USERNAME),
							(String)cl.getValue(PASSWORD));
				}
				validate(user);
				userService.createUser(user);
			}
			else if (cl.hasOption(DELETE)) {
				userService.deleteUser((String)cl.getValue(USERNAME));
			}
		}
		catch (Throwable t) {
			System.err.println(t.getMessage());
			System.exit(1);
		}
	}

	
	public static void main(String[] args) {
		new UserControl().execute(args);
		System.exit(0);
	}
}
