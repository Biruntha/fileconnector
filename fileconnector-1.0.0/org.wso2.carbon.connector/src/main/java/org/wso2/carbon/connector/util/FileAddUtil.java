package org.wso2.carbon.connector.util;

import de.schlichtherle.truezip.zip.ZipParametersException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileAddUtil {

    private static Log log = LogFactory.getLog(FileAddUtil.class);

    /**
     *
     * @param zipFile
     * @param folder
     * @throws IOException
     */
    public static void addFilesToExistingZip(File zipFile,
                                             File folder) throws IOException {
        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();

        zipFile.renameTo(tempFile);

        byte[] buf = new byte[4096];
        File[] listOfFiles = folder.listFiles();

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean notInFiles = true;
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    if (listOfFiles[i].getName().equals(name)) {
                        notInFiles = false;
                        break;
                    }
                }
            }
            if (notInFiles) {
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(name));
                // Transfer bytes from the ZIP file to the output file
                int len;
                while ((len = zin.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            entry = zin.getNextEntry();
        }
        // Close the streams
        zin.close();
        // Compress the files
        for (int i = 0; i < listOfFiles.length; i++) {
            InputStream in = new FileInputStream(listOfFiles[i]);
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(listOfFiles[i].getName()));
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Complete the entry
            out.closeEntry();
            in.close();
        }
        // Complete the ZIP file
        out.close();
        tempFile.delete();
    }
}
