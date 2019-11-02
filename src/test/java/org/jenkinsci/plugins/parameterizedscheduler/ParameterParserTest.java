package org.jenkinsci.plugins.parameterizedscheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import hudson.model.ParametersDefinitionProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jenkinsci.plugins.parameterizedscheduler.Messages;
import org.jenkinsci.plugins.parameterizedscheduler.ParameterParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ParameterParserTest {

	@Mock
	private ParametersDefinitionProperty mockParametersDefinitionProperty;

	@Test
	public void test_nullReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		assertEquals(new HashMap<String, String>(), testObject.parse(null));
	}

	@Test
	public void test_EmptyStringReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		assertEquals(new HashMap<String, String>(), testObject.parse(""));
		assertEquals(new HashMap<String, String>(), testObject.parse("     "));
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_Malformed_NoEquals_StringReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		assertEquals(new HashMap<String, String>(), testObject.parse("namevalue"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_Malformed_ExtraSemicolon_StringReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		HashMap<String, String> expected = new HashMap<String, String>();
		expected.put("name", "value");
		assertEquals(expected, testObject.parse("name=value;;"));
	}

	@Test
	public void test_OneParamStringReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		HashMap<String, String> expected = new HashMap<String, String>();
		expected.put("name", "value");
		assertEquals(expected, testObject.parse("name=value"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_TrimsSpacesStringReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		HashMap<String, String> expected = new HashMap<String, String>();
		expected.put("name", "value");
		testObject.parse(" name = value; ;;");
	}

	@Test
	public void test_TwoParamsStringReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		HashMap<String, String> expected = new HashMap<String, String>();
		expected.put("name", "value");
		expected.put("name2", "value2");
		assertEquals(expected, testObject.parse("name2=value2;name=value"));
	}

	@Test
	public void test_TwoParamsStringWithSpaceReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		HashMap<String, String> expected = new HashMap<String, String>();
		expected.put("name", "value");
		expected.put("name2", "value2");
		assertEquals(expected, testObject.parse("name2=value2; name=value"));
	}

	@Test
	public void checkSanity_HappyPath() throws Exception {
		ParameterParser testObject = new ParameterParser();

		Mockito.when(mockParametersDefinitionProperty.getParameterDefinitionNames()).thenReturn(Arrays.asList("name"));
		assertNull(testObject.checkSanity("* * * * *%name=value", mockParametersDefinitionProperty));
	}

	@Test
	public void checkSanity_NotDefined_ProjectParameter() throws Exception {
		ParameterParser testObject = new ParameterParser();

		List<String> list = Arrays.asList("not name");
		Mockito.when(mockParametersDefinitionProperty.getParameterDefinitionNames()).thenReturn(list);
		assertEquals(Messages.ParameterizedTimerTrigger_UndefinedParameter("[name]", list.toString()),
				testObject.checkSanity("* * * * *%name=value", mockParametersDefinitionProperty));
	}

	@Test
	public void checkSanity_TrailingSemiColon_IsTrimmed() throws Exception {
		ParameterParser testObject = new ParameterParser();

		Mockito.when(mockParametersDefinitionProperty.getParameterDefinitionNames()).thenReturn(
				Arrays.asList("env", "freckled"));
		assertNull(testObject.checkSanity("* * * * *%env=eight;freckled=flase;", mockParametersDefinitionProperty));
	}

	@Test
	public void checkSanity_MoreThanOnePercent() throws Exception {
		ParameterParser testObject = new ParameterParser();

		assertEquals(Messages.ParameterizedTimerTrigger_MoreThanOnePercent(),
				testObject.checkSanity("* * * * *%name=value;%fred=barney", mockParametersDefinitionProperty));
	}

	@Test
	public void checkSanity_NoParametersIsNoBigDeal() throws Exception {
		ParameterParser testObject = new ParameterParser();

		assertNull(testObject.checkSanity("* * * * *%", mockParametersDefinitionProperty));
		assertNull(testObject.checkSanity("* * * * *", mockParametersDefinitionProperty));
	}

	@Test
	public void checkSanity_duplicateParamName() throws Exception {
		ParameterParser testObject = new ParameterParser();

		testObject.checkSanity("* * * * *%name=value;name=value2", mockParametersDefinitionProperty);
	}

	@Test
	public void checkSanity_UnmatchedEquals() throws Exception {
		ParameterParser testObject = new ParameterParser();

		testObject.checkSanity("* * * * *%name=value;name2=", mockParametersDefinitionProperty);
	}

	@Test
	public void test_paramValue_with_percent() {
		ParameterParser testObject = new ParameterParser();

		HashMap<String, String> expected = new HashMap<String, String>();
		expected.put("name", "value");
		expected.put("percent", "10%");
		assertEquals(expected, testObject.parse("name=value;percent=10%"));
	}

}
