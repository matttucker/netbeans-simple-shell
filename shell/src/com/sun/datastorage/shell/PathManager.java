/**
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original  Software is Simple Shell. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc.
 *
 * Matt Tucker, Sun Microsystems
 *
 */
package com.sun.datastorage.shell;

import com.sun.datastorage.shell.completion.FileCompletionExtensionFilter;
import com.sun.datastorage.shell.completion.FileCompletionFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Vector;

import org.netbeans.api.java.classpath.ClassPath;


import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;

public class PathManager {

    public static final String NONEXISTENT_PATH_TAG = "NONEXISTENT: ";
    Vector<String> paths = new Vector<String>(10);
    File cwd;

    /**
     * Class to manage the current working directory and the search path.
     */
    public PathManager() {
        cwd = new File(System.getProperty("user.dir"));
    }

    /**
     * Sets the current working directory.
     *
     * @param directory name of directory to change to.
     */
    public void setCwd(String directory) throws FileNotFoundException, IOException {
        //directory = replaceRelatives(directory);
        File dir = new File(directory);

        if (!dir.isAbsolute()) {
            dir = new File(cwd, dir.getPath());
        }

        if (dir.exists() && dir.isDirectory()) {
            cwd = dir.getCanonicalFile();
        } else {
            throw new FileNotFoundException();
        }
    }

    /**
     * Get the current working directory.
     */
    public File getCwd() {
        return cwd;
    }

    /**
     * Converts a file name string into a file. If the file name is absolute
     * the associated file is returned. If the file name is relative the list of
     * paths is searched to find the location of the file. If it exists the
     * resulting file is returned.
     *
     *@param fileName name of the file to find.
     */
    @SuppressWarnings("empty-statement")
    public File getFile(String fileName) throws IOException {
        FileObject fileObject = getFileObject(fileName);
        if (fileObject == null) {
            return null;
        } else {
            return FileUtil.toFile(fileObject);
        }

    }

    public FileObject getFileObject(String fileName) throws IOException {

        File aFile = new File(fileName);

        if (aFile.isAbsolute()) {

            return FileUtil.toFileObject(aFile);

        } else {


            // search cwd
            aFile = new File(cwd, fileName);

            if (aFile.exists() && aFile.isFile()) {
                return FileUtil.toFileObject(aFile.getCanonicalFile());
            }

            // search registered paths.
            for (String path : paths) {

                if (path.startsWith("jar:")) {
                    String packagepath = path.substring(4).replace(".", "/");

                    // just system
                    //URL fileUrl = ClassLoader.getSystemResource(packagepath + fileName);
                    // just shell module jar
                    // URL fileUrl2 = getClass().getClassLoader().getResource(packagepath + fileName);

                    // all modules
                    URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(packagepath + "/" + fileName);

                    if (fileUrl != null) {
                        ClassPath cp = ClassPath.getClassPath(null, ClassPath.EXECUTE);
                        //FileObject fo = (FileObject) cp.findResource(packagepath + fileName);
                        FileObject fo = URLMapper.findFileObject(fileUrl);
                        return fo;

                    }

                } else {

                    aFile = new File(path, fileName);

                    if (aFile.exists()) {
                        return FileUtil.toFileObject(aFile.getCanonicalFile());
                    }
                }
            }

            return null;
        }
    }

    /**
     * Converts a file name string into a file. If the file name is absolute
     * the associated file is returned.  If it not, a file in the cwd is
     * returned.
     *
     *@param fileName name of the file
     */
    public File newFile(String fileName) throws IOException {

        File aFile = new File(fileName);

        if (aFile.isAbsolute()) {
            return aFile.getCanonicalFile();
        } else {
            aFile = new File(cwd, fileName);
            return aFile.getCanonicalFile();
        }
    }

    public Vector<FileMatch> getFileListExt(String searchName, String[] exts) throws IOException {
        File searchFile = new File(searchName);
        String fileSearchStr = searchFile.getName();

        if (searchName.endsWith("/")) {
            fileSearchStr += "/";
        }
        FilenameFilter fileFilter = new FileCompletionExtensionFilter(fileSearchStr, exts);

        return getFileList(searchName, fileFilter);
    }

    /**
     * fileType :
     * DIRS_AND_FILES = 0;
     * FILES_ONLY = 1;
     * DIRS_ONLY = 2;
     *  
     */
    public Vector<FileMatch> getFileList(String searchName, int fileType) throws IOException {
        File searchFile = new File(searchName);
        String fileSearchStr = searchFile.getName();

        if (searchName.endsWith("/")) {
            fileSearchStr += "/";
        }
        FilenameFilter fileFilter = new FileCompletionFilter(fileSearchStr, fileType);

        return getFileList(searchName, fileFilter);
    }

    /**
     * Finds a list of files that start with a specified start string.
     *
     * @param searchName
     * @return
     */
    public Vector<FileMatch> getFileList(String searchName, FilenameFilter fileFilter) throws IOException {

        File searchFile = new File(searchName);

        Vector<FileMatch> fileMatches = new Vector<FileMatch>();
        String prefixPath;
        String fullPath;
        File searchDir;

        if (searchFile.isAbsolute() == true) {
            //if the file is absolute then all we have to do is get a listing in
            //that directory of files that start with searchName

            if (searchName.endsWith("/")) {
                //if search ends with a slash , then look at all files in that directory.
                searchDir = searchFile;
            } else {
                //if searchName is a partial name in a parent directory, then search 
                //parent directory for files starting with fileSearchStr.
                searchDir = searchFile.getParentFile();
            }

            for (File match : searchDir.listFiles(fileFilter)) {
                FileMatch fm = new FileMatch();
                fm.prefixPath = searchName;
                fm.file = match.getCanonicalFile();
                fullPath = fm.file.getCanonicalPath();
//                if (fm.prefixPath.contains("/")) {
//                    fm.match = fullPath.substring(fm.prefixPath.lastIndexOf("/") + 1);
//                } else {
//                    fm.match = fullPath.substring(fm.prefixPath.length() + 1);
//                }
                fm.match = fm.file.getName();
                fileMatches.add(fm);
            }
            return fileMatches;

        } else {

            // if the file is not absolute then things are more complex, as we will have to

            // (1) search in cwd
            prefixPath = cwd.getCanonicalPath();

            if (searchName.isEmpty()) {
                //show everything in the cwd.
                searchDir = cwd;
                // matches = parent.listFiles(new FileCompletionExtensionFilter(null));

            } else {
                searchFile = new File(cwd, searchName);
                if (searchName.endsWith("/")) {
                    searchDir = searchFile;
                } else {
                    searchDir = searchFile.getParentFile();
                }
                // matches = parent.listFiles(new FileCompletionExtensionFilter(fileSearchStr));
            }


            for (File match : searchDir.listFiles(fileFilter)) {
                FileMatch fm = new FileMatch();
                fm.prefixPath = prefixPath;
                fm.file = match.getCanonicalFile();
                fullPath = fm.file.getCanonicalPath();
                try {
//                    if (fm.prefixPath.contains("/")) {
//                        fm.match = match.getAbsolutePath().substring(fm.prefixPath.lastIndexOf("/") + 1);
//                    } else {
//                        fm.match = match.getAbsolutePath().substring(fm.prefixPath.length() + 1);
//                    }
                    fm.match = fm.file.getName();
                } catch (Exception ex) {
                    System.out.println(fm.prefixPath + " " + fm.prefixPath.length() + " " + fullPath + " " + fullPath.length());
                }
                fileMatches.add(fm);
            }



            // (2) search in the directories registered in the path.
            for (String path : paths) {
                if (!path.startsWith("jar")) {
                    File pf = new File(path);
                    prefixPath = pf.getCanonicalPath();

                    //determine new directory
                    if (searchName.isEmpty()) {
                        //show everything in the path dir.
                        searchDir = pf;
                    } else {
                        searchFile = new File(path, searchName);
                        if (searchName.endsWith("/")) {
                            searchDir = searchFile;
                        } else {
                            searchDir = searchFile.getParentFile();
                        }
                    }

                    File[] matches = searchDir.listFiles(fileFilter);
                    if (matches != null) {

                        for (File match : matches) {
                            FileMatch fm = new FileMatch();
                            fm.prefixPath = prefixPath;
                            fm.file = match.getCanonicalFile();
                            fullPath = fm.file.getCanonicalPath();
                            fm.match = fullPath.substring(fm.prefixPath.length() + 1);
                            fileMatches.add(fm);
                        }
                    }
                }
            }

            // return fileMatches.toArray(new File[fileMatches.size()]);
            return fileMatches;
        }
    }

    /**
     * Append a path to the list of paths.
     *
     * @param path path to add.
     */
    public void addPath(String path) throws FileNotFoundException {
        if (!path.isEmpty()) {
            path = path.replace("\\", "/");
            if (path.endsWith("/") == false && !path.startsWith("jar:")) {
                path = path + "/";
            }


            if (paths.indexOf(path) == -1) {
                // only add unique paths.
                paths.add(path);
            }

        }
    }

    /**
     * Remove a path from the list of paths.
     *@param path path to remove.
     */
    public void removePath(String path) {
        path = path.replace("\\", "/");
        int idx = paths.indexOf(path);
        if (idx != -1) {
            paths.remove(idx);
        }
    }

    /**
     * Determine if a path name exists.
     *
     *@return true if the path name exists, false if not.
     */
    static public boolean pathExists(String path) {
        if (path.startsWith("jar:")) {
            String packagepath = path.substring(4).replace(".", "/");
            URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(packagepath + "/");

            return (fileUrl != null);
        } else {
            File file = new File(path);

            return file.exists() && file.isDirectory();
        }
    }

    /**
     *
     */
    public void setPath(String paths) throws FileNotFoundException {
        String[] somePaths = paths.split(";");
        clearPath();
        for (String path : somePaths) {
            addPath(path.trim());
        }
    }

    public void clearPath() {
        this.paths = new Vector<String>(10);
    }

    /**
     * 
     * @return an array of path names.
     */
    public String[] getPath() {
        String[] pathArray = new String[paths.size()];
        paths.toArray(pathArray);
        return pathArray;
    }

    /**
     *
     */
    public void print(PrintWriter out) {
        for (String path : paths) {
            out.println(path);
        }
    }

    /**
     *
     */
    public void print(PrintWriter out, PrintWriter error) {
        for (String path : paths) {
            if (path.startsWith("jar:")) {
                out.println(path);
            } else {
                File p = new File(path);
                if (p.exists()) {
                    out.println(path);
                } else {
                    error.println(NONEXISTENT_PATH_TAG + path);
                }
            }
        }
    }

    @Override
    public String toString() {
        String pathStr = "";
        for (String path : paths) {
            pathStr += path + ";";
        }
        return pathStr;
    }
}
