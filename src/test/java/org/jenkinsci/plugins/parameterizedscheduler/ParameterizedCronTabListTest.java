package org.jenkinsci.plugins.parameterizedscheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
		ParameterizedCronTabList testObject = ParameterizedCronTabList.create("* * * * *%foo=bar\n */1 * * * *%bar=foo");
		assertTrue(testObject.checkSanity(), testObject.checkSanity().startsWith("Do you really mean \"every minute\""));
		List<ParameterizedCronTab> actualCronTabList = testObject.check(new GregorianCalendar());
		assertTrue(actualCronTabList.size() == 2);

		Map<String, String> expected = Maps.newHashMap();
		expected.put("foo", "bar");
		expected.put("bar", "foo");

		Map<String, String> actual = actualCronTabList.stream()
				.flatMap(tab -> tab.getParameterValues().entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		assertEquals(expected, actual);
	}

	@Test
	public void check_Delegates_ReturnsNull() {
		ParameterizedCronTabList testObject = new ParameterizedCronTabList(Arrays.asList(mockParameterizedCronTab,
				mockParameterizedCronTabToo));
		GregorianCalendar testCalendar = new GregorianCalendar();

		assertTrue(testObject.check(testCalendar).isEmpty());

		Mockito.verify(mockParameterizedCronTab).check(testCalendar);
		Mockito.verify(mockParameterizedCronTabToo).check(testCalendar);
	}

	@Test
	public void check_Delegates_ReturnsSame_WithoutEarlyExit() {
		ParameterizedCronTabList testObject = new ParameterizedCronTabList(Arrays.asList(mockParameterizedCronTab,
				mockParameterizedCronTabToo));
		GregorianCalendar testCalendar = new GregorianCalendar();

		Mockito.when(mockParameterizedCronTab.check(testCalendar)).thenReturn(true);
		assertEquals(Collections.singletonList(mockParameterizedCronTab), testObject.check(testCalendar));
	}

	@Test
	public void check_Delegates_ReturnsSame() {
		ParameterizedCronTabList testObject = new ParameterizedCronTabList(Arrays.asList(mockParameterizedCronTab,
				mockParameterizedCronTabToo));
		GregorianCalendar testCalendar = new GregorianCalendar();

		Mockito.when(mockParameterizedCronTabToo.check(testCalendar)).thenReturn(true);
		assertEquals(Collections.singletonList(mockParameterizedCronTabToo), testObject.check(testCalendar));
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

}
