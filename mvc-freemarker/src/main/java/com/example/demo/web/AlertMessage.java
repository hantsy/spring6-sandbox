package com.example.demo.web;

import java.io.Serializable;

public class AlertMessage implements Serializable {

	enum Type {
		success, warning, danger, info
	}

	private Type type;

	private String text;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "AlertMessage [type=" + type + ", text=" + text + "]";
	}

	public static AlertMessage info(String text) {
		AlertMessage msg = new AlertMessage();
		msg.setType(Type.info);
		msg.setText(text);
		return msg;
	}

	public static AlertMessage success(String text) {
		AlertMessage msg = new AlertMessage();
		msg.setType(Type.success);
		msg.setText(text);
		return msg;
	}

	public static AlertMessage danger(String text) {
		AlertMessage msg = new AlertMessage();
		msg.setType(Type.danger);
		msg.setText(text);
		return msg;
	}

	public static AlertMessage warning(String text) {
		AlertMessage msg = new AlertMessage();
		msg.setType(Type.warning);
		msg.setText(text);
		return msg;
	}

}
