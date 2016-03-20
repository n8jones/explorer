
package me.natejones.explorer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;

public class FindDuplicatesHandler {
	@Execute
	public void execute(
			@Named(IServiceConstants.ACTIVE_SELECTION) Item selection)
					throws IOException {
		Path p = Paths.get(selection.getName());
		List<Path> files = new ArrayList<>();
		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
					throws IOException {
				System.out.println(file);
				return FileVisitResult.CONTINUE;
			}
		};
		Files.walkFileTree(p, visitor);
	}

	@CanExecute
	public boolean canExecute(
			@Named(IServiceConstants.ACTIVE_SELECTION) Item selection) {
		return selection != null;
	}
}