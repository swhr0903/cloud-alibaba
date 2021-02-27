package com.cloud.console.interceptor;

import com.cloud.console.ribbon.RibbonFilterContext;
import com.cloud.console.ribbon.RibbonFilterContextHolder;
import feign.RequestInterceptor;
import org.apache.commons.lang3.StringUtils;

import static com.cloud.console.ribbon.DefaultRibbonFilterContext.VERSION;

public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(feign.RequestTemplate template) {
        RibbonFilterContext currentContext = RibbonFilterContextHolder.getCurrentContext();

        String version = currentContext.getAttributes().get(VERSION);
        if (StringUtils.isNotBlank(version)) {
            template.header(VERSION, version);
        }
    }
}
