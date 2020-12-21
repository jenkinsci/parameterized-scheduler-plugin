package org.jenkinsci.plugins.parameterizedscheduler;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.PeriodicWork;
import hudson.triggers.Trigger;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class Cron extends PeriodicWork {
	private static final Logger LOGGER = Logger.getLogger(Cron.class.getName());

	@Override
	public long getRecurrencePeriod() {
		long period = TimeUnit.MINUTES.toMillis(1);
		LOGGER.log(Level.FINEST, "period set to {0}", period);
		return period;
	}

	@Override
	public long getInitialDelay() {
		return MIN - (Calendar.getInstance().get(Calendar.SECOND) * 1000);
	}

	@Override
	protected void doRun() throws Exception {
		Jenkins instance = Jenkins.get();

		for (AbstractProject<?, ?> project : instance.allItems(AbstractProject.class)) {
			checkTriggers(project.getName(), project.getTriggers().values(), new GregorianCalendar());
		}

		for (WorkflowJob workflowJob : instance.allItems(WorkflowJob.class)) {
			checkTriggers(workflowJob.getName(), workflowJob.getTriggers().values(), new GregorianCalendar());
		}
	}

	private void checkTriggers(String projectName, Collection<Trigger<?>> triggers, Calendar calendar) {

		for (Trigger<?> trigger : triggers) {
			if (trigger instanceof ParameterizedTimerTrigger) {
				LOGGER.log(Level.FINE, "cron checking {0}", projectName);
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
