/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.persistence.database;

/**
 * The Statements class holds all the SQL statements needed for interacting with
 * the database. On construction, the Statements object will create SQL data definition
 * expressions using the vendor specific dialect.
 */
public class Statements {

	
	/**
	 * storage operations
	 */
    public String createRegistrationTableSQL = null;
    public String addRegistrationSQL         = "insert into registrations values (?, ?, ?)";
    public String removeRegistrationSQL      = "delete from registrations where name = ?";
    public String getRegistrationSQL         = "select storage from registrations where name = ?";
    public String reparentRegistrationSQL    = "update registrations set parent = ? where name = ?";
    public String getRootRegistrationsSQL    = "select storage from registrations where registrations.parent is null";
    public String getOrphanRegistrationsSQL  = "select storage from registrations where registrations.parent is not null and not registrations.parent in (select name from registrations)";
    public String createAttributesTableSQL   = null;
    public String addAttributeSQL            = "insert into attributes values (?, ?, ?, ?)";
    public String replaceAttributeSQL        = "update attributes set value = ? where cname = ? AND aname = ?";
    public String replaceTagsSQL             = "update attributes set tags = ? where cname = ? AND aname = ?";
    public String removeAttributeSQL         = "delete from attributes where cname = ? AND aname = ?";
    public String removeAllAttributesSQL     = "delete from attributes where cname = ?";
    public String getAllAttributesSQL        = "select aname, tags, value from attributes where cname = ?";
        
    public Statements(SQLDialect dialect) {
    	
    	createRegistrationTableSQL = 
    		new StringBuffer(256).
    		append("create table registrations (name ").
    		append(dialect.getType(SQLDialect.DataType.TEXT)).
    		append(" not null primary key, parent ").
    		append(dialect.getType(SQLDialect.DataType.TEXT)).
    		append(", storage ").
    		append(dialect.getType(SQLDialect.DataType.LARGE_BINARY)).
    		append(")").
    		toString();
    	
    	createAttributesTableSQL = 
    		new StringBuffer(256).
    		append("create table attributes (cname ").
    		append(dialect.getType(SQLDialect.DataType.TEXT)).
    		append(" not null, aname ").
    		append(dialect.getType(SQLDialect.DataType.TEXT)).
    		append(" not null, tags ").
    		append(dialect.getType(SQLDialect.DataType.TEXT)).
    		append(", value ").
    		append(dialect.getType(SQLDialect.DataType.LARGE_BINARY)).
    		append(", primary key (cname, aname), foreign key (cname) references registrations (name) on delete cascade)").
    		toString();
    	
    }
    

}
