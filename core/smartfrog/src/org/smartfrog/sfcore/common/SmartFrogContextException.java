/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.prim.Prim;

public class SmartFrogContextException extends SmartFrogRuntimeException {

  public SmartFrogContextException(String message) {
    super(message);
  }

  public SmartFrogContextException(Throwable cause) {
    super(cause);
  }

  public SmartFrogContextException(Throwable cause, Prim sfObject) {
    super(cause, sfObject);
  }

  public SmartFrogContextException(String message, Throwable cause) {
    super(message, cause);
  }

  public SmartFrogContextException(String message, Prim sfObject) {
    super(message, sfObject);
  }

  public SmartFrogContextException(String message, Throwable cause, Prim sfObject) {
    super(message, cause, sfObject);
  }
}