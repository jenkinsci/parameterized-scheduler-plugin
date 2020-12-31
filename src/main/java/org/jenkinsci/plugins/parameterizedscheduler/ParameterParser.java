package org.jenkinsci.plugins.parameterizedscheduler;

import com.google.common.base.Splitter;
import hudson.model.ParametersDefinitionProperty;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParameterParser {
	/**
	 * if ever changed, documentation and messages will need to be updated as well
	 */
	private static final String PARAMETER_SEPARATOR = "%";
	private static final String NAME_VALUE_SEPARATOR = "=";
	private static final String PAIR_SEPARATOR = ";";

	/**
	 * Parses a string with key value pairs
	 * @param nameValuePairFormattedString of name=value;other=value name value pairs
	 * @return Map of key-value pairs parsed from provided string
	 */
	public Map<String, String> parse(String nameValuePairFormattedString) {
		if (StringUtils.isBlank(nameValuePairFormattedString)) {
			return Collections.emptyMap();
		}
		String clean = nameValuePairFormattedString.trim();
		if (nameValuePairFormattedString.endsWith(PAIR_SEPARATOR)) {
			//the default splitter message in this scenario is not user friendly, so snip a trailing semicolon
			clean = clean.substring(0, clean.length() - 1);
		}
		return Splitter.on(PAIR_SEPARATOR).trimResults().withKeyValueSeparator(Splitter.on(NAME_VALUE_SEPARATOR).limit(2)).split(clean);
	}

	public String checkSanity(String cronTabSpec, ParametersDefinitionProperty parametersDefinitionProperty) {
		String[] cronTabLines = cronTabSpec.split("\\r?\\n");
		for (String cronTabLine : cronTabLines) {
			String[] split = cronTabLine.split(PARAMETER_SEPARATOR);
			if (split.length > 2) {
				return Messages.ParameterizedTimerTrigger_MoreThanOnePercent();
			}
			if (split.length == 2) {
				try {
					Map<String, String> parsedParameters = parse(split[1]);
					List<String> parameterDefinitionNames = parametersDefinitionProperty != null
							? parametersDefinitionProperty.getParameterDefinitionNames() : Collections.emptyList();
					List<String> parsedKeySet = parsedParameters.keySet().stream().filter(s -> !parameterDefinitionNames.contains(s)).collect(Collectors.toList());
					if (!parsedKeySet.isEmpty()) {
						return Messages.ParameterizedTimerTrigger_UndefinedParameter(parsedKeySet, parameterDefinitionNames);
					}
					List<String> emptyParameters = parsedParameters.keySet().stream().filter(k -> parsedParameters.get(k).isEmpty()).collect(Collectors.toList());
					if (!emptyParameters.isEmpty()) {
						return Messages.ParameterizedTimerTrigger_EmptyParameter(emptyParameters);
					}
				} catch (IllegalArgumentException e) {
					return e.getMessage();
				}
			}
		}
		return null;
	}
}
