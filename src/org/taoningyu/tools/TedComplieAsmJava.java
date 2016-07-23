package org.taoningyu.tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * 类TedComplieAsmJava.java的实现描述：编译修改后的ASM java文件为class文件。
 * @author Administrator 2016年7月11日 下午5:09:01
 */
public class TedComplieAsmJava {
    private static HashMap<String, String> fileMd5Hm = new HashMap<String, String>();
    private static String                  rootDir   = null;
    private static ZipFile                 zipFile   = null;

    public static void main(String[] args) throws Exception {

        rootDir = args[0];//"E:/Android/jars/weixin/weixin624android600-dex2jar/";
        zipFile = new ZipFile(args[1]);
        ArrayList<String> allDumpJavaFileNames = new ArrayList<String>();
        String os = System.getProperty("os.name");
        String divide = "/";
        if (os.startsWith("win") || os.startsWith("Win")) {
            divide = "\\";
        }
        getAllDumpJavaFileNames(new File(rootDir).getCanonicalPath() + divide, new File(rootDir), allDumpJavaFileNames);
        URL[] rootDirUrlArr = { new File(rootDir).toURI().toURL() };
        URLClassLoader myUrlClazzLoader = new URLClassLoader(rootDirUrlArr);
        for (String tmpDumpJavaFileName : allDumpJavaFileNames) {

            String[] compileArgs = { rootDir + tmpDumpJavaFileName.replace(".", "/") + ".java" };
            com.sun.tools.javac.Main.compile(compileArgs);
            Class<?> clz = myUrlClazzLoader.loadClass(tmpDumpJavaFileName);
            System.out.println(clz.getName());

            Method aDump = clz.getMethod("dump", null);
            byte[] aDumpByte = (byte[]) aDump.invoke(null, null);
            String[] fileNameArr = tmpDumpJavaFileName.split("\\.");
            String tmpFileName = "";
            for (int i = 0; i < fileNameArr.length - 1; i++) {
                tmpFileName = tmpFileName + fileNameArr[i] + divide;
            }
            String zipFolder = tmpFileName;
            tmpFileName = tmpFileName + fileNameArr[fileNameArr.length - 1].replace("Dump", ".class");
            File newClzFile = new File(rootDir + tmpFileName);
            FileUtils.writeByteArrayToFile(newClzFile, aDumpByte);
            updateMd5File(newClzFile);
            AddFilesDeflateComp(zipFolder, newClzFile);
            System.out.println(rootDir + tmpFileName);
        }
        if (allDumpJavaFileNames.size() > 0) {
            writeMd5File();
        }
        System.out.println("All Done. dump " + allDumpJavaFileNames.size() + " files");
    }

    private static void writeMd5File() throws IOException {

        ArrayList<String> md5Result = new ArrayList<String>();
        for (Entry<String, String> tmpEntry : fileMd5Hm.entrySet()) {
            md5Result.add(tmpEntry.getKey().trim() + "\t" + tmpEntry.getValue().trim());
        }
        FileUtils.writeLines(new File(rootDir + "/" + "filesMd5.txt"), md5Result);
        System.out.println("write new md5 file.");

    }

    private static void updateMd5File(File newClzFile) throws IOException {
        File deFile = new File(
                newClzFile.getParentFile().getCanonicalPath() + "/" + newClzFile.getName().replace(".class", "Dump.java"));
        fileMd5Hm.put(deFile.getAbsolutePath(), Md5Utils.getMd5(deFile));
    }

    private static void getAllDumpJavaFileNames(String rootDirPath, File rootDirF, ArrayList<String> allDumpJavaFileNames)
            throws IOException {
        for (File tmpFile : rootDirF.listFiles()) {
            if (tmpFile.isDirectory()) {
                getAllDumpJavaFileNames(rootDirPath, tmpFile, allDumpJavaFileNames);
            }
            if (tmpFile.isFile()) {
                if (tmpFile.getName().endsWith("Dump.java") && validMd5(tmpFile)) {
                    allDumpJavaFileNames.add(tmpFile.getParentFile().getCanonicalPath().replace(rootDirPath, "").replace("/", ".")
                            .replace("\\", ".").replace("..", ".") + "." + tmpFile.getName().replace(".java", ""));
                }
            }
        }

    }

    private static boolean validMd5(File tmpFile) throws IOException {
        if (fileMd5Hm.size() == 0) {
            initFileMd5();
        }
        String md5Val = Md5Utils.getMd5(tmpFile);

        if (fileMd5Hm.get(tmpFile.getAbsolutePath()).equals(md5Val)) {
            return false;
        } else {
            return true;
        }
    }

    private static void initFileMd5() throws IOException {

        List<String> allMd5Lines = FileUtils.readLines(new File(rootDir + "/" + "filesMd5.txt"));
        for (String tmpLi : allMd5Lines) {
            String[] tmpArr = tmpLi.split("\t");
            fileMd5Hm.put(tmpArr[0].trim(), tmpArr[1].trim());
        }
    }

    public static void AddFilesDeflateComp(String folder, File file) {
        try {

            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            parameters.setRootFolderInZip(folder);
            zipFile.addFile(file, parameters);

        } catch (ZipException e) {
            e.printStackTrace();
        }

    }
}
