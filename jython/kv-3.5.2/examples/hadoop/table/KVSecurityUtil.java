/*-
 *
 *  This file is part of Oracle NoSQL Database
 *  Copyright (C) 2011, 2015 Oracle and/or its affiliates.  All rights reserved.
 *
 *  Oracle NoSQL Database is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, version 3.
 *
 *  Oracle NoSQL Database is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public
 *  License in the LICENSE file along with Oracle NoSQL Database.  If not,
 *  see <http://www.gnu.org/licenses/>.
 *
 *  An active Oracle commercial licensing agreement for this product
 *  supercedes this license.
 *
 *  For more information please contact:
 *
 *  Vice President Legal, Development
 *  Oracle America, Inc.
 *  5OP-10
 *  500 Oracle Parkway
 *  Redwood Shores, CA 94065
 *
 *  or
 *
 *  berkeleydb-info_us@oracle.com
 *
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  EOF
 *
 */

package hadoop.table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

import oracle.kv.KVSecurityConstants;
import oracle.kv.PasswordCredentials;
import oracle.kv.impl.security.PasswordStore;
import oracle.kv.impl.security.filestore.FileStoreManager;
import oracle.kv.impl.security.wallet.WalletManager;

/**
 * Utility class that provides convenience methods related to the Oracle
 * NoSQL Database security model. The methods of this class can be used
 * by an application to create and process the various security artifacts
 * required to interact with a given secure KVStore. To see how this
 * utility might be used, refer to the example class provided in the
 * current package.
 */
public final class KVSecurityUtil {

    private static final Class<?> THIS_CLASS = KVSecurityUtil.class;
    private static final String THIS_CLASS_NAME = THIS_CLASS.getName();
    private static final Logger LOGGER = Logger.getLogger(THIS_CLASS_NAME);

    private static final String FILE_SEP =
                                    System.getProperty("file.separator");
    private static final String CR = "\n";

    private static String serverLoginFlnm;
    private static PasswordCredentials passwordCredentials;
    private static String clientTrustFlnm;

    /*
     * To satisfy the warnings mechanism, need a private constructor because
     * this is a utility class (final class, all static methods).
     */
    private KVSecurityUtil() {
    }

    /**
     * Creates the artifacts required to connect to and interact with
     * a secure store; specifically, a login file, a trust file, and
     * <code>PasswordCredentials</code>. To create the login and trust
     * files in the local file system, this method uses the given file
     * names to retrieve the contents of each respective file as a
     * resource from the classpath and then writes the contents to the
     * local file system, in a directory owned by the application. This
     * is done so that this information is available to the frontend,
     * client-side of the application when attempting to connect to a
     * secure store.
     * <p>
     * This method also caches the names of the given files (as well
     * as the <code>PasswordCredentials</code>) so that this information
     * can be retrieved and sent to another party, such as the backend,
     * server-side of an application; for example, the InputFormat and
     * InputSplits of a MapReduce job.
     */
     protected static void createLocalKVSecurity(final String clientLoginFile,
                                                 final String serverLoginFile)
                                                     throws IOException {
        if (clientLoginFile == null) {
            return;
        }

        if (serverLoginFile == null) {
            KVSecurityUtil.serverLoginFlnm = clientLoginFile;
        } else {
            KVSecurityUtil.serverLoginFlnm = serverLoginFile;
        }

        InputStream loginStream = null;

        LOGGER.info("clientLoginFile = " + clientLoginFile);
        LOGGER.info("serverLoginFile = " + serverLoginFile);

        final ClassLoader cl = THIS_CLASS.getClassLoader();
        if (cl != null) {
            loginStream = cl.getResourceAsStream(clientLoginFile);
        } else {
            loginStream =
                ClassLoader.getSystemResourceAsStream(clientLoginFile);
        }

        /* Create tmp location for transferring resources from classpath.*/
        final String userSecurityDir = System.getProperty("user.dir") +
                FILE_SEP + "KVSTORE_CLIENT_SECURITY_DIR_" +
                THIS_CLASS.getSimpleName();
        final File userSecurityDirFd = new File(userSecurityDir);
        if (!userSecurityDirFd.exists()) {
            if (!userSecurityDirFd.mkdirs()) {
                throw new IOException("failed to create " + userSecurityDir);
            }
        }

        /*
         * Retrieve the contents of the login file from the classpath
         * resources and write the contents to the client's local file
         * system. But exclude the properties related to the credentials;
         * that is, exclude 'oracle.kv.auth.*" properties.
         */
        final Properties loginProps = new Properties();
        if (loginStream != null) {
            loginProps.load(loginStream);
        }

        /* Retrieve the name of the PUBLIC client trust file. */
        KVSecurityUtil.clientTrustFlnm = (String) loginProps.get(
            KVSecurityConstants.SSL_TRUSTSTORE_FILE_PROPERTY);

        LOGGER.info("clientTrustFlnm = " + clientTrustFlnm);

        /* Retrieve but exclude 'oracle.kv.auth.*" properties. */
        final String userName = (String) loginProps.remove(
            KVSecurityConstants.AUTH_USERNAME_PROPERTY);

        /* Determine if wallet or password file and get file/dir name. */
        boolean usingWallet = true;
        String credentialsLoc = (String) loginProps.remove(
            KVSecurityConstants.AUTH_WALLET_PROPERTY);
        if (credentialsLoc == null) {
            usingWallet = false;
            credentialsLoc = (String) loginProps.remove(
                KVSecurityConstants.AUTH_PWDFILE_PROPERTY);
        }

        /* Write the non-auth login properties to the local file system. */
        final File loginFd =
            new File(userSecurityDir + FILE_SEP + clientLoginFile);
        final FileOutputStream loginFos = new FileOutputStream(loginFd);
        loginProps.store(loginFos, null);
        loginFos.close();

        /*
         * Retrieve the contents of the client credentials (password file
         * or wallet directory) from the classpath resources and then use
         * the username (retrieved above from the login file) and the
         * password retrieved here from the password file or wallet to
         * construct the PasswordCredentials.
         */
        if (usingWallet) { /* Retrieve the password from the wallet. */

            /* Retrieve WALLET DIRECTORY from classpath as a resource. */
            final String walletDir =
                userSecurityDir + FILE_SEP + credentialsLoc;
            LOGGER.info("USING WALLET DIRECTORY [" + walletDir + "]");

            InputStream walletDirStream = null;
            if (cl != null) {
                walletDirStream = cl.getResourceAsStream(credentialsLoc);
            } else {
                walletDirStream =
                    ClassLoader.getSystemResourceAsStream(credentialsLoc);
            }

            final InputStreamReader walletDirIsr =
                new InputStreamReader(walletDirStream);
            final BufferedReader walletDirBr =
                new BufferedReader(walletDirIsr);

            /* For writing TEMPORARY credentials to local filesystem. */
            final File walletDirFd = new File(walletDir);
            walletDirFd.mkdir();

            String nextWalletFile = walletDirBr.readLine();
            LOGGER.info("   next wallet file [" + nextWalletFile + "]");

            while (nextWalletFile != null) {
                final String nextWalletPath =
                    credentialsLoc + FILE_SEP + nextWalletFile;
                LOGGER.info("   next wallet path [" + nextWalletPath + "]");

                InputStream walletFileStream = null;
                if (cl != null) {
                    walletFileStream = cl.getResourceAsStream(nextWalletPath);
                } else {
                    walletFileStream =
                        ClassLoader.getSystemResourceAsStream(nextWalletPath);
                }

                /* Write wallet file bytes to the local filesystem. */
                final File walletFileFd =
                    new File(walletDir + FILE_SEP + nextWalletFile);
                final FileOutputStream walletFileFos =
                    new FileOutputStream(walletFileFd);

                int nextByte = walletFileStream.read();
                while (nextByte != -1) {
                    walletFileFos.write(nextByte);
                    nextByte = walletFileStream.read();
                }
                walletFileFos.close();
                nextWalletFile = walletDirBr.readLine();
                LOGGER.info("   next wallet file [" + nextWalletFile + "]");
            }

            /*
             * Retrieve the password from the local wallet directory
             * just created and populated, and use it and the userName
             * retrieved above to create PasswordCredentials.
             */
            final WalletManager storeMgr = new WalletManager();
            final PasswordStore fileStore =
               storeMgr.getStoreHandle(walletDirFd);
            fileStore.open(null);
            final Collection<String> secretAliases =
                                         fileStore.getSecretAliases();
            final Iterator<String> aliasItr = secretAliases.iterator();
            final char[] userPassword = (aliasItr.hasNext() ?
                            fileStore.getSecret(aliasItr.next()) : null);

            passwordCredentials =
                new PasswordCredentials(userName, userPassword);

            /* Remove the wallet directory created and populated above. */
            removeDir(walletDirFd);

        } else { /* Retrieve the password from the password file. */

            final String passwordFile =
                             userSecurityDir + FILE_SEP + credentialsLoc;
            LOGGER.info("USING PASSWORD FILE [" + passwordFile + "]");

            /* Retrieve PASSWORD FILE from the classpath as a resource. */
            InputStream passwordFileStream = null;
            if (cl != null) {
                passwordFileStream = cl.getResourceAsStream(credentialsLoc);
            } else {
                passwordFileStream =
                    ClassLoader.getSystemResourceAsStream(credentialsLoc);
            }

            /* For reading password file contents from the classpath. */
            final InputStreamReader passwordFileIsr =
                new InputStreamReader(passwordFileStream);
            final BufferedReader passwordFileBr =
                new BufferedReader(passwordFileIsr);

            /* For writing TEMPORARY password file to local filesystem. */
            final File passwordFileFd = new File(passwordFile);
            final FileOutputStream passwordFileFos =
                new FileOutputStream(passwordFileFd);

            String nextLine = passwordFileBr.readLine();
            while (nextLine != null) {
                passwordFileFos.write(nextLine.getBytes());
                passwordFileFos.write(CR.getBytes());
                nextLine = passwordFileBr.readLine();
            }
            passwordFileFos.close();

            /*
             * Retrieve the password from the local password file just
             * written, and use it and the userName retrieved above
             * to create PasswordCredentials.
             */
            final FileStoreManager storeMgr = new FileStoreManager();
            final PasswordStore fileStore =
               storeMgr.getStoreHandle(passwordFileFd);
            fileStore.open(null);
            final Collection<String> secretAliases =
                                         fileStore.getSecretAliases();
            final Iterator<String> aliasItr = secretAliases.iterator();
            final char[] userPassword = (aliasItr.hasNext() ?
                fileStore.getSecret(aliasItr.next()) : null);

            passwordCredentials =
                new PasswordCredentials(userName, userPassword);

            /* Remove the local password file written above. */
            passwordFileFd.delete();
        }

        /*
         * Retrieve the contents of the client trust file from the
         * classpath resources and write the contents to the client's
         * local file system.
         */
        InputStream clientTrustStream = null;
        if (cl != null) {
            clientTrustStream = cl.getResourceAsStream(clientTrustFlnm);
        } else {
            clientTrustStream =
                ClassLoader.getSystemResourceAsStream(clientTrustFlnm);
        }

        /* For writing client trust file bytes to the local filesystem. */
        final File clientTrustFd =
            new File(userSecurityDir + FILE_SEP + clientTrustFlnm);
        final FileOutputStream clientTrustFlnmFos =
            new FileOutputStream(clientTrustFd);

        int nextByte = clientTrustStream.read();
        while (nextByte != -1) {
            clientTrustFlnmFos.write(nextByte);
            nextByte = clientTrustStream.read();
        }
        clientTrustFlnmFos.close();

        LOGGER.info("userName     = " + passwordCredentials.getUsername());
        LOGGER.info("userPassword = " +
                 String.valueOf(passwordCredentials.getPassword()));
    }

    protected static String getServerLoginFlnm() {
        return serverLoginFlnm;
    }

    protected static oracle.kv.PasswordCredentials getPasswordCredentials() {
        return passwordCredentials;
    }

    protected static String getClientTrustFlnm() {
        return clientTrustFlnm;
    }

    protected static boolean removeDir(File fd) throws IOException {
        boolean removed = false;

        if (fd == null || !fd.exists())  {
            return removed;
        }

        /* FILE: remove it and return. */
        if (fd.isFile()) {
            removed = fd.delete();
            if (removed) {
                LOGGER.info("removed file [" + fd.getPath() + "]");
            } else {
                LOGGER.info("FAILED to remove file [" + fd.getPath() + "]");
            }
            return removed;
        }

        /* DIRECTORY: get its contents. */
        final File[] fdArray = fd.listFiles();

        /* EMPTY: remove directory and return. */
        if (fdArray == null) {
            removed = fd.delete();
            if (removed) {
                LOGGER.info("removed directory [" + fd.getPath() + "]");
            } else {
                LOGGER.info(
                    "FAILED to remove directory [" + fd.getPath() + "]");
            }
            return removed;
        }

        /* NON-EMPTY: recursively remove all files in the directory */
        for (int i = 0; i < fdArray.length; i++) {
            removeDir(fdArray[i]);
        }

        /* EMPTY: remove directory and return */
        removed = fd.delete();
        if (removed) {
            LOGGER.info("removed directory [" + fd.getPath() + "]");
        } else {
            LOGGER.info("FAILED to remove directory [" + fd.getPath() + "]");
        }
        return removed;
    }
}
