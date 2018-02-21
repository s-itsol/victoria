/**
 * 
 */
package net.sitsol.victoria.setvlet.spring;

import org.springframework.web.bind.support.DefaultSessionAttributeStore;
import org.springframework.web.context.request.WebRequest;

/**
 * 
 * 
 * @author shibano
 */
public class VctSessionAttributeStore extends DefaultSessionAttributeStore {

	@Override
	public void storeAttribute(WebRequest request, String attributeName, Object attributeValue) {
		super.storeAttribute(request, attributeName, attributeValue);
	}

	@Override
	public Object retrieveAttribute(WebRequest request, String attributeName) {
		return super.retrieveAttribute(request, attributeName);
	}

	@Override
	public void cleanupAttribute(WebRequest request, String attributeName) {
		super.cleanupAttribute(request, attributeName);
	}


}
