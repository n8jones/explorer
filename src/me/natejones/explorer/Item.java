package me.natejones.explorer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.swing.Icon;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class Item {
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private final Path path;

	public Item(Path path) {
		this.path = Objects.requireNonNull(path, "path must not be null");
	}

	public Path getPath() {
		return path;
	}

	public String getName() {
		Path fn = path.getFileName();
		return fn == null ? path.toString() : fn.toString();
	}

	public Long getSize() throws IOException {
		return Files.isRegularFile(path) ? Files.size(path) : null;
	}

	public String getType() throws IOException {
		return Files.probeContentType(path);
	}

	public Image getIcon() {
		Icon icon = javax.swing.filechooser.FileSystemView.getFileSystemView()
				.getSystemIcon(path.toFile());
		BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = img.createGraphics();
		icon.paintIcon(null, graphics, 0, 0);
	   graphics.dispose();
		return SwingFXUtils.toFXImage(img, null);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	@Override
	public String toString() {
		return String.format("Item [%s]", path);
	}
}