package org.jenkinsci.plugins.parameterizedscheduler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import antlr.ANTLRException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

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
		List<ParameterizedCronTab> actualCronTabs = testObject.check(new GregorianCalendar());
		assertThat(actualCronTabs.size(), is(1));
		assertEquals(Collections.singletonMap("foo", "bar"), actualCronTabs.get(0).getParameterValues());
	}

	@Test
	public void createMultiple() throws Exception {
		ParameterizedCronTabList testObject = ParameterizedCronTabList.create("* * * * *%foo=bar\n*/1 * * * *%bar=bar");
		assertTrue(testObject.checkSanity(), testObject.checkSanity().startsWith("Do you really mean \"every minute\""));
		List<ParameterizedCronTab> actualCronTabs = testObject.check(new GregorianCalendar());
		assertThat(actualCronTabs.size(), is(2));
		assertEquals(Collections.singletonMap("foo", "bar"), actualCronTabs.get(0).getParameterValues());
		assertEquals(Collections.singletonMap("bar", "bar"), actualCronTabs.get(1).getParameterValues());
	}

	@Test
	public void check_Delegates_ReturnsNull() {
		ParameterizedCronTabList testObject = new ParameterizedCronTabList(Arrays.asList(mockParameterizedCronTab,
				mockParameterizedCronTabToo));
		GregorianCalendar testCalendar = new GregorianCalendar();
		List<ParameterizedCronTab> tabList = testObject.check(testCalendar);
		assertThat(tabList, is(empty()));

		Mockito.verify(mockParameterizedCronTab).check(testCalendar);
		Mockito.verify(mockParameterizedCronTabToo).check(testCalendar);
	}

	@Test
	public void check_Delegates_ReturnsSame() {
		ParameterizedCronTabList testObject = new ParameterizedCronTabList(Arrays.asList(mockParameterizedCronTab,
				mockParameterizedCronTabToo));
		GregorianCalendar testCalendar = new GregorianCalendar();

		Mockito.when(mockParameterizedCronTab.check(testCalendar)).thenReturn(true);
		Mockito.when(mockParameterizedCronTabToo.check(testCalendar)).thenReturn(false);
		List<ParameterizedCronTab> tabList = testObject.check(testCalendar);
		assertThat(tabList.size(), is(1));
		assertSame(mockParameterizedCronTab, tabList.get(0));
	}

	@Test
	public void check_Delegates_ReturnsBoth() {
		ParameterizedCronTabList testObject = new ParameterizedCronTabList(Arrays.asList(mockParameterizedCronTab,
				mockParameterizedCronTabToo));
		GregorianCalendar testCalendar = new GregorianCalendar();

		Mockito.when(mockParameterizedCronTab.check(testCalendar)).thenReturn(true);
		Mockito.when(mockParameterizedCronTabToo.check(testCalendar)).thenReturn(true);
		List<ParameterizedCronTab> tabList = testObject.check(testCalendar);
		assertThat(tabList.size(), is(2));

		assertSame(mockParameterizedCronTab, tabList.get(0));
		assertSame(mockParameterizedCronTabToo, tabList.get(1));
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

		Mockito.verifyNoInteractions(mockParameterizedCronTabToo);
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
		List<ParameterizedCronTab> actualCronTabs = testObject.check(new GregorianCalendar());
		assertThat(actualCronTabs.size(), is(1));

		Map<String, String> expected = Collections.singletonMap("foo", "bar");
		assertEquals(expected, actualCronTabs.get(0).getParameterValues());
	}

	@Test(expected = ANTLRException.class)
	public void create_with_invalidTimezone() throws ANTLRException {
		ParameterizedCronTabList.create("TZ=Dune/Arrakis \n * * * * *%foo=bar");
	}

}
