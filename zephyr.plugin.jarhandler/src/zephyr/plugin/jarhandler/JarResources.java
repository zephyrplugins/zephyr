package zephyr.plugin.jarhandler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public final class JarResources {
  public final File jarFile;

  private final Map<String, Integer> fileToSize = new Hashtable<String, Integer>();
  private final Map<String, byte[]> fileToContent = new Hashtable<String, byte[]>();

  public JarResources(File jarFile) {
    this.jarFile = jarFile;
    buildJarDictionary();
  }

  public Manifest getManifest() {
    InputStream resource = getResourceAsStream("META-INF/MANIFEST.MF");
    if (resource == null)
      return null;
    try {
      return new Manifest(resource);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public InputStream getResourceAsStream(String name) {
    byte[] resource = getResource(name);
    if (resource == null)
      return null;
    return new ByteArrayInputStream(resource);
  }

  public byte[] getResource(String name) {
    return fileToContent.get(name);
  }

  private void buildJarDictionary() {
    try {
      ZipFile zipFile = new ZipFile(jarFile);
      Enumeration<? extends Object> e = zipFile.entries();
      while (e.hasMoreElements()) {
        ZipEntry ze = (ZipEntry) e.nextElement();
        fileToSize.put(ze.getName(), new Integer((int) ze.getSize()));
      }
      zipFile.close();

      FileInputStream fis = new FileInputStream(jarFile);
      ZipInputStream zis = new ZipInputStream(fis);
      ZipEntry ze = null;
      while ((ze = zis.getNextEntry()) != null) {
        if (ze.isDirectory())
          continue;
        int size = (int) ze.getSize();
        if (size == -1)
          size = fileToSize.get(ze.getName()).intValue();
        byte[] b = new byte[size];
        int rb = 0;
        int chunk = 0;
        while (size - rb > 0) {
          chunk = zis.read(b, rb, size - rb);
          if (chunk == -1)
            break;
          rb += chunk;
        }

        fileToContent.put(ze.getName(), b);
      }
    } catch (NullPointerException e) {
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
