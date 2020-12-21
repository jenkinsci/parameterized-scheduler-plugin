package org.jenkinsci.plugins.parameterizedscheduler;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import antlr.ANTLRException;
import hudson.scheduler.CronTab;
import hudson.scheduler.CronTabList;
import hudson.scheduler.Hash;

/**
 * this is a copy of {@link CronTab} with added parameters map support
 * 
 * @author jameswilson
 */
public class ParameterizedCronTab {

	private final Map<String, String> parameterValues;
	private final CronTabList cronTabList;

	/**
	 * @param cronTab the crontab to use as a template
	 * @param parameters the parameters in name=value key pairings
	 */
	public ParameterizedCronTab(CronTab cronTab, Map<String, String> parameters) {
		cronTabList = new CronTabList(Collections.singleton(cronTab));
		parameterValues = parameters;
	}

	/**
	 * @param hash
	 *      Used to spread out token like "@daily". Null to preserve the legacy behaviour
	 *      of not spreading it out at all.
	 */
	public static ParameterizedCronTab create(String line, int lineNumber, Hash hash) throws ANTLRException {
		String[] lineParts = line.split("%");
		CronTab cronTab = new CronTab(lineParts[0].trim(), lineNumber, hash);
		Map<String, String> parameters = new HashMap<>();
		if (lineParts.length == 2) {
			parameters = new ParameterParser().parse(lineParts[1]);
		}
		return new ParameterizedCronTab(cronTab, parameters);
	}

	public Map<String, String> getParameterValues() {
		return parameterValues;
	}


	public boolean check(Calendar calendar) {
		return cronTabList.check(calendar);
	}

	public String checkSanity() {
		return cronTabList.checkSanity();
	}
}
