package ua.ieeta.test;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

public class LogFilter extends AbstractMatcherFilter<ILoggingEvent> {
	private String packageName;

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if(event.getLoggerName().startsWith(packageName))
			return FilterReply.ACCEPT;
		else
			return FilterReply.DENY;
	}
	
	public void setPackageName(String packageName) {this.packageName = packageName;}
	public String getPackageName() {return packageName;}
}
