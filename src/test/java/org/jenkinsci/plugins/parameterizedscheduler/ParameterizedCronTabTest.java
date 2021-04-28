package org.jenkinsci.plugins.parameterizedscheduler;

import hudson.scheduler.CronTab;
import org.junit.Test;
import org.jvnet.localizer.LocaleProvider;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import hudson.scheduler.Hash;

import com.google.common.collect.Maps;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParameterizedCronTabTest {

	static
	{
		LocaleProvider.setProvider(new LocaleProvider()
		{
			@Override
			public Locale get()
			{
				return Locale.ENGLISH;
			}
		});
	}

	@Test
	public void ctor_happyPath() throws Exception {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("one", "onevalue");
		CronTab testCronTab = new CronTab("* * * * *");
		ParameterizedCronTab testObject = new ParameterizedCronTab(testCronTab, parameters);

		assertEquals(parameters, testObject.getParameterValues());
		assertTrue(testObject.check(new GregorianCalendar()));
		assertTrue(testObject.checkSanity().startsWith("Do you really mean"));
	}

	@Test
	public void param_value_with_percent_sign() throws Exception {
		String cron = "* * * * *";
		String params = "one=onevalue;two=10%";
		String line = cron +" %" + params;
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("one", "onevalue");
		parameters.put("two", "10%");

		ParameterizedCronTab parameterizedCronTab = ParameterizedCronTab.create(line, 1, Hash.from(line), null);
		assertEquals(parameters, parameterizedCronTab.getParameterValues());
	}

	@Test
	public void with_no_params_separator() throws Exception {
		String line = "* * * * *";
		Map<String, String> parameters = Maps.newHashMap();
		ParameterizedCronTab parameterizedCronTab = ParameterizedCronTab.create(line, 1, Hash.from(line), null);
		assertEquals(parameters, parameterizedCronTab.getParameterValues());
	}

}
