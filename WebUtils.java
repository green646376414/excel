package com.walmart.utils;


import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class WebUtils {
	private static Logger log = Logger.getLogger(WebUtils.class);
	
	public static final String DEFAULT_CHARSET = "UTF-8";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final int DEFAULT_CONNECT_TIMEOUT = 5000;
	public static final int DEFAULT_READ_TIMEOUT = 5000;

	private static class DefaultTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
	}

	private WebUtils() {
	}

	public static String doPost(String url, Map<String, String> params,
			int connectTimeout, int readTimeout) throws IOException {
		return doPost(url, params, DEFAULT_CHARSET, connectTimeout, readTimeout);
	}

	public static String doPost(String url, Map<String, String> params,
			String charset, int connectTimeout, int readTimeout)
			throws IOException {
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		String query = buildQuery(params, charset);
		byte[] content = {};
		if (query != null) {
			content = query.getBytes(charset);
		}
		return doPost(url, ctype, content, connectTimeout, readTimeout);
	}

	public static String doPost(String url, String ctype,
			StringBuilder content, int connectTimeout, int readTimeout)
			throws IOException {
		return doPost(url, ctype, content.toString().getBytes(),
				connectTimeout, readTimeout);
	}

	public static String doPost(String url, String ctype, String content,
			int connectTimeout, int readTimeout) throws IOException {
		return doPost(url, ctype, content.getBytes(), connectTimeout,
				readTimeout);
	}

	public static String doPost(String url, String ctype, String content)
			throws IOException {
		return doPost(url, ctype, content.getBytes(), DEFAULT_CONNECT_TIMEOUT,
				DEFAULT_READ_TIMEOUT);
	}

	public static String doPost(String url, String ctype, byte[] content,
			int connectTimeout, int readTimeout) throws IOException {
		log.debug("Request url: " + url);
		log.debug("Content: " + content);

		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				conn = getConnection(new URL(url), METHOD_POST, ctype);
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (IOException e) {
				Map<String, String> map = getParamsFromUrl(url);
				log.debug(content);
				log.debug(map);
				e.printStackTrace();
				throw e;
			}

			try {
				out = conn.getOutputStream();
				out.write(content);
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				Map<String, String> map = getParamsFromUrl(url);
				log.debug(content);
				log.debug(map);
				e.printStackTrace();
				throw e;
			}
		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	public static String doGet(String url, Map<String, String> params)
			throws IOException {
		return doGet(url, params, DEFAULT_CHARSET);
	}

	public static String doGet(String url, Map<String, String> params,
			String charset) throws IOException {
		HttpURLConnection conn = null;
		String rsp = null;

		try {
			String ctype = "application/x-www-form-urlencoded;charset="
					+ charset;
			String query = buildQuery(params, charset);
			try {
				conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype);
			} catch (IOException e) {
				Map<String, String> map = getParamsFromUrl(url);
				log.debug(params);
				log.debug(map);
				e.printStackTrace();
				throw e;
			}

			try {
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				Map<String, String> map = getParamsFromUrl(url);
				log.debug(params);
				log.debug(map);
				e.printStackTrace();
				throw e;
			}

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static HttpURLConnection getConnection(URL url, String method,
			String ctype) throws IOException {
		HttpURLConnection conn = null;
		if ("https".equals(url.getProtocol())) {
			SSLContext ctx = null;
			try {
				ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0],
						new TrustManager[] { new DefaultTrustManager() },
						new SecureRandom());
			} catch (Exception e) {
				throw new IOException(e);
			}
			HttpsURLConnection connHttps = (HttpsURLConnection) url
					.openConnection();
			connHttps.setSSLSocketFactory(ctx.getSocketFactory());
			connHttps.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return false;
				}
			});
			conn = connHttps;
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}

		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
		conn.setRequestProperty("User-Agent", "aop-sdk-java");
		conn.setRequestProperty("Content-Type", ctype);
		return conn;
	}

	private static URL buildGetUrl(String strUrl, String query)
			throws IOException {
		URL url = new URL(strUrl);
		if (StringUtils.isEmpty(query)) {
			return url;
		}

		if (StringUtils.isEmpty(url.getQuery())) {
			if (strUrl.endsWith("?")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "?" + query;
			}
		} else {
			if (strUrl.endsWith("&")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "&" + query;
			}
		}

		log.debug("Request Url: " + strUrl);
		return new URL(strUrl);
	}

	public static String buildQuery(Map<String, String> params, String charset)
			throws IOException {
		if (params == null || params.isEmpty()) {
			return null;
		}

		StringBuilder query = new StringBuilder();
		Set<Map.Entry<String, String>> entries = params.entrySet();
		boolean hasParam = false;

		for (Map.Entry<String, String> entry : entries) {
			String name = entry.getKey();
			String value = entry.getValue();
			if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
				if (hasParam) {
					query.append("&");
				} else {
					hasParam = true;
				}

				query.append(name).append("=")
						.append(URLEncoder.encode(value, charset));
			}
		}

		return query.toString();
	}

	protected static String getResponseAsString(HttpURLConnection conn)
			throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		InputStream es = conn.getErrorStream();
		if (es == null) {
			return getStreamAsString(conn.getInputStream(), charset);
		} else {
			String msg = getStreamAsString(es, charset);
			if (StringUtils.isEmpty(msg)) {
				throw new IOException(conn.getResponseCode() + ":"
						+ conn.getResponseMessage());
			} else {
				throw new IOException(msg);
			}
		}
	}

	private static String getStreamAsString(InputStream stream, String charset)
			throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream, charset));
			StringWriter writer = new StringWriter();

			char[] chars = new char[256];
			int count = 0;
			while ((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}

			return writer.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	private static String getResponseCharset(String ctype) {
		String charset = DEFAULT_CHARSET;

		if (!StringUtils.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (!StringUtils.isEmpty(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}

		return charset;
	}

	/**
	 * ???????UTF-8?????????????????
	 *
	 * @param value
	 *            ?????
	 * @return ????????????
	 */
	public static String decode(String value) {
		return decode(value, DEFAULT_CHARSET);
	}

	/**
	 * ???????UTF-8????????????????
	 *
	 * @param value
	 *            ?????
	 * @return ??????????
	 */
	public static String encode(String value) {
		return encode(value, DEFAULT_CHARSET);
	}

	/**
	 * ?????????????????????????
	 *
	 * @param value
	 *            ?????
	 * @param charset
	 *            ???
	 * @return ????????????
	 */
	public static String decode(String value, String charset) {
		String result = null;
		if (!StringUtils.isEmpty(value)) {
			try {
				result = URLDecoder.decode(value, charset);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	/**
	 * ????????????????????????
	 *
	 * @param value
	 *            ?????
	 * @param charset
	 *            ???
	 * @return ??????????
	 */
	public static String encode(String value, String charset) {
		String result = null;
		if (!StringUtils.isEmpty(value)) {
			try {
				result = URLEncoder.encode(value, charset);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	private static Map<String, String> getParamsFromUrl(String url) {
		Map<String, String> map = null;
		if (url != null && url.indexOf('?') != -1) {
			map = splitUrlQuery(url.substring(url.indexOf('?') + 1));
		}
		if (map == null) {
			map = new HashMap<String, String>();
		}
		return map;
	}

	/**
	 * ??URL????????��????
	 *
	 * @param query
	 *            URL???
	 * @return ???????
	 */
	public static Map<String, String> splitUrlQuery(String query) {
		Map<String, String> result = new HashMap<String, String>();

		String[] pairs = query.split("&");
		if (pairs != null && pairs.length > 0) {
			for (String pair : pairs) {
				String[] param = pair.split("=", 2);
				if (param != null && param.length == 2) {
					result.put(param[0], param[1]);
				}
			}
		}

		return result;
	}

}
