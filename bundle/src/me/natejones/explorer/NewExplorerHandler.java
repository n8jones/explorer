
package me.natejones.explorer;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

public class NewExplorerHandler {
	@Execute
	public void execute(EPartService parts) {
		MPart part = parts.createPart(
				AppModelId.PARTDESCRIPTOR_ME_NATEJONES_EXPLORER_PARTDESCRIPTOR_EXPLORER);
		parts.showPart(part, PartState.ACTIVATE);
	}
}