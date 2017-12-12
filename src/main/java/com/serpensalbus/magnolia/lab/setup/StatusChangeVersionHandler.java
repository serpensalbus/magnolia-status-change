package com.serpensalbus.magnolia.lab.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.BootstrapConditionally;
import info.magnolia.module.delta.CheckAndModifyPropertyValueTask;
import info.magnolia.module.delta.CheckOrCreatePropertyTask;
import info.magnolia.module.delta.Task;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is optional and lets you manage the versions of your module,
 * by registering "deltas" to maintain the module's configuration, or other type of content.
 * If you don't need this, simply remove the reference to this class in the module descriptor xml.
 *
 * @see info.magnolia.module.DefaultModuleVersionHandler
 * @see info.magnolia.module.ModuleVersionHandler
 * @see info.magnolia.module.delta.Task
 */
public class StatusChangeVersionHandler extends DefaultModuleVersionHandler {

    private final Task addImplementationClassForEditingPages = new CheckOrCreatePropertyTask( "Add implementation class for the editPage dialog action in pages.", "", RepositoryConstants.CONFIG, "/modules/pages/dialogs/editPage/actions/commit", "implementationClass",  "com.serpensalbus.magnolia.lab.ui.dialog.action.ChangeNodeNameDialogAction");
    private final Task addDamRenameFolderDialog = new BootstrapConditionally("Add the rename folder dialog to the DAM app.", "", "/mgnl-bootstrap/magnolia-status-change/dialogs/config.modules.dam-app.dialogs.renameFolder.xml");
    private final Task changeDamRenameFolderDialog = new CheckAndModifyPropertyValueTask("Change the rename folder dialog in the DAM app.", "", RepositoryConstants.CONFIG, "/modules/dam-app/apps/assets/subApps/browser/actions/editFolder", "dialogName", "ui-framework:folder", "dam-app:renameFolder");


    @Override
    protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        final List<Task> tasks = new ArrayList<>();
        tasks.add(addImplementationClassForEditingPages);
        tasks.add(addDamRenameFolderDialog);
        tasks.add(changeDamRenameFolderDialog);
        return tasks;
    }
}
