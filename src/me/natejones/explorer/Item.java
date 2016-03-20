package me.natejones.explorer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Path;

public class Item {
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private String name;

	public Item(Path p) {
		name = p.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		pcs.firePropertyChange("name", this.name, this.name = name);
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	@Override
	public String toString() {
		return String.format("Item [name=%s]", name);
	}
}