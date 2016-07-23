package org.taoningyu.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class TdeComplieASMifier {
	private static HashMap<String, String> fileMd5Hm = new HashMap<String, String>();

	private static void doDeComplie(File classFile) {
		try {
			InputStream clsFIps = new FileInputStream(classFile);
			ClassReader cr = new ClassReader(clsFIps);
			File deFile = new File(classFile.getParentFile().getCanonicalPath() + "/"
					+ classFile.getName().replace(".class", "Dump.java"));
			TraceClassVisitor cv = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(deFile));
			cr.accept(cv, 0);
			fileMd5Hm.put(deFile.getAbsolutePath(), Md5Utils.getMd5(deFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public static void main(String[] args) throws IOException, ZipException {

		String fileName = args[0];
		// String fileName = "D:\\jars\\autoextract1.0.jar";//args[0];
		if (fileName.endsWith(".class")) {
			doDeComplie(new File(fileName));
			System.out.println("DeComplied done." + "\t" + fileName);
		} else if (fileName.endsWith("jar") || fileName.endsWith("war")) {
			String outputDir = fileName.replace(".jar", "").replace(".war", "");
			//Unzip.unZip(fileName, outputDir);
			ZipFile zipFile = new ZipFile(new File(fileName));
			zipFile.extractAll(outputDir);
			doDeComplieAllFiles(outputDir);
			ArrayList<String> md5Result = new ArrayList<String>();
			for (Entry<String, String> tmpEntry : fileMd5Hm.entrySet()) {
				md5Result.add(tmpEntry.getKey().trim() + "\t" + tmpEntry.getValue().trim());
			}
			FileUtils.writeLines(new File(outputDir + "/" + "filesMd5.txt"), md5Result);
			System.out.println("DeComplied done." + "\t" + fileName + "\t" + outputDir);
		}
	}

	private static void doDeComplieAllFiles(String outputDir) {

		File rootFile = new File(outputDir);
		File[] tmpFiles = rootFile.listFiles();
		for (File tmpFile : tmpFiles) {
			if (tmpFile.isDirectory()) {
				try {
					doDeComplieAllFiles(tmpFile.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (tmpFile.getName().endsWith(".class")) {
				doDeComplie(tmpFile);
			}
		}
	}
}
