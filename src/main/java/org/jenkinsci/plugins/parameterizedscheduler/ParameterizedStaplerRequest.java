package org.jenkinsci.plugins.parameterizedscheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.BindInterceptor;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.WebApp;
import org.kohsuke.stapler.bind.BoundObjectTable;
import org.kohsuke.stapler.lang.Klass;

public class ParameterizedStaplerRequest implements StaplerRequest {

	private final String value;

	public ParameterizedStaplerRequest(String value) {
		this.value = value;
	}

	@Override
	public String getAuthType() {
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		return null;
	}

	@Override
	public long getDateHeader(String name) {
		return 0;
	}

	@Override
	public String getHeader(String name) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getHeaders(String name) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getHeaderNames() {
		return null;
	}

	@Override
	public int getIntHeader(String name) {
		return 0;
	}

	@Override
	public String getMethod() {
		return null;
	}

	@Override
	public String getPathInfo() {
		return null;
	}

	@Override
	public String getPathTranslated() {
		return null;
	}

	@Override
	public String getContextPath() {
		return null;
	}

	@Override
	public String getQueryString() {
		return null;
	}

	@Override
	public String getRemoteUser() {
		return null;
	}

	@Override
	public boolean isUserInRole(String role) {
		return false;
	}

	@Override
	public Principal getUserPrincipal() {
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		return null;
	}

	@Override
	public String getRequestURI() {
		return null;
	}

	@Override
	public StringBuffer getRequestURL() {
		return null;
	}

	@Override
	public String getServletPath() {
		return null;
	}

	@Override
	public HttpSession getSession(boolean create) {
		return null;
	}

	@Override
	public HttpSession getSession() {
		return null;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	@Override
	public Object getAttribute(String name) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getAttributeNames() {
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

	}

	@Override
	public int getContentLength() {
		return 0;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public String getParameter(String name) {
		return value;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getParameterNames() {
		return null;
	}

	@Override
	public String[] getParameterValues(String name) {
		return new String[] { value };
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getParameterMap() {
		return null;
	}

	@Override
	public String getProtocol() {
		return null;
	}

	@Override
	public String getScheme() {
		return null;
	}

	@Override
	public String getServerName() {
		return null;
	}

	@Override
	public int getServerPort() {
		return 0;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return null;
	}

	@Override
	public String getRemoteAddr() {
		return null;
	}

	@Override
	public String getRemoteHost() {
		return null;
	}

	@Override
	public void setAttribute(String name, Object o) {

	}

	@Override
	public void removeAttribute(String name) {

	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getLocales() {
		return null;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return null;
	}

	@Override
	public String getRealPath(String path) {
		return null;
	}

	@Override
	public int getRemotePort() {
		return 0;
	}

	@Override
	public String getLocalName() {
		return null;
	}

	@Override
	public String getLocalAddr() {
		return null;
	}

	@Override
	public int getLocalPort() {
		return 0;
	}

	@Override
	public Stapler getStapler() {
		return null;
	}

	@Override
	public WebApp getWebApp() {
		return null;
	}

	@Override
	public String getRestOfPath() {
		return null;
	}

	@Override
	public String getOriginalRestOfPath() {
		return null;
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public String getRequestURIWithQueryString() {
		return null;
	}

	@Override
	public StringBuffer getRequestURLWithQueryString() {
		return null;
	}

	@Override
	public RequestDispatcher getView(Object it, String viewName) throws IOException {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public RequestDispatcher getView(Class clazz, String viewName) throws IOException {
		return null;
	}

	@Override
	public RequestDispatcher getView(Klass<?> clazz, String viewName) throws IOException {
		return null;
	}

	@Override
	public String getRootPath() {
		return null;
	}

	@Override
	public String getReferer() {
		return null;
	}

	@Override
	public List<Ancestor> getAncestors() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Ancestor findAncestor(Class type) {
		return null;
	}

	@Override
	public <T> T findAncestorObject(Class<T> type) {
		return null;
	}

	@Override
	public Ancestor findAncestor(Object o) {
		return null;
	}

	@Override
	public boolean hasParameter(String name) {
		return false;
	}

	@Override
	public String getOriginalRequestURI() {
		return null;
	}

	@Override
	public boolean checkIfModified(long timestampOfResource, StaplerResponse rsp) {
		return false;
	}

	@Override
	public boolean checkIfModified(Date timestampOfResource, StaplerResponse rsp) {
		return false;
	}

	@Override
	public boolean checkIfModified(Calendar timestampOfResource, StaplerResponse rsp) {
		return false;
	}

	@Override
	public boolean checkIfModified(long timestampOfResource, StaplerResponse rsp, long expiration) {
		return false;
	}

	@Override
	public void bindParameters(Object bean) {

	}

	@Override
	public void bindParameters(Object bean, String prefix) {

	}

	@Override
	public <T> List<T> bindParametersToList(Class<T> type, String prefix) {
		return null;
	}

	@Override
	public <T> T bindParameters(Class<T> type, String prefix) {
		return null;
	}

	@Override
	public <T> T bindParameters(Class<T> type, String prefix, int index) {
		return null;
	}

	@Override
	public <T> T bindJSON(Class<T> type, JSONObject src) {
		return null;
	}

	@Override
	public <T> T bindJSON(Type genericType, Class<T> erasure, Object json) {
		return null;
	}

	@Override
	public void bindJSON(Object bean, JSONObject src) {

	}

	@Override
	public <T> List<T> bindJSONToList(Class<T> type, Object src) {
		return null;
	}

	@Override
	public BindInterceptor getBindInterceptor() {
		return null;
	}

	@Override
	public BindInterceptor setBindListener(BindInterceptor bindListener) {
		return null;
	}

	@Override
	public JSONObject getSubmittedForm() throws ServletException {
		return null;
	}

	@Override
	public FileItem getFileItem(String name) throws ServletException, IOException {
		return null;
	}

	@Override
	public boolean isJavaScriptProxyCall() {
		return false;
	}

	@Override
	public BoundObjectTable getBoundObjectTable() {
		return null;
	}

	@Override
	public String createJavaScriptProxy(Object toBeExported) {
		return null;
	}

	@Override
	public BindInterceptor setBindInterceptpr(BindInterceptor bindListener) {
		return null;
	}

	@Override
	public BindInterceptor setBindInterceptor(BindInterceptor bindInterceptor) {
		return null;
	}
}
