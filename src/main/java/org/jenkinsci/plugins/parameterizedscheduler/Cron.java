package org.jenkinsci.plugins.parameterizedscheduler;

import hudson.Extension;
import hudson.model.PeriodicWork;
import hudson.model.AbstractProject;
import hudson.triggers.Trigger;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

@Extension
public class Cron extends PeriodicWork {
	private static final Logger LOGGER = Logger.getLogger(Cron.class.getName());

	@Override
	public long getRecurrencePeriod() {
		long period = TimeUnit.MINUTES.toMillis(1);
		LOGGER.warning("period set to " + period);
		return period;
	}

	@Override
	protected void doRun() throws Exception {
		LOGGER.finer("dorun-run");

		Jenkins instance = Jenkins.getInstance();

		if (instance == null) {
			LOGGER.severe("Jenkins not initialized");
			return;
		}

		for (AbstractProject<?, ?> project : instance.getAllItems(AbstractProject.class)) {
			checkTriggers(project.getName(), project.getTriggers().values(), new GregorianCalendar());
		}

		if (instance.getPlugin("workflow-job") != null) {
			for (WorkflowJob workflowJob : instance.getAllItems(WorkflowJob.class)) {
				checkTriggers(workflowJob.getName(), workflowJob.getTriggers().values(), new GregorianCalendar());
			}
		}
	}

	private void checkTriggers(String projectName, Collection<Trigger<?>> triggers, Calendar calendar) {

		for (Trigger<?> trigger : triggers) {
			if (trigger instanceof ParameterizedTimerTrigger) {
				LOGGER.fine("cron checking " + projectName);
				ParameterizedTimerTrigger ptTrigger = (ParameterizedTimerTrigger) trigger;

				try {
					ptTrigger.checkCronTabsAndRun(calendar);
				} catch (Throwable e) {
					// t.run() is a plugin, and some of them throw RuntimeException and other things.
					// don't let that cancel the polling activity. report and move on.
					LOGGER.log(Level.WARNING,
							trigger.getClass().getName() + ".run() failed for " + projectName, e);
				}
			}
		}
	}

}
