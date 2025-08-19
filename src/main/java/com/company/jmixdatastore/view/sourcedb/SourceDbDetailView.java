package com.company.jmixdatastore.view.sourcedb;

import com.company.jmixdatastore.entity.DBType;
import com.company.jmixdatastore.entity.SourceDb;
import com.company.jmixdatastore.service.dbcon.DbConnect;
import com.company.jmixdatastore.service.dbcon.DbConnectFactory;
import com.company.jmixdatastore.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "source-dbs/:id", layout = MainView.class)
@ViewController(id = "SourceDb.detail")
@ViewDescriptor(path = "source-db-detail-view.xml")
@EditedEntityContainer("sourceDbDc")
public class SourceDbDetailView extends StandardDetailView<SourceDb> {

    @Autowired
    private DbConnectFactory dbConnectFactory;

    @Autowired
    private Notifications notifications;

    @ViewComponent
    private TextField urlField;

    @ViewComponent
    private TextField hostField;

    @ViewComponent
    private TextField portField;

    @ViewComponent
    private TextField dbnameField;

    @ViewComponent
    private ComboBox<DBType> dbtypeField;

    // Biến này để ngăn không cho 2 chiều tự động update lẫn nhau gây loop vô tận
    private boolean isInternalChange = false;

    /**
     * Build URL khi user thay đổi host, port, dbname, dbtype
     */
    private void buildUrlFromFields() {
        if (isInternalChange) return;
        isInternalChange = true;
        try {
            DBType dbtype = dbtypeField.getValue();
            String host = safeStr(hostField.getValue());
            String port = safeStr(portField.getValue());
            String dbname = safeStr(dbnameField.getValue());
            String url = "";

            if (dbtype == null || host.isBlank() || port.isBlank() || dbname.isBlank()) {
                urlField.setValue("");
                return;
            }
            url = switch (dbtype) {
                case MYSQL -> String.format("jdbc:mysql://%s:%s/%s", host, port, dbname);
                case POSTGRES -> String.format("jdbc:postgresql://%s:%s/%s", host, port, dbname);
                case SQLSERVER -> String.format("jdbc:sqlserver://%s:%s;databaseName=%s", host, port, dbname);
            };
            urlField.setValue(url);
        } finally {
            isInternalChange = false;
        }
    }

    /**
     * Parse URL khi user nhập hoặc paste URL vào
     */
    private void parseUrlToFields() {
        if (isInternalChange) return;
        isInternalChange = true;
        try {
            String url = safeStr(urlField.getValue());
            if (url.isBlank()) return;

            if (url.startsWith("jdbc:mysql://")) {
                dbtypeField.setValue(DBType.MYSQL);
                String s = url.substring("jdbc:mysql://".length());
                String[] main = s.split("/", 2);
                String[] hostPort = main[0].split(":");
                hostField.setValue(hostPort[0]);
                portField.setValue(hostPort.length > 1 ? hostPort[1] : "3306");
                dbnameField.setValue(main.length > 1 ? main[1].split("[?;]")[0] : "");
            } else if (url.startsWith("jdbc:postgresql://")) {
                dbtypeField.setValue(DBType.POSTGRES);
                String s = url.substring("jdbc:postgresql://".length());
                String[] main = s.split("/", 2);
                String[] hostPort = main[0].split(":");
                hostField.setValue(hostPort[0]);
                portField.setValue(hostPort.length > 1 ? hostPort[1] : "5432");
                dbnameField.setValue(main.length > 1 ? main[1].split("[?;]")[0] : "");
            } else if (url.startsWith("jdbc:sqlserver://")) {
                dbtypeField.setValue(DBType.SQLSERVER);
                String s = url.substring("jdbc:sqlserver://".length());
                String[] main = s.split(";", 2);
                String[] hostPort = main[0].split(":");
                hostField.setValue(hostPort[0]);
                portField.setValue(hostPort.length > 1 ? hostPort[1] : "1433");
                String dbName = "";
                if (main.length > 1) {
                    for (String param : main[1].split(";")) {
                        if (param.startsWith("databaseName=")) {
                            dbName = param.substring("databaseName=".length());
                        }
                        else if (param.startsWith("database=")) {
                            dbName = param.substring("database=".length());
                        }
                    }
                }
                dbnameField.setValue(dbName);
            }
        } finally {
            isInternalChange = false;
        }
    }

    private String safeStr(String s) {
        return s == null ? "" : s.trim();
    }

    @Subscribe("hostField")
    public void onHostChange(TextField.ComponentValueChangeEvent<TextField, String> event) {
        buildUrlFromFields();
    }

    @Subscribe("portField")
    public void onPortChange(TextField.ComponentValueChangeEvent<TextField, String> event) {
        buildUrlFromFields();
    }

    @Subscribe("dbnameField")
    public void onDbnameChange(TextField.ComponentValueChangeEvent<TextField, String> event) {
        buildUrlFromFields();
    }

    @Subscribe("dbtypeField")
    public void onDbtypeChange(ComboBox.ComponentValueChangeEvent<ComboBox<DBType>, DBType> event) {
        buildUrlFromFields();
    }

    @Subscribe("urlField")
    public void onUrlChange(TextField.ComponentValueChangeEvent<TextField, String> event) {
        parseUrlToFields();
    }


    @Subscribe(id = "test_connection", subject = "clickListener")
    public void onDetailActionsClick(final ClickEvent<HorizontalLayout> event) {
        SourceDb currentSourceDb = getEditedEntity();
        DbConnect dbConnect = dbConnectFactory.get(currentSourceDb);
        boolean isConnected = dbConnect.connect(currentSourceDb);

        if (isConnected) {
            notifications.create("Kết nối thành công!")
                    .withType(Notifications.Type.SUCCESS)
                    .withPosition(Notification.Position.TOP_END)
                    .show();
        } else {
            notifications.create("Kết nối thất bại!")
                    .withType(Notifications.Type.ERROR)
                    .withPosition(Notification.Position.TOP_END)
                    .show();
        }
    }


}
