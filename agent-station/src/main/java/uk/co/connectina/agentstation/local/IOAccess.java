/* Agent Station environment for static and mobile software agents
 * Copyright (C) 2022  Dr Christos Bohoris
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * connectina.co.uk/agent-station
 */
package uk.co.connectina.agentstation.local;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

import org.apache.logging.log4j.LogManager;
import uk.co.connectina.agentstation.App;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.client.OperationException;

/**
 * An I/O-related utility.
 *
 * @author Dr Christos Bohoris
 */
public class IOAccess {

    private IOAccess() {

    }

    public static final String USER_HOME = "user.home";
    public static final String AGENT_STATION = ".AgentStation";
    public static final String APP_BASE_PATH = System.getProperty(USER_HOME) + File.separator + AGENT_STATION;
    public static final File APP_PACKAGE_FOLDER = new File(APP_BASE_PATH + File.separator + "packages");
    public static final File APP_CERT_FOLDER = new File(APP_BASE_PATH + File.separator + "certificates");
    public static final File APP_PROCESS_FOLDER = new File(APP_BASE_PATH + File.separator + "processes");
    public static final File APP_PROPERTY_FOLDER = new File(APP_BASE_PATH + File.separator + "properties");

    public static String getServerAndPortText(String server, int port) {
        String portText = MessageFormat.format("{0,number,#}", port);
        
        return MessageFormat.format("{0}:{1}", server, portText);
    }

    public static byte[] toByteArray(Object object) throws OperationException {
        byte[] tmpArray;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            try ( ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {
                objOut.writeObject(object);
                objOut.flush();
                tmpArray = byteOut.toByteArray();
            }
        } catch (IOException e) {
            throw new OperationException(e);
        }

        return tmpArray;
    }

    public static Object toObject(byte[] byteArray, URL url) throws OperationException {
        ObjectInput input = null;

        try ( ByteArrayInputStream bis = new ByteArrayInputStream(byteArray)) {
            input = new ObjectInputStream(bis) {

                @Override
                protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url}, cl);
                    // Replace the thread classloader - assumes there is permission to do so
                    Thread.currentThread().setContextClassLoader(urlClassLoader);

                    if (cl == null) {

                        return super.resolveClass(desc);
                    }

                    return Class.forName(desc.getName(), false, cl);
                }
            };

            return input.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new OperationException(e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                LogManager.getLogger(IOAccess.class).error(e);
            }
        }
    }

    public static String fromPathToURL(String localPackageLocation) throws OperationException {
        String localPackageLocationURL;
        try {
            localPackageLocationURL = IOAccess.getURLFromLocation(localPackageLocation).toExternalForm();
        } catch (MalformedURLException e) {
            throw new OperationException("Malformed local location package URL.");
        }

        return localPackageLocationURL;
    }

    public static byte[] readFromFile(String location) throws OperationException {
        try {
            return Files.readAllBytes(Paths.get(location));
        } catch (IOException e) {
            throw new OperationException("Failed to read file.");
        }
    }

    public static void writeToFile(String packageLocation, byte[] packageData) throws OperationException {
        try ( FileOutputStream outputStream = new FileOutputStream(packageLocation)) {
            outputStream.write(packageData);
        } catch (IOException e) {
            throw new OperationException("Failed to write file.");
        }
    }

    public static String checkMD5(String packageLocation, String hashCode) throws OperationException {
        String localPackageLocation;
        String md5;
        try {
            localPackageLocation = IOAccess.getLocalPackageLocation(packageLocation);
            md5 = getFileMD5(localPackageLocation.startsWith("file:") ? localPackageLocation.substring(5) : localPackageLocation);
        } catch (IOException e) {
            throw new OperationException(e);
        }
        if (!md5.equals(hashCode)) {
            throw new OperationException("Provided hash code does not match the md5 checksum of the package.");
        }

        return localPackageLocation;
    }

    public static String getStringMD5(String text) {
        try ( InputStream is = new ByteArrayInputStream(text.getBytes())) {

            return md5Hex(is);
        } catch (IOException e) {
            LogManager.getLogger(IOAccess.class).error(e);
        }

        return null;
    }

    public static String getFileMD5(String location) throws IOException {
        try ( InputStream is = Files.newInputStream(Paths.get(location))) {

            return md5Hex(is);
        }
    }

    public static void prepareAppFolder() throws IOException {
        File localAppFolder = new File(APP_BASE_PATH);
        if (!localAppFolder.exists()) {
            boolean created = localAppFolder.mkdir();
            if (!created) {
                throw new IOException("Cannot create local application folder: " + localAppFolder);
            }
        }
        if (!IOAccess.APP_PACKAGE_FOLDER.exists()) {
            boolean created = IOAccess.APP_PACKAGE_FOLDER.mkdir();
            if (!created) {
                throw new IOException("Cannot create local packages folder: " + IOAccess.APP_PACKAGE_FOLDER);
            }
        }
        if (!IOAccess.APP_CERT_FOLDER.exists()) {
            boolean created = IOAccess.APP_CERT_FOLDER.mkdir();
            if (!created) {
                throw new IOException("Cannot create local certificates folder: " + IOAccess.APP_CERT_FOLDER);
            }
        }
        if (!IOAccess.APP_PROCESS_FOLDER.exists()) {
            boolean created = IOAccess.APP_PROCESS_FOLDER.mkdir();
            if (!created) {
                throw new IOException("Cannot create local processes folder: " + IOAccess.APP_PROCESS_FOLDER);
            }
        }
        if (!IOAccess.APP_PROPERTY_FOLDER.exists()) {
            boolean created = IOAccess.APP_PROPERTY_FOLDER.mkdir();
            if (!created) {
                throw new IOException("Cannot create local properties folder: " + IOAccess.APP_PROPERTY_FOLDER);
            }
        }
    }

    public static URL copyPackage(String packageLocation) throws IOException {
        String localPackageLocation = IOAccess.getLocalPackageLocation(packageLocation);

        URL inURLLocation = IOAccess.getURLFromLocation(packageLocation);
        URL outURLLocation = IOAccess.getURLFromLocation(localPackageLocation);
        if (!localPackageLocation.equals(packageLocation)) {
            InputStream inputStream = inURLLocation.openStream();
            Files.copy(inputStream, Paths.get(outURLLocation.getPath()), StandardCopyOption.REPLACE_EXISTING);
        }

        return outURLLocation;
    }

    public static String getLocalPackageLocation(String packageLocation) {
        String fileName = packageLocation.substring(packageLocation.lastIndexOf("/") + 1);

        return APP_PACKAGE_FOLDER.getAbsolutePath() + File.separator + fileName;
    }

    public static URL getURLFromLocation(String location) throws MalformedURLException {

        return location.contains(":/") ? new URL(location) : new File(location).toURI().toURL();
    }

    public static String readFileFromJar(String filename) throws IOException {
        InputStream is = IOAccess.class.getResourceAsStream(filename);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");
        }
        br.close();
        isr.close();
        is.close();

        return sb.toString();
    }

    public static String getAboutAgent(Instance instance) {
        final int DIGITS_TO_HIGHLIGHT = 3;
        String part1 = instance.getShortId().substring(0, instance.getShortId().length() - DIGITS_TO_HIGHLIGHT);
        String part2 = instance.getShortId().substring(instance.getShortId().length() - DIGITS_TO_HIGHLIGHT);
        return MessageFormat.format("<html>Agent {0} Id={1}<strong>{2}", instance.getIdentity().getName(), part1, part2);
    }

    public static String getPlainAboutAgent(Instance instance) {
        return MessageFormat.format("Agent {0} Id={1}", instance.getIdentity().getName(), instance.getShortId());
    }

    public static String getAboutPlace(String placeName) {

        return MessageFormat.format("{0} Place", placeName);
    }

    public static String checkNewerVersion() {
        URL versionUrl;
        try {
            versionUrl = new URL("https://www.connectina.co.uk/agentstation/version");
        } catch (MalformedURLException e) {
            LogManager.getLogger(IOAccess.class).error(e);

            return null;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(versionUrl.openStream()));) {
            String inputLine = in.readLine();
            if (inputLine == null) {

                return null;
            }

            String[] latest = inputLine.split("\\.");
            String[] current = App.getAppProperty("app_version").split("\\.");
            if ((Integer.parseInt(latest[0]) > Integer.parseInt(current[0]))
                    || (Integer.parseInt(latest[0]) == Integer.parseInt(current[0])
                    && Integer.parseInt(latest[1]) > Integer.parseInt(current[1]))
                    || (Integer.parseInt(latest[0]) == Integer.parseInt(current[0])
                    && Integer.parseInt(latest[1]) == Integer.parseInt(current[1])
                    && Integer.parseInt(latest[2]) > Integer.parseInt(current[2]))) {

                return inputLine;
            }
        } catch (IOException | NumberFormatException e) {
            LogManager.getLogger(IOAccess.class).error(e);
        }

        return null;
    }

}
