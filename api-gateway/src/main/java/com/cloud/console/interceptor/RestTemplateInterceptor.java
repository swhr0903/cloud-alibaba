package com.cloud.console.interceptor;

import com.cloud.console.ribbon.RibbonFilterContext;
import com.cloud.console.ribbon.RibbonFilterContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;

import static com.cloud.console.ribbon.DefaultRibbonFilterContext.VERSION;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        RibbonFilterContext currentContext = RibbonFilterContextHolder.getCurrentContext();
        String version = currentContext.getAttributes().get(VERSION);
        if (StringUtils.isNotBlank(version)) {
            requestWrapper.getHeaders().add(VERSION, version);
        }
        return execution.execute(requestWrapper, body);
    }
}
