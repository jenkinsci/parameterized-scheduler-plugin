package org.jenkinsci.plugins.parameterizedscheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Map;

import org.jenkinsci.plugins.parameterizedscheduler.ParameterizedCronTab;
import org.jenkinsci.plugins.parameterizedscheduler.ParameterizedCronTabList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class ParameterizedCronTabListTest {
	@Mock
	private ParameterizedCronTab mockParameterizedCronTab;
	@Mock
	private ParameterizedCronTab mockParameterizedCronTabToo;

	@Test
	public void create() throws Exception {
		ParameterizedCronTabList testObject = ParameterizedCronTabList.create("* * * * *%foo=bar");
		assertTrue(testObject.checkSanity(), testObject.checkSanity().startsWith("Do you really mean \"every minute\""));
		ParameterizedCronTab actualCronTab = testObject.check(new GregorianCalendar());
		assertTrue(actualCronTab != null);

		Map<String, String> expected = Maps.newHashMap();
		expected.put("foo", "bar");
		assertEquals(expected, actualCronTab.getParameterValues());
	}

	@Test
	public void check_Delegates_ReturnsNull() {
		ParameterizedCronTabList testObject = new ParameterizedCronTabList(Arrays.asList(mockParameterizedCronTab,
				mockParameterizedCronTabToo));
		GregorianCalendar testCalendar = new GregorianCalendar();

		assertNull(testObject.check(testCalendar));

		Mockito.verify(mockParameterizedCronTab).check(testCalendar);
		Mockito.verify(mockParameterizedCronTabToo).check(testCalendar);
	}

	@Test
	public void check_Delegates_ReturnsSame_EarlyExit() {
		ParameterizedCronTabList testObject = new ParameterizedCronTabList(Arrays.asList(mockParameterizedCronTab,
				mockParameterizedCronTabToo));
		GregorianCalendar testCalendar = new GregorianCalendar();

		Mockito.when(mockParameterizedCronTab.check(testCalendar)).thenReturn(true);
		assertSame(mockParameterizedCronTab, testObject.check(testCalendar));

		Mockito.verifyZeroInteractions(mockParameterizedCronTabToo);
	}

	@Test
	public void check_Delegates_ReturnsSame() {
		ParameterizedCronTabList testObject = new ParameterizedCronTabList(Arrays.asList(mockParameterizedCronTab,
				mockParameterizedCronTabToo));
		GregorianCalendar testCalendar = new GregorianCalendar();

		Mockito.when(mockParameterizedCronTabToo.check(testCalendar)).thenReturn(true);
		assertSame(mockParameterizedCronTabToo, testObject.check(testCalendar));

	}

	@Test
	public void checkSanity_Delegates_ReturnsNull() {
		ParameterizedCronTabList testObject = new ParameterizedCronTabList(Arrays.asList(mockParameterizedCronTab,
				mockParameterizedCronTabToo));

		assertNull(testObject.checkSanity());

		Mockito.verify(mockParameterizedCronTab).checkSanity();
		Mockito.verify(mockParameterizedCronTabToo).checkSanity();
	}

	@Test
	public void checkSanity_Delegates_ReturnsSame_EarlyExit() {
		ParameterizedCronTabList testObject = new ParameterizedCronTabList(Arrays.asList(mockParameterizedCronTab,
				mockParameterizedCronTabToo));

		String sanityValue = "foo";
		Mockito.when(mockParameterizedCronTab.checkSanity()).thenReturn(sanityValue);
		assertSame(sanityValue, testObject.checkSanity());

		Mockito.verifyZeroInteractions(mockParameterizedCronTabToo);
	}

	@Test
	public void checkSanity_Delegates_ReturnsSame() {
		ParameterizedCronTabList testObject = new ParameterizedCronTabList(Arrays.asList(mockParameterizedCronTab,
				mockParameterizedCronTabToo));

		String sanityValue = "foo";
		Mockito.when(mockParameterizedCronTabToo.checkSanity()).thenReturn(sanityValue);
		assertSame(sanityValue, testObject.checkSanity());

	}

	@Test
	public void create_with_timezone() throws Exception {
		ParameterizedCronTabList testObject = ParameterizedCronTabList.create("TZ=Australia/Sydney \n * * * * *%foo=bar");
		assertTrue(testObject.checkSanity(), testObject.checkSanity().startsWith("Do you really mean \"every minute\""));
		ParameterizedCronTab actualCronTab = testObject.check(new GregorianCalendar());
		assertTrue(actualCronTab != null);

		Map<String, String> expected = Maps.newHashMap();
		expected.put("foo", "bar");
		assertEquals(expected, actualCronTab.getParameterValues());
	}

}
