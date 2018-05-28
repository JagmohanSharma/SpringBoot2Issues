package com.example.springboot2issue.config.serviceaccess;

import io.micrometer.core.annotation.Timed;
import org.apache.http.conn.DnsResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * DnsResolver that times DNS Resolution
 */
public class TimingDnsResolver implements DnsResolver {
    private DnsResolver delegate;

    public TimingDnsResolver(DnsResolver resolver) {
        this.delegate = resolver;
    }

    @Override
    @Timed(value="jvm.dns.httpclient.lookup")
    public InetAddress[] resolve(String host) throws UnknownHostException {
        return delegate.resolve(host);
    }
}
