package org.jenkinsci.plugins.parameterizedscheduler;

import hudson.model.Cause;

import java.util.Map;

public class ParameterizedTimerTriggerCause extends Cause {

	private final String description;

	public ParameterizedTimerTriggerCause(Map<String, String> parameterValues) {
		this.description = Messages.ParameterizedTimerTrigger_TimerTriggerCause_ShortDescription(parameterValues
				.toString());
	}

	@Override
	public String getShortDescription() {
		return description;
	}

}
