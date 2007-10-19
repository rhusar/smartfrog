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

package org.smartfrog.services.rest.exceptions;

/**
 * Thrown when the URI specificed in a request is invalid. The
 * creation of such an exception should yield an appropriate
 * HTTP 4xx response detailing the exceptional situation.
 *
 * @author Derek Mortimer
 * @version 1.0
 */
public class InvalidURIException extends RestException
{
	public InvalidURIException()
	{
		super();
	}

	public InvalidURIException(String string)
	{
		super(string);
	}

	public InvalidURIException(String string, Throwable throwable)
	{
		super(string, throwable);
	}

	public InvalidURIException(Throwable throwable)
	{
		super(throwable);
	}
}