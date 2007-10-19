/**
	(C) Copyright 2006 Hewlett-Packard Development Company, LP

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

	For more information: www.smartfrog.org
 */

package org.smartfrog.services.rest.filters;

import javax.servlet.*;
import java.io.IOException;

/**
 * Intended as a superclass to all filters created within the
 * system providing various utility functions.
 *
 * @author Derek Mortimer
 * @version 1.0
 */
public class GeneralFilter implements Filter
{
	public void init(FilterConfig filterConfig)
	{
		this.filterConfig = filterConfig;
	}

	public void destroy()
	{
		this.filterConfig = null;
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
		throws ServletException, IOException
	{
		chain.doFilter(servletRequest, servletResponse);
	}


	public FilterConfig getFilterConfig()
	{
		return filterConfig;
	}

	private FilterConfig filterConfig;
}
