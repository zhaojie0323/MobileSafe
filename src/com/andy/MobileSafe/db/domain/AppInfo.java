package com.andy.MobileSafe.db.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private String label;
	private String packageName;
	private Drawable icon;
	private boolean isSdCard;
	private boolean isSystem;
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public boolean isSdCard() {
		return isSdCard;
	}
	public void setSdCard(boolean isSdCard) {
		this.isSdCard = isSdCard;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
}
