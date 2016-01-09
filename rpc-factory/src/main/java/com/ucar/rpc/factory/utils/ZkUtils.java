package com.ucar.rpc.factory.utils;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 与zk交互的工具类
 * Created by zhangyong on 2015/10/31.
 */
public class ZkUtils {

    private final static Logger logger = LoggerFactory.getLogger(ZkUtils.class);

    /**
     * make sure a persiste.nt path exists in ZK. Create the path if not exist.
     */
    public static void makeSurePersistentPathExists(final ZkClient client, final String path) throws Exception {
        if (!client.exists(path)) {
            try {
                client.createPersistent(path, true);
            }
            catch (final ZkNodeExistsException e) {
            }
            catch (final Exception e) {
                throw e;
            }

        }
    }

    /**
     * create the parent path
     */
    public static void createParentPath(final ZkClient client, final String path) throws Exception {
        final String parentDir = path.substring(0, path.lastIndexOf('/'));
        if (parentDir.length() != 0) {
            client.createPersistent(parentDir, true);
        }
    }

    /**
     * Create an ephemeral node with the given path and data. Create parents if
     * necessary.
     */
    public static void createEphemeralPath(final ZkClient client, final String path, final String data)
            throws Exception {
        try {
            client.createEphemeral(path, data);
        }
        catch (final ZkNoNodeException e) {
            createParentPath(client, path);
            client.createEphemeral(path, data);
        }
    }
    /**
     * Create an ephemeral node with the given path and data. Throw
     * NodeExistException if node already exists.
     */
    public static void createEphemeralPathExpectConflict(final ZkClient client, final String path, final String data)
            throws Exception {
        try {
            createEphemeralPath(client, path, data);
        }
        catch (final ZkNodeExistsException e) {

            // this canZkConfig happen when there is connection loss; make sure
            // the data
            // is what we intend to write
            String storedData = null;
            try {
                storedData = readData(client, path);
            }
            catch (final ZkNoNodeException e1) {
                // the node disappeared; treat as if node existed and let caller
                // handles this
            }
            catch (final Exception e2) {
                throw e2;
            }
            if (storedData == null || !storedData.equals(data)) {
                throw e;
            }
            else {
                // otherwise, the creation succeeded, return normally
                logger.info(path + " exists with value " + data + " during connection loss; this is ok");
            }
        }
        catch (final Exception e) {
            throw e;
        }

    }


    /**
     * Update the value of a persistent node with the given path and data.
     * create parrent directory if necessary. Never throw NodeExistException.
     */
    public static void updatePersistentPath(final ZkClient client, final String path, final String data)
            throws Exception {
        try {
            client.writeData(path, data);
        }
        catch (final ZkNoNodeException e) {
            createParentPath(client, path);
            client.createPersistent(path, data);
        }
        catch (final Exception e) {
            throw e;
        }
    }
    public static String readData(final ZkClient client, final String path) {
        return client.readData(path);
    }

    public static String readDataMaybeNull(final ZkClient client, final String path) {
        return client.readData(path, true);
    }
    /**
     * Update the value of a persistent node with the given path and data.
     * create parrent directory if necessary. Never throw NodeExistException.
     */
    public static void updateEphemeralPath(final ZkClient client, final String path, final String data)
            throws Exception {
        try {
            client.writeData(path, data);
        }
        catch (final ZkNoNodeException e) {

            createParentPath(client, path);
            client.createEphemeral(path, data);

        }
        catch (final Exception e) {
            throw e;
        }
    }

    public static void deletePath(final ZkClient client, final String path) throws Exception {
        try {
            client.delete(path);
        }
        catch (final ZkNoNodeException e) {
            logger.info(path + " deleted during connection loss; this is ok");
        }
        catch (final Exception e) {
            throw e;
        }
    }

    public static void deletePathRecursive(final ZkClient client, final String path) throws Exception {
        try {
            client.deleteRecursive(path);
        }
        catch (final ZkNoNodeException e) {
            logger.info(path + " deleted during connection loss; this is ok");

        }
        catch (final Exception e) {
            throw e;
        }
    }

    public static List<String> getChildren(final ZkClient client, final String path) {
        return client.getChildren(path);
    }

    public static List<String> getChildrenMaybeNull(final ZkClient client, final String path) {
        try {
            return client.getChildren(path);
        }
        catch (final ZkNoNodeException e) {
            return null;
        }
    }

    /**
     * Check if the given path exists
     */
    public static boolean pathExists(final ZkClient client, final String path) {
        return client.exists(path);
    }

    public static String getLastPart(final String path) {
        if (path == null) {
            return null;
        }
        final int index = path.lastIndexOf('/');
        if (index >= 0) {
            return path.substring(index + 1);
        }
        else {
            return null;
        }
    }

    public static class StringSerializer implements ZkSerializer {

        @Override
        public Object deserialize(final byte[] bytes) throws ZkMarshallingError {
            try {
                return new String(bytes, "utf-8");
            }
            catch (final UnsupportedEncodingException e) {
                throw new ZkMarshallingError(e);
            }
        }

        @Override
        public byte[] serialize(final Object data) throws ZkMarshallingError {
            try {
                return ((String) data).getBytes("utf-8");
            }
            catch (final UnsupportedEncodingException e) {
                throw new ZkMarshallingError(e);
            }
        }

    }

}
