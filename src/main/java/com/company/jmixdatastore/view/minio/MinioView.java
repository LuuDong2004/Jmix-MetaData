package com.company.jmixdatastore.view.minio;

import com.company.jmixdatastore.dto.BucketInfo;
import com.company.jmixdatastore.dto.ObjectInfo;
import com.company.jmixdatastore.service.minio.KeyNamingStrategy;
import com.company.jmixdatastore.service.minio.ObjectStorageService;
import com.company.jmixdatastore.service.minio.PresignedUrlService;
import com.company.jmixdatastore.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.upload.FileUploadField;
import io.jmix.flowui.kit.component.upload.event.FileUploadSucceededEvent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextArea;

import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.InstanceContainer;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.time.format.DateTimeFormatter;

@Route(value = "minio-view", layout = MainView.class)
@ViewController("MinioView") // ID chính thức
@ViewDescriptor("Minio-view.xml")
public class MinioView extends StandardView {

    @Autowired
    private ObjectStorageService storage;

    @Autowired
    private PresignedUrlService presigned;

    @Autowired
    private KeyNamingStrategy keyNaming;

    @ViewComponent
    private FileUploadField uploadField;

    @ViewComponent
    private DataGrid<ObjectInfo> filesTable;

    @ViewComponent
    private CollectionContainer<ObjectInfo> filesDc;

    @ViewComponent
    private InstanceContainer<ObjectInfo> selectedFileDc;

    @ViewComponent
    private TextField prefixField;

    @ViewComponent
    private TextField newBucketField;

    @ViewComponent
    private Label statusLabel;

    @ViewComponent
    private TextArea fileInfoText;

    @ViewComponent
    private com.vaadin.flow.component.orderedlayout.VerticalLayout fileDetailPanel;

    @Subscribe
    public void onInit(InitEvent event) {
        loadBuckets();
        loadFiles();
        setStatus("Ready");

        fileDetailPanel.setVisible(false);

        // Thêm selection listener cho filesTable
        filesTable.addSelectionListener(e -> {
            var selected = e.getFirstSelectedItem();
            if (selected.isPresent()) {
                updateFileDetails(selected.get());
            } else {
                clearFileDetails();
            }
        });
    }

    private void updateFileDetails(ObjectInfo fileInfo) {
        selectedFileDc.setItem(fileInfo);

        // Hiển thị tab detail
        fileDetailPanel.setVisible(true);

        // Tạo thông tin chi tiết cho textArea
        StringBuilder info = new StringBuilder();
        info.append("File Details:\n");
        info.append("=============\n\n");
        info.append("Path: ").append(fileInfo.getKey()).append("\n");
        info.append("Size: ").append(formatFileSize(fileInfo.getSize())).append("\n");
        info.append("Content Type: ").append(fileInfo.getContentType() != null ? fileInfo.getContentType() : "Unknown")
                .append("\n");

        if (fileInfo.getLastModified() != null) {
            info.append("Last Modified: ")
                    .append(fileInfo.getLastModified().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .append("\n");
        }

        // Thêm thông tin bucket
        String bucket = getSelectedBucketName();
        if (bucket != null) {
            info.append("Bucket: ").append(bucket).append("\n");
        }

        // Thêm thông tin về file type
        String fileName = getFileNameFromKey(fileInfo.getKey());
        info.append("File Name: ").append(fileName).append("\n");
        info.append("File Extension: ").append(getFileExtension(fileName)).append("\n");

        fileInfoText.setValue(info.toString());
    }

    private void clearFileDetails() {
        selectedFileDc.setItem(null);
        fileInfoText.setValue("No file selected");
        // Ẩn tab detail
        fileDetailPanel.setVisible(false);
    }

    private String formatFileSize(Long size) {
        if (size == null)
            return "Unknown";

        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    private String getFileNameFromKey(String key) {
        if (key == null)
            return "";
        int lastSlash = key.lastIndexOf('/');
        return lastSlash >= 0 ? key.substring(lastSlash + 1) : key;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null)
            return "";
        int lastDot = fileName.lastIndexOf('.');
        return lastDot >= 0 ? fileName.substring(lastDot + 1) : "";
    }

    @Subscribe("uploadField")
    public void onUploadFieldFileUploadSucceeded(
            FileUploadSucceededEvent event) {
        try {
            byte[] content = uploadField.getValue();
            if (content == null) {
                Notification.show("No content uploaded", 3000, Notification.Position.MIDDLE);
                return;
            }
            String fileName = event.getFileName();
            String bucket = getSelectedBucketName();
            if (bucket == null || bucket.isBlank()) {
                Notification.show("Select a bucket first", 4000, Notification.Position.MIDDLE);
                return;
            }
            String key = keyNaming.buildKey("tenant1", "user1", fileName);
            String prefix = prefixField.getValue();
            if (prefix != null && !prefix.isBlank()) {
                // tiền tố để "giả lập thư mục" nếu người dùng nhập prefix
                key = (prefix.endsWith("/") ? prefix : prefix + "/") + key;
            }
            try (var is = new java.io.ByteArrayInputStream(content)) {
                String contentType = java.net.URLConnection.guessContentTypeFromName(fileName);
                if (contentType == null || contentType.isBlank())
                    contentType = "application/octet-stream";
                storage.put(bucket, key, is, content.length, contentType);
            }
            Notification.show("Uploaded: " + key);
            loadFiles();
            setStatus("Uploaded '" + fileName + "' to bucket '" + bucket + "'");
        } catch (Exception ex) {
            String msg = ex.getMessage();
            Throwable cause = ex.getCause();
            while (cause != null && cause.getCause() != null) {
                cause = cause.getCause();
            }
            if (cause != null && cause != ex && cause.getMessage() != null) {
                msg = msg + " - " + cause.getMessage();
            }
            Notification.show("Upload failed: " + msg, 7000, Notification.Position.MIDDLE);
            setStatus("Upload failed");
        }
    }

    @Subscribe("searchBtn")
    public void onSearchBtnClick(ClickEvent<Button> event) {
        loadFiles();
    }

    @Subscribe("createBucketBtn")
    public void onCreateBucketBtnClick(ClickEvent<Button> event) {
        String name = newBucketField.getValue();
        if (name == null || name.isBlank()) {
            Notification.show("Bucket name is required");
            return;
        }
        try {
            if (storage instanceof com.company.jmixdatastore.service.minio.impl.MinioObjectStorageService ms) {
                ms.createBucket(name);
            }
            loadBuckets();
            selectBucketByName(name);
            Notification.show("Created bucket: " + name);
            setStatus("Created bucket '" + name + "'");
        } catch (Exception e) {
            Notification.show("Create bucket failed: " + e.getMessage());
            setStatus("Create bucket failed");
        }
    }

    @Subscribe("downloadBtn")
    public void onDownloadBtnClick(ClickEvent<Button> event) {
        var selected = filesTable.getSingleSelectedItem();
        if (selected != null) {
            try {
                String bucket = getSelectedBucketName();
                if (bucket == null || bucket.isBlank()) {
                    Notification.show("Select a bucket first");
                    return;
                }
                String url = presigned.presignedGet(bucket, selected.getKey(), 300);
                // mở URL trên trình duyệt phía client
                getUI().ifPresent(ui -> ui.getPage().open(url));
                setStatus("Downloading '" + selected.getKey() + "'");
            } catch (Exception e) {
                Notification.show("Download error: " + e.getMessage());
                setStatus("Download failed");
            }
        }
    }

    @Subscribe("deleteBtn")
    public void onDeleteBtnClick(ClickEvent<Button> event) {
        var selected = filesTable.getSingleSelectedItem();
        if (selected != null) {
            String bucket = getSelectedBucketName();
            if (bucket == null || bucket.isBlank()) {
                Notification.show("Select a bucket first");
                return;
            }
            var dialog = new ConfirmDialog();
            dialog.setHeader("Confirm deletion");
            dialog.setText("Delete object '" + selected.getKey() + "' from bucket '" + bucket + "'?");
            dialog.setCancelable(true);
            dialog.setConfirmText("Delete");
            dialog.setRejectable(true);
            dialog.setRejectText("Cancel");
            dialog.addConfirmListener(e -> {
                try {
                    storage.delete(bucket, selected.getKey());
                    Notification.show("Deleted: " + selected.getKey());
                    setStatus("Deleted '" + selected.getKey() + "'");
                    loadFiles();
                } catch (Exception ex) {
                    Notification.show("Delete error: " + ex.getMessage());
                    setStatus("Delete failed");
                }
            });
            dialog.open();
        }
    }

    private void loadFiles() {
        try {
            String bucket = getSelectedBucketName();
            if (bucket == null || bucket.isBlank()) {
                filesDc.setItems(java.util.List.of());
                clearFileDetails();
                setStatus("Select a bucket to view files");
                return;
            }
            String prefix = prefixField.getValue();
            List<ObjectInfo> list = storage.list(bucket, prefix == null ? "" : prefix);
            filesDc.setItems(list);
            setStatus("Loaded " + list.size() + " object(s) from '" + bucket + "'");

            // Ẩn tab detail khi load files mới (vì chưa có file nào được chọn)
            if (list.isEmpty()) {
                clearFileDetails();
            }
        } catch (Exception e) {
            Notification.show("Load error: " + e.getMessage());
            setStatus("Load failed");
        }
    }

    private void setStatus(String text) {
        if (statusLabel != null) {
            statusLabel.setText(text);
        }
    }

    @ViewComponent
    private DataGrid<BucketInfo> bucketsTable;

    @ViewComponent
    private CollectionContainer<BucketInfo> bucketsDc;

    private void loadBuckets() {
        try {
            if (storage instanceof com.company.jmixdatastore.service.minio.impl.MinioObjectStorageService ms) {
                var buckets = ms.listBuckets();
                bucketsDc.setItems(buckets);
                if (!buckets.isEmpty() && getSelectedBucketName() == null) {
                    bucketsTable.setItems(buckets);
                    bucketsTable.select(buckets.get(0));
                }
                // khi người dùng chọn bucket khác, tự động reload files và clear details
                bucketsTable.addSelectionListener(e -> {
                    loadFiles();
                    clearFileDetails();
                });
            }
        } catch (Exception e) {
            Notification.show("Load buckets failed: " + e.getMessage());
        }
    }

    private String getSelectedBucketName() {
        var selected = bucketsTable == null ? null : bucketsTable.getSingleSelectedItem();
        return selected == null ? null : selected.getName();
    }

    private void selectBucketByName(String name) {
        if (bucketsDc == null || bucketsTable == null)
            return;
        var match = bucketsDc.getItems().stream()
                .filter(b -> name.equals(b.getName()))
                .findFirst();
        match.ifPresent(bucketsTable::select);
    }

    @Subscribe("deleteBucketBtn")
    public void onDeleteBucketBtnClick(ClickEvent<Button> event) {
        String bucket = getSelectedBucketName();
        if (bucket == null || bucket.isBlank()) {
            Notification.show("Select a bucket first");
            return;
        }
        var dialog = new ConfirmDialog();
        dialog.setHeader("Confirm deletion");
        dialog.setText("Delete bucket '" + bucket + "'? Bucket must be empty.");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete bucket");
        dialog.setRejectable(true);
        dialog.setRejectText("Cancel");
        dialog.addConfirmListener(e -> {
            try {
                if (storage instanceof com.company.jmixdatastore.service.minio.impl.MinioObjectStorageService ms) {
                    ms.deleteBucket(bucket);
                }
                Notification.show("Deleted bucket: " + bucket);
                setStatus("Deleted bucket '" + bucket + "'");
                loadBuckets();
                loadFiles();
            } catch (Exception ex) {
                Notification.show("Delete bucket failed: " + ex.getMessage());
                setStatus("Delete bucket failed");
            }
        });
        dialog.open();
    }

}