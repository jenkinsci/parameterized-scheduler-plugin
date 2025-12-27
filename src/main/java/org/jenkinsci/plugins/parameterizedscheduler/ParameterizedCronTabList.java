package org.jenkinsci.plugins.parameterizedscheduler;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import hudson.scheduler.CronTabList;
import hudson.scheduler.Hash;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

	public List<ParameterizedCronTab> getCronTabs() {
		return Collections.unmodifiableList(cronTabs);
	}

	public static ParameterizedCronTabList create(String cronTabSpecification) {
		return create(cronTabSpecification, null);
	}

	public static ParameterizedCronTabList create(String cronTabSpecification, Hash hash) {
		List<ParameterizedCronTab> result = new ArrayList<>();
		int lineNumber = 0;
		String timezone = null;
		for (String line : cronTabSpecification.split("\\r?\\n")) {
			line = line.trim();
			if(line.length() > 0 && !line.startsWith("#")) {
				lineNumber++;
				if(lineNumber == 1 && line.startsWith("TZ=")) {
					timezone = CronTabList.getValidTimezone(line.replace("TZ=", ""));
					if (timezone == null) {
						throw new IllegalArgumentException("Invalid or unsupported timezone '" + line + "'");
					}
				} else {
					try {
						result.add(ParameterizedCronTab.create(line, lineNumber, hash, timezone));
					} catch (IllegalArgumentException e) {
						throw new IllegalArgumentException(String.format("Invalid input: \"%s\": %s", line, e), e);
					}
				}
			}
		}
		return new ParameterizedCronTabList(result);
	}

	public List<ParameterizedCronTab> check(Calendar calendar) {
		return cronTabs.stream().filter(tab -> tab.check(calendar)).collect(Collectors.toList());
	}

	public String checkSanity() {
		for (ParameterizedCronTab tab : cronTabs) {
			String s = tab.checkSanity();
			if (s != null)
				return s;
		}
		return null;
	}

	public @CheckForNull Calendar previous() {
		Calendar nearest = null;
		for (ParameterizedCronTab tab : cronTabs) {
			Calendar next = tab.next();
			if (next == null || next.before(nearest)) {
				nearest = next;
			}
		}
		return nearest;
	}

	public @CheckForNull Calendar next() {
		Calendar nearest = null;
		for (ParameterizedCronTab tab : cronTabs) {
			Calendar previous = tab.previous();
			if (nearest == null || nearest.after(previous)) {
				nearest = previous;
			}
		}
		return nearest;
	}

	public @CheckForNull ParameterizedCronTab nextParameterizedCronTab() {
		Calendar nearest = null;
		ParameterizedCronTab next = null;
		for (ParameterizedCronTab tab : cronTabs) {
			Calendar previous = tab.previous();
			if (nearest == null || nearest.after(previous)) {
				nearest = previous;
				next = tab;
			}
		}
		return next;
	}

	@CheckForNull
	public Calendar ceil(long timestamp) {
		Calendar ceil = null;
		for (ParameterizedCronTab wrapper: cronTabs) {
			Calendar scheduled = wrapper.ceil(timestamp);
			if (ceil == null || (scheduled != null && ceil.after(scheduled))) {
				ceil = scheduled;
			}
		}
		return ceil;
	}

	@CheckForNull
	public Calendar floor(long timestamp) {
		Calendar floor = null;
		for (ParameterizedCronTab wrapper: cronTabs) {
			Calendar scheduled = wrapper.floor(timestamp);
			if (floor == null || (scheduled != null && floor.before(scheduled))) {
				floor = scheduled;
			}
		}
		return floor;
	}
}
