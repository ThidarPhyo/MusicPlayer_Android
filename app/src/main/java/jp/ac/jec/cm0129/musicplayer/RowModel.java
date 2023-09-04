package jp.ac.jec.cm0129.musicplayer;

import java.io.File;

public class RowModel {
//    private String fileName;
//    private long fileSize;

    private File file;
//    public RowModel(String fileName, long fileSize) {
//        this.fileName = fileName;
//        this.fileSize = fileSize;
//    }
//
    public String getFileName() {
        return file.getName();
    }
//

    public RowModel(File file, String title) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
        public long getFileSize() {
        return file.length();
    }
}
