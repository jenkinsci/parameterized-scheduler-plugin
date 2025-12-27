package org.jenkinsci.plugins.parameterizedscheduler;

import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Job;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.scheduler.Hash;
import hudson.triggers.Trigger;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link Trigger} that runs a job periodically with support for parameters.
 * 
 * @author jameswilson
 *
 */
@SuppressWarnings("rawtypes")
public class ParameterizedTimerTrigger extends Trigger<Job> {
	private static final Logger LOGGER = Logger.getLogger(ParameterizedTimerTrigger.class.getName());
	private transient ParameterizedCronTabList cronTabList;
	private final String parameterizedSpecification;

	@DataBoundConstructor
	public ParameterizedTimerTrigger(String parameterizedSpecification) {
		this.parameterizedSpecification = parameterizedSpecification;
		this.cronTabList = ParameterizedCronTabList.create(parameterizedSpecification);
	}

	public ParameterizedCronTabList getCronTabList() {
		return cronTabList;
	}

	@Override
	public void run() {
		LOGGER.fine("tried to run from base Trigger, nothing will happen");
	}

	/**
	 * this method started out as hudson.model.AbstractProject.getDefaultParametersValues()
	 * @param parameterValues A map of parameters and their values
	 * @return the ParameterValues as set from the crontab row or their defaults
	 */
	private List<ParameterValue> configurePropertyValues(Map<String, String> parameterValues) {
		assert job != null : "job must not be null if this was 'started'";
		ParametersDefinitionProperty paramDefProp = (ParametersDefinitionProperty) job
				.getProperty(ParametersDefinitionProperty.class);
		List<ParameterValue> defValues = new ArrayList<>();

		/* Scan for all parameter with an associated default values */
		for (ParameterDefinition paramDefinition : paramDefProp.getParameterDefinitions()) {
			ParameterValue defaultValue = paramDefinition.getDefaultParameterValue();

			if (parameterValues.containsKey(paramDefinition.getName())) {
				ParameterizedStaplerRequest request = new ParameterizedStaplerRequest(
						parameterValues.get(paramDefinition.getName()));
				ParameterValue value = paramDefinition.createValue(request);
				if (value!= null) {
					defValues.add(value);
				} else {
					LOGGER.warning("Cannot create value for " + paramDefinition.getName());
				}
			} else if (defaultValue != null) {
				defValues.add(defaultValue);
			}
		}

		return defValues;
	}

	public void checkCronTabsAndRun(Calendar calendar) {
		LOGGER.fine("checking and maybe running at " + calendar);
		List<ParameterizedCronTab> cronTabs = cronTabList.check(calendar);

		cronTabs.forEach(cronTab -> {
			Map<String, String> parameterValues = cronTab.getParameterValues();
			ParametersAction parametersAction = new ParametersAction(configurePropertyValues(parameterValues));
			assert job != null : "job must not be null, if this was 'started'";
			if (job instanceof AbstractProject) {
				((AbstractProject) job).scheduleBuild2(0, (Cause)null, causeAction(parameterValues), parametersAction);
			} else if (job instanceof WorkflowJob) {
				((WorkflowJob) job).scheduleBuild2(0, causeAction(parameterValues), parametersAction);
			}
		});
	}

	private CauseAction causeAction(Map<String, String> parameterValues) {
		return new CauseAction(new ParameterizedTimerTriggerCause(parameterValues));
	}

	@Override
	public void start(Job project, boolean newInstance) {
		this.job = project;

		try {// reparse the tabs with the job as the hash
			cronTabList = ParameterizedCronTabList.create(parameterizedSpecification, Hash.from(project.getFullName()));
		} catch (IllegalArgumentException e) {
			// this shouldn't fail because we've already parsed stuff in the constructor,
			// so if it fails, use whatever 'tabs' that we already have.
			LOGGER.log(Level.FINE, "Failed to parse crontab spec: " + spec, e);
		}
	}

	/**
	 * for the config.jelly to populate
	 * 
	 * @return the raw specification
	 */
	public String getParameterizedSpecification() {
		return parameterizedSpecification;
	}
}
