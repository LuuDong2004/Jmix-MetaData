package com.company.jmixdatastore.view.minio;

import com.company.jmixdatastore.dto.BucketInfo;
import com.company.jmixdatastore.service.minio.impl.MinioObjectStorageService;
import com.company.jmixdatastore.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.grid.DataGrid;
import com.vaadin.flow.component.textfield.TextField;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "minio-buckets", layout = MainView.class)
@ViewController("MinioBucketsView")
@ViewDescriptor("Minio-buckets-view.xml")
public class MinioBucketsView extends StandardView {

    @Autowired
    private MinioObjectStorageService minioService;

    @ViewComponent
    private DataGrid<BucketInfo> bucketsTable;

    @ViewComponent
    private CollectionContainer<BucketInfo> bucketsDc;

    @ViewComponent
    private TextField bucketNameField;

    @Subscribe
    public void onInit(InitEvent event) {
        loadBuckets();
    }

    @Subscribe("refreshBtn")
    public void onRefreshBtnClick(ClickEvent<Button> event) {
        loadBuckets();
    }

    @Subscribe("createBtn")
    public void onCreateBtnClick(ClickEvent<Button> event) {
        String name = bucketNameField.getValue();
        if (name == null || name.isBlank()) {
            Notification.show("Bucket name is required");
            return;
        }
        try {
            minioService.createBucket(name);
            Notification.show("Created bucket: " + name);
            loadBuckets();
        } catch (Exception e) {
            Notification.show("Create bucket failed: " + e.getMessage());
        }
    }

    @Subscribe("deleteBtn")
    public void onDeleteBtnClick(ClickEvent<Button> event) {
        var selected = bucketsTable.getSingleSelectedItem();
        if (selected == null) {
            Notification.show("Select a bucket first");
            return;
        }
        try {
            minioService.deleteBucket(selected.getName());
            Notification.show("Deleted bucket: " + selected.getName());
            loadBuckets();
        } catch (Exception e) {
            Notification.show("Delete bucket failed: " + e.getMessage());
        }
    }

    private void loadBuckets() {
        try {
            List<BucketInfo> list = minioService.listBuckets();
            bucketsDc.setItems(list);
        } catch (Exception e) {
            Notification.show("Load buckets failed: " + e.getMessage());
        }
    }
}


