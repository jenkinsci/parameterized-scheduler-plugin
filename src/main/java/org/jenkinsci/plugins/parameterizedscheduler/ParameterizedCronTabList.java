package org.jenkinsci.plugins.parameterizedscheduler;

import hudson.scheduler.CronTabList;
import hudson.scheduler.Hash;
import hudson.scheduler.Messages;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import antlr.ANTLRException;

/**
 * mostly a copy of {@link CronTabList}
 * 
 * @author jameswilson
 *
 */
public class ParameterizedCronTabList {

	private final List<ParameterizedCronTab> cronTabs;

	public ParameterizedCronTabList(List<ParameterizedCronTab> cronTabs) {
		this.cronTabs = cronTabs;
	}

	public static ParameterizedCronTabList create(String cronTabSpecification) throws ANTLRException {
		return create(cronTabSpecification, null);
	}

	public static ParameterizedCronTabList create(String cronTabSpecification, Hash hash) throws ANTLRException {
		List<ParameterizedCronTab> result = new ArrayList<>();
		int lineNumber = 0;
		for (String line : cronTabSpecification.split("\\r?\\n")) {
			lineNumber++;
			line = line.trim();
			if (line.length() == 0 || line.startsWith("#"))
				continue; // ignorable line
			try {
				result.add(ParameterizedCronTab.create(line, lineNumber, hash));
			} catch (ANTLRException e) {
				throw new ANTLRException(String.format("Invalid input: \"%s\": %s", line, e.toString()), e);
			}
		}
		return new ParameterizedCronTabList(result);
	}

	public ParameterizedCronTab check(Calendar calendar) {
		for (ParameterizedCronTab tab : cronTabs) {
			if (tab.check(calendar))
				return tab;
		}
		return null;
	}

	public String checkSanity() {
		for (ParameterizedCronTab tab : cronTabs) {
			String s = tab.checkSanity();
			if (s != null)
				return s;
		}
		return null;
	}
}
