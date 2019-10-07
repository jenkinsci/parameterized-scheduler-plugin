package org.jenkinsci.plugins.parameterizedscheduler;

import hudson.model.*;
import hudson.scheduler.Hash;
import hudson.triggers.Trigger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.kohsuke.stapler.DataBoundConstructor;

import antlr.ANTLRException;

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
	public ParameterizedTimerTrigger(String parameterizedSpecification) throws ANTLRException {
		this.parameterizedSpecification = parameterizedSpecification;
		this.cronTabList = ParameterizedCronTabList.create(parameterizedSpecification);
	}

	@Override
	public void run() {
		LOGGER.fine("tried to run from base Trigger, nothing will happen");
	}

	/**
	 * this method started out as hudson.model.AbstractProject.getDefaultParametersValues()
	 * @param parameterValues
	 * @return the ParameterValues as set from the crontab row or their defaults
	 */
	@SuppressWarnings("unchecked")
	private List<ParameterValue> configurePropertyValues(Map<String, String> parameterValues) {
		assert job != null : "job must not be null if this was 'started'";
		ParametersDefinitionProperty paramDefProp = (ParametersDefinitionProperty) job
				.getProperty(ParametersDefinitionProperty.class);
		ArrayList<ParameterValue> defValues = new ArrayList<ParameterValue>();

		/* Scan for all parameter with an associated default values */
		for (ParameterDefinition paramDefinition : paramDefProp.getParameterDefinitions()) {
			ParameterValue defaultValue = paramDefinition.getDefaultParameterValue();

			if (parameterValues.containsKey(paramDefinition.getName())) {
				ParameterizedStaplerRequest request = new ParameterizedStaplerRequest(
						parameterValues.get(paramDefinition.getName()));
				defValues.add(paramDefinition.createValue(request));
			} else if (defaultValue != null)
				defValues.add(defaultValue);
		}

		return defValues;
	}

	public void checkCronTabsAndRun(Calendar calendar) {
		LOGGER.fine("checking and maybe running at " + calendar);
		List<ParameterizedCronTab> checkedCronTabList = cronTabList.check(calendar);
		Jenkins jenkins = Jenkins.getInstance();

		checkedCronTabList.forEach(cronTab -> {
			Map<String, String> parameterValues = cronTab.getParameterValues();
			ParametersAction parametersAction = new ParametersAction(configurePropertyValues(parameterValues));
			assert job != null : "job must not be null, if this was 'started'";
			if (job instanceof AbstractProject) {
				((AbstractProject) job).scheduleBuild2(0, (Cause)null, causeAction(parameterValues), parametersAction);
			} else if (jenkins != null && jenkins.getPlugin("workflow-job") != null && job instanceof WorkflowJob) {
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
		} catch (ANTLRException e) {
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
