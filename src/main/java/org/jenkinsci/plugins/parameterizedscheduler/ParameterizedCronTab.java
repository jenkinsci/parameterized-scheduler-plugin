package org.jenkinsci.plugins.parameterizedscheduler;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;

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
	public static ParameterizedCronTab create(String line, int lineNumber, Hash hash, String timezone) throws ANTLRException {
		Map<String, String> parameters = Maps.newHashMap();
		int firstPercentIdx = line.indexOf("%");
		if(firstPercentIdx != -1) {
			String cronLinePart = line.substring(0, firstPercentIdx).trim();
			String paramsLinePart = line.substring(firstPercentIdx + 1).trim();
			CronTab cronTab = new CronTab(cronLinePart, lineNumber, hash, timezone);
			parameters = new ParameterParser().parse(paramsLinePart);
			return new ParameterizedCronTab(cronTab, parameters);
		} else {
			CronTab cronTab = new CronTab(line, lineNumber, hash, timezone);
			return new ParameterizedCronTab(cronTab, parameters);
		}

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
