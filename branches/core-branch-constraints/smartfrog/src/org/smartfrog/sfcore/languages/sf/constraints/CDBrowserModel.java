package org.smartfrog.sfcore.languages.sf.constraints;

public interface CDBrowserModel {
	void setES(Object es);
	Object attr(Object d, Object av, boolean showme);
	void redraw();
	void kill();
}
