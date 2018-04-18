package com.example.springboot2issue.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="test.web.mvc.content.negotiation")
public class ContentNegotiationProperties {

    private boolean favorPathExtension = true;

    private boolean favorParameter = true;

    private boolean ignoreAcceptHeader = true;

    private boolean ignoreUnknownPathExtensions; //default = false

    private boolean useJaf;//default = false

    private boolean useSuffixPatternMatch = true;

    public boolean isFavorPathExtension() {
        return favorPathExtension;
    }

    public void setFavorPathExtension(boolean favorPathExtension) {
        this.favorPathExtension = favorPathExtension;
    }

    public boolean isFavorParameter() {
        return favorParameter;
    }

    public void setFavorParameter(boolean favorParameter) {
        this.favorParameter = favorParameter;
    }

    public boolean isIgnoreAcceptHeader() {
        return ignoreAcceptHeader;
    }

    public void setIgnoreAcceptHeader(boolean ignoreAcceptHeader) {
        this.ignoreAcceptHeader = ignoreAcceptHeader;
    }

    public boolean isIgnoreUnknownPathExtensions() {
        return ignoreUnknownPathExtensions;
    }

    public void setIgnoreUnknownPathExtensions(boolean ignoreUnknownPathExtensions) {
        this.ignoreUnknownPathExtensions = ignoreUnknownPathExtensions;
    }

    public boolean isUseJaf() {
        return useJaf;
    }

    public void setUseJaf(boolean useJaf) {
        this.useJaf = useJaf;
    }

	public boolean isUseSuffixPatternMatch() {
		return useSuffixPatternMatch;
	}

	public void setUseSuffixPatternMatch(boolean useSuffixPatternMatch) {
		this.useSuffixPatternMatch = useSuffixPatternMatch;
	}

}