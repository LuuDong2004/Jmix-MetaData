package com.company.jmixdatastore.view.dbmanagement;

import com.company.jmixdatastore.entity.SourceDb;
import com.company.jmixdatastore.entity.TableGroup;
import com.company.jmixdatastore.service.dbcon.TableGroupingService;
import com.company.jmixdatastore.service.dbcon.DbConnect;
import com.company.jmixdatastore.service.dbcon.DbConnectFactory;
import com.company.jmixdatastore.view.main.MainView;
import com.company.jmixdatastore.view.sourcedb.SourceDbDetailView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;

import com.vaadin.flow.component.html.Span;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.entity.KeyValueEntity;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.model.KeyValueCollectionContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


@Route(value = "db-management-view", layout = MainView.class)
@ViewController(id = "DbManagementView")
@ViewDescriptor(path = "db-management-view.xml")
public class DbManagementView extends StandardView {

    @Autowired
    protected DataManager dataManager;
    @Autowired
    private DialogWindows dialogWindows;

    @Autowired
    private DbConnectFactory dbConnectFactory;

    @Autowired
    private TableGroupingService tableGroupingService;

    @ViewComponent
    private CollectionLoader<SourceDb> sourceDbsDl;

    @ViewComponent
    private EntityComboBox<SourceDb> dbSourseComboBox;

    @ViewComponent
    private BoxLayout rightbox;

    @ViewComponent
    private Span tableCount;

    @ViewComponent
    private Span fieldCount;

    @ViewComponent
    private Span connectionStatus;

    @ViewComponent
    private KeyValueCollectionContainer tablesDc;

    @ViewComponent
    private KeyValueCollectionContainer fieldsDc;

    @Autowired
    private Notifications notifications;

    @Subscribe(id = "newButton", subject = "clickListener")
    public void onNewButtonClick(final ClickEvent<JmixButton> event) {
        dialogWindows.detail(this, SourceDb.class)
                .newEntity()
                .withAfterCloseListener(closeEvent -> {
                    if (closeEvent.closedWith(StandardOutcome.SAVE)) {
                        sourceDbsDl.load();
                        SourceDbDetailView sourceDbDetailView = (SourceDbDetailView) closeEvent.getView();
                        SourceDb entity = sourceDbDetailView.getEditedEntity();
                        dbSourseComboBox.setValue(entity);
                    }

                })
                .build()
                .open();

    }

    @Subscribe(id = "tablesDc", target = Target.DATA_CONTAINER)
    public void onTablesDcItemChange(final InstanceContainer.ItemChangeEvent<KeyValueEntity> event) {
        SourceDb selectedSourceDb = dbSourseComboBox.getValue();
        fieldsDc.getMutableItems().clear();
        KeyValueEntity selectedItem = event.getItem();
        if (selectedItem == null) {
            fieldCount.setText("Tổng số cột: 0");
            return;
        }

        Boolean isGroup = selectedItem.getValue("isGroup");
        if (isGroup != null && isGroup) {
            // User selected a group header, show group info
            String domainName = selectedItem.getValue("name");
            notifications.create("Selected domain: " + domainName + ". Please select a specific table to view columns.")
                    .withType(Notifications.Type.DEFAULT)
                    .withPosition(Notification.Position.TOP_END)
                    .show();
            fieldCount.setText("Tổng số cột: Select a table to view columns");
            return;
        }

        // Get the actual table name (remove tree formatting)
        String actualTableName = selectedItem.getValue("actualTableName");
        if (actualTableName == null) {
            String displayName = selectedItem.getValue("name");
            actualTableName = displayName.replace("  └─ ", "").trim();
        }

        notifications.create("Selected table: " + actualTableName)
                .withType(Notifications.Type.SUCCESS)
                .withPosition(Notification.Position.TOP_END)
                .show();

        DbConnect dbConnect = dbConnectFactory.get(selectedSourceDb);
        List<KeyValueEntity> fieldList = dbConnect.loadTableFields(selectedSourceDb, actualTableName);
        fieldsDc.setItems(fieldList);
        fieldCount.setText("Tổng số cột: " + fieldList.size());
    }

    @Subscribe("dbSourseComboBox")
    public void onDbSourseComboBoxComponentValueChange(final AbstractField.ComponentValueChangeEvent<EntityComboBox<SourceDb>, SourceDb> event) {
        SourceDb selectedSourceDb = event.getValue();
        if (selectedSourceDb == null) {
            tablesDc.setItems(List.of());
            fieldsDc.setItems(List.of());
            tableCount.setText("Tổng số bảng: 0");
            fieldCount.setText("Tổng số cột: 0");
            connectionStatus.setText("Trạng thái: Chưa kết nối");
            return;
        }

        DbConnect dbConnect = dbConnectFactory.get(selectedSourceDb);
        List<String> tableList = dbConnect.loadTableList(selectedSourceDb);
        fieldsDc.getMutableItems().clear();

        // Group tables by domain
        List<TableGroup> tableGroups = tableGroupingService.groupTablesByDomain(tableList);
        List<KeyValueEntity> hierarchicalItems = new ArrayList<>();

        for (TableGroup group : tableGroups) {
            // Add group header
            KeyValueEntity groupEntity = dataManager.create(KeyValueEntity.class);
            groupEntity.setValue("name", group.getName());
            groupEntity.setValue("description", "Domain: " + group.getName() + " (" + group.getTables().size() + " tables)");
            groupEntity.setValue("isGroup", true);
            groupEntity.setValue("domain", group.getDomain());
            hierarchicalItems.add(groupEntity);

            // Add tables under group
            for (String tableName : group.getTables()) {
                KeyValueEntity tableEntity = dataManager.create(KeyValueEntity.class);
                tableEntity.setValue("name", "  └─ " + tableName);
                tableEntity.setValue("description", "Table: " + tableName);
                tableEntity.setValue("isGroup", false);
                tableEntity.setValue("actualTableName", tableName);
                tableEntity.setValue("parentDomain", group.getDomain());
                hierarchicalItems.add(tableEntity);
            }
        }

        tablesDc.setItems(hierarchicalItems);
        tableCount.setText("Tổng số bảng: " + tableList.size() + " (grouped into " + tableGroups.size() + " domains)");
        fieldsDc.setItems(List.of());
        fieldCount.setText("Tổng số cột: 0");
        connectionStatus.setText("Trạng thái: Đã kết nối");

        // Show grouping summary notification
        String summary = tableGroups.stream()
                .map(group -> group.getName() + ": " + group.getTables().size())
                .reduce((a, b) -> a + ", " + b)
                .orElse("No tables");

        notifications.create("Tables grouped by domain: " + summary)
                .withType(Notifications.Type.SUCCESS)
                .withPosition(Notification.Position.TOP_END)
                .show();
    }
}
