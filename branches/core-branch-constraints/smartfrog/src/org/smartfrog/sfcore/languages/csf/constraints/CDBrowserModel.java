package org.smartfrog.sfcore.languages.csf.constraints;

public interface CDBrowserModel {
	void setES(Object es);
	Object attr(Object d, Object av);
	void redraw();
	void kill();
}
