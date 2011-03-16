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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.mzd.shap.domain.FeatureException;
import org.mzd.shap.spring.BatchAdminService;
import org.mzd.shap.spring.JobDaemon;
import org.mzd.shap.spring.NotFoundException;
import org.mzd.shap.spring.plan.Plan;
import org.mzd.shap.spring.plan.PlanException;
import org.mzd.shap.spring.plan.PlanIO;
import org.mzd.shap.spring.plan.PlanIOXstream;
import org.mzd.shap.spring.plan.PlanValidator;
import org.mzd.shap.spring.plan.Step;
import org.mzd.shap.spring.plan.Target;
import org.mzd.shap.spring.task.Job;

public class JobControl extends BaseCommand {
	private static BatchAdminService batchAdminService;
	private static JobDaemon jobDaemon;
	
	public List<Job> createJobs(Plan plan) throws FeatureException, NotFoundException {
		
		List<Job> submittedJobs = new ArrayList<Job>();

		for (Target target : plan.getTargets()) {
			Job combinedJob = new Job();
			combinedJob.setComment(plan.getId());
			for (Step step : plan.getSteps()) {
				System.out.println("Building submission for step: [" + step + "]");
				switch (step.getType()) {
				case DETECTION:
					combinedJob.addTasks(
							batchAdminService.newDetectionStep(target,step));
					break;
		
				case ANNOTATION:			
					combinedJob.addTasks(
							batchAdminService.newAnnotationStep(target,step));
					break;
				}
			}
			
			// Persist job
			submittedJobs.add(
					batchAdminService.saveNewJob(combinedJob));
		}
		
		return submittedJobs;
	}
	
	public void submitAndWait(Plan plan) throws PlanException, FeatureException, NotFoundException {
		PlanValidator.validate(plan);
		createJobs(plan);
		System.out.println("Submission completed, beginning processing.");
		waitOnWork();
	}

	public void restartAndWait() throws NotFoundException {
		waitOnWork();
	}

	public void waitOnWork() {
		while (jobDaemon.pendingWork()) {
			synchronized (this) {
				try {
					wait(3000);
				}
				catch (InterruptedException ex) {
					System.err.println(ex);
				}
			}
		}
	}

	private final static Option SUBMIT = CommandLineApplication.buildOption()
		.withLongName("submit")
		.withDescription("Submit a new job")
		.withArgument(CommandLineApplication.buildArgument()
				.withName("plan xml file")
				.withMinimum(1)
				.withMaximum(1)
				.withValidator(CommandLineApplication.getExitisngFileValidator(true, false))
				.create())
		.create();

	private final static Option RESTART = CommandLineApplication.buildOption()
		.withLongName("restart")
		.withDescription("Restart job processing")
		.create();

	public JobControl() {
		Group actionGroup = CommandLineApplication.buildGroup()
			.withName("Action")
			.withDescription("Action to preform")
//			.withRequired(true)
			.withMinimum(1)
			.withMaximum(1)
			.withOption(SUBMIT)
			.withOption(RESTART)
			.create();
		
		setApp(new CommandLineApplication(actionGroup));
	}
	
	public void execute(String[] args) {
		try {
			CommandLine cl = getApp().parseArguments(args);
			
			String[] xmlPath = {
					"delegate-context.xml",
					"datasource-context.xml",
					"task-context.xml",
					"service-context.xml",
					"orm-context.xml"
			};
			
			getApp().startApplication(xmlPath, true);
			
			batchAdminService = (BatchAdminService)getApp().getContext().getBean("batchAdminService");
			jobDaemon = (JobDaemon)getApp().getContext().getBean("jobDaemon");

			if (cl.hasOption(SUBMIT)) {
				File inputXml = (File)cl.getValue(SUBMIT);
				PlanIO beanIO = new PlanIOXstream();
				Plan plan = beanIO.read(inputXml);
				submitAndWait(plan);
			}
			else if (cl.hasOption(RESTART)) {
				restartAndWait();
			}
		}
		catch (Throwable t) {
			System.err.println(t.getMessage());
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		new JobControl().execute(args);
		System.exit(0);
	}
}
