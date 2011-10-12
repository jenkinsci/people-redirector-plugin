/*
 * The MIT License
 * 
 * Copyright (c) 2011, Cisco Systems, Inc., Max Spring
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.cisco.step.jenkins.plugins.people.redirector;

import org.jvnet.hudson.test.HudsonTestCase;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class BasicTest extends HudsonTestCase {
	private final static String REDIRECTION_PREFIX = "some/where/else";
	
	public void testNoRedirection() throws Exception{
		WebClient wc = new WebClient();
		
		assertNull("not configured: resulting path should be null",resultingPath(wc,"user/xyz"));
		
		setRedirection(wc,/*enable=*/true);
		String rp = resultingPath(wc,"user/xyz");
		assertNotNull("should contain redirection prefix, but is null",rp);
		assertTrue("should contain redirection prefix",(rp.indexOf(REDIRECTION_PREFIX) > -1));
		
		setRedirection(wc,/*enable=*/false);
		assertNull("disabled: resulting path should be null",resultingPath(wc,"user/xyz"));
	}
	
	private void setRedirection(WebClient wc, boolean enable) throws Exception{
		HtmlPage configPage = wc.goTo("configure");
		HtmlForm form = configPage.getFormByName("config");
		form.getInputByName("redirectTarget").setValueAttribute(wc.getContextPath()+REDIRECTION_PREFIX+"/${user}");
		form.<HtmlInput>getInputByName("redirectDisabled").setChecked(!enable);
		form.submit((HtmlButton)last(form.getHtmlElementsByTagName("button")));
	}
	
	private static String resultingPath(WebClient wc, String dir) throws Exception{
		try {
			wc.goTo(dir);
		} catch (FailingHttpStatusCodeException e) {
			return e.getMessage();
		}
		return null;
	}
}
