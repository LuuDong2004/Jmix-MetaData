package com.company.jmixdatastore.view.sourcedb;

import com.company.jmixdatastore.entity.SourceDb;
import com.company.jmixdatastore.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "source-dbs", layout = MainView.class)
@ViewController(id = "SourceDb.list")
@ViewDescriptor(path = "source-db-list-view.xml")
@LookupComponent("sourceDbsDataGrid")
@DialogMode(width = "64em")
public class SourceDbListView extends StandardListView<SourceDb> {
}
