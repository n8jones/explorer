package me.natejones.explorer;

import java.nio.file.Path;

public class Item {
	private String name;

	public Item(Path p) {
		name = p.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("Item [name=%s]", name);
	}
}