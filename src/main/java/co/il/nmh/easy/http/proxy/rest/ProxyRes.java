package co.il.nmh.easy.http.proxy.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import co.il.nmh.easy.http.proxy.core.ProxyRequestHandler;
import co.il.nmh.easy.utils.EasyInputStream;

/**
 * @author Maor Hamami
 *
 */
public class ProxyRes
{
	@Autowired
	protected ProxyRequestHandler proxyRequestHandler;

	@RequestMapping(value = "**")
	public ResponseEntity<Object> proxy(HttpServletRequest httpServletRequest) throws IOException
	{
		String method = httpServletRequest.getMethod();
		String requestURI = httpServletRequest.getRequestURI();

		String queryString = httpServletRequest.getQueryString();

		if (null != queryString)
		{
			requestURI += "?" + queryString;
		}

		Map<String, List<String>> headers = getHeaders(httpServletRequest);
		EasyInputStream payload = new EasyInputStream(httpServletRequest.getInputStream());

		try
		{
			return proxyRequestHandler.handle(httpServletRequest, method, requestURI, headers, payload);
		}
		finally
		{
			payload.close();
		}
	}

	private Map<String, List<String>> getHeaders(HttpServletRequest httpServletRequest)
	{
		Map<String, List<String>> headers = new HashMap<>();

		Enumeration<String> headerNames = httpServletRequest.getHeaderNames();

		while (headerNames.hasMoreElements())
		{
			String header = headerNames.nextElement();
			Enumeration<String> values = httpServletRequest.getHeaders(header);

			while (values.hasMoreElements())
			{
				String value = values.nextElement();

				if (!headers.containsKey(header))
				{
					headers.put(header, new ArrayList<>());
				}

				headers.get(header).add(value);
			}
		}
		return headers;
	}
}
