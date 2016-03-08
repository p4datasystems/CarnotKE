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

import static oracle.kv.impl.security.PasswordManager.FILE_STORE_MANAGER_CLASS;
import static oracle.kv.impl.security.PasswordManager.WALLET_MANAGER_CLASS;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import oracle.kv.KVSecurityConstants;
import oracle.kv.impl.security.PasswordManager;
import oracle.kv.impl.security.PasswordStore;
import oracle.kv.impl.security.util.ConsolePasswordReader;
import oracle.kv.impl.security.util.PasswordReader;
import oracle.kv.impl.security.util.SecurityUtils;
import oracle.kv.impl.util.ConfigUtils;

/**
 * Standalone program that creates (or deletes) a password file or Oracle
 * Wallet for use when running the associated example MapReduce job
 * with a secure KVStore. This optional program is provided as a convenience,
 * and shows examples of how the necessary Oracle NoSQL Database security
 * artifacts can be created programmatically. It can be executed from a
 * script or the command line, to produce the type of security credentials
 * a client will need to interact with a given secure KVStore.
 */
public final class KVSecurityCreation {

    private static final String CREDENTIALS_PATH =
                             System.getProperty("java.io.tmpdir");
    private static final String FILE_SEP =
                             System.getProperty("file.separator");

    private static final String LOGIN_COMMENT =
        "Security property settings for communication with KVStore servers";
    private static final String PREFERRED_PROTOCOLS = "TLSv1.2,TLSv1.1,TLSv1";

    private static boolean deleteCredentials = false;

    private String credentialsManagerClass = FILE_STORE_MANAGER_CLASS;
    private String alias;
    private String credentialsNameArg = "PWD_FILE_OR_WALLET_DIR";

    public static void main(String[] args) {
        try {
            final KVSecurityCreation util = new KVSecurityCreation(args);
            if (deleteCredentials) {
                util.doDeleteCredentials();
            } else {
                util.doCreateCredentials();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private KVSecurityCreation(String[] argv) {

        final int nArgs = argv.length;
        int argc = 0;

        while (argc < nArgs) {
            final String thisArg = argv[argc++];

            if (("-pwdfile").equals(thisArg)) {
                if (argc < nArgs) {
                    credentialsNameArg = argv[argc++];
                    credentialsManagerClass = FILE_STORE_MANAGER_CLASS;
                } else {
                    usage("-pwdfile requires a path and filename");
                }
            } else if (("-wallet").equals(thisArg)) {
                if (argc < nArgs) {
                    credentialsNameArg = argv[argc++];
                    credentialsManagerClass = WALLET_MANAGER_CLASS;
                } else {
                    usage("-wallet requires a directory path");
                }
            } else if (("-alias").equals(thisArg)) {
                if (argc < nArgs) {
                    alias = argv[argc++];
                } else {
                    usage("-alias requires a valid alias name");
                }
            } else if (("-set").equals(thisArg)) {
                deleteCredentials = false;
            } else if (("-delete").equals(thisArg)) {
                deleteCredentials = true;
            } else {
                usage("Unknown argument: " + thisArg);
            }
        }
    }

    private void usage(String message) {
        System.out.println("\n" + message + "\n");
        System.out.println("usage: " + getClass().getName());
        System.out.println("\t[-pwdfile | -wallet] " +
                           "[<password filename> | <wallet directory name>] " +
                           "[-set | -delete] -alias <alias>");
        System.exit(1);
    }

    private void doDeleteCredentials() throws Exception {

        /* Remove the Wallet directory or the password file. */
        KVSecurityUtil.removeDir(
            new File(CREDENTIALS_PATH + FILE_SEP + credentialsNameArg));

        /* Remove the login file based on wallet or password file config. */
        if (WALLET_MANAGER_CLASS.equals(credentialsManagerClass)) {
            KVSecurityUtil.removeDir(
                new File(CREDENTIALS_PATH + FILE_SEP +
                         alias + "-client-wallet.login"));
        } else {
            KVSecurityUtil.removeDir(
                new File(CREDENTIALS_PATH + FILE_SEP +
                         alias + "-client-pwdfile.login"));
        }

        /*
         * Note: because the same server side login properties file is used
         *       by BOTH the wallet and the password file configurations,
         *       do NOT remove the server side login properties file.
         */
    }

    private void doCreateCredentials() throws Exception {

        /* Remove existing files from previous run. */
        doDeleteCredentials();

        /* Create the client side login properties file. */
        createLoginPropertiesFile(alias, credentialsNameArg);

        /* Create the server side login properties file. */
        createLoginPropertiesFile(alias);

        /* Create the credentials Store. */
        final PasswordManager credentialsMgr =
                  PasswordManager.load(credentialsManagerClass);
        final File credentialsFd = new File(CREDENTIALS_PATH + FILE_SEP +
                                            credentialsNameArg);
        final PasswordStore credentialsStore =
                                credentialsMgr.getStoreHandle(credentialsFd);
        if (credentialsStore.exists()) {
            throw new IOException("Creation failure: credentials store " +
                                  "already exists [" +
                                  credentialsFd.getPath() + "]");
        }

        /* Password-protected pwdFiles/wallets are not currently supported. */
        final char[] pwd = null;

        /* Create pwd file or wallet; depending on credentialsManagerClass. */
        credentialsStore.create(pwd);
        System.out.println("created credentials store [" +
                           credentialsFd.getPath() + "]");

        /* Store the password (the secret) in the created credentials store. */
        final PasswordReader pwdReader = new ConsolePasswordReader();
        char[] secret;
        char[] verifySecret;
        secret = pwdReader.readPassword("Enter the secret value to store: ");
        verifySecret = pwdReader.readPassword(
                           "Re-enter the secret value for verification: ");
        if (SecurityUtils.passwordsMatch(secret, verifySecret)) {
            if (credentialsStore.setSecret(alias, secret)) {
                System.out.println("Secret updated");
            } else {
                System.out.println("Secret created");
            }
            credentialsStore.save();
        } else {
            System.out.println("The passwords do not match");
        }
    }

    private void createLoginPropertiesFile(final String username)
                     throws IOException {
        createLoginPropertiesFile(username, null);
    }

    private void createLoginPropertiesFile(final String username,
                                           final String credentialsName)
                     throws IOException {

        String loginFlnm = username + "-server.login";
        if (credentialsName != null) {
            if (WALLET_MANAGER_CLASS.equals(credentialsManagerClass)) {
                loginFlnm = username + "-client-wallet.login";
            } else {
                loginFlnm = username + "-client-pwdfile.login";
            }
        }

        final File loginFd = new File(CREDENTIALS_PATH + FILE_SEP + loginFlnm);

        final Properties loginProps = new Properties();
        loginProps.put(
            KVSecurityConstants.SSL_TRUSTSTORE_FILE_PROPERTY, "client.trust");
        loginProps.put(
            KVSecurityConstants.TRANSPORT_PROPERTY,
            KVSecurityConstants.SSL_TRANSPORT_NAME);
        loginProps.put(
            KVSecurityConstants.SSL_PROTOCOLS_PROPERTY, PREFERRED_PROTOCOLS);
        loginProps.put(
            KVSecurityConstants.SSL_HOSTNAME_VERIFIER_PROPERTY,
            "dnmatch(CN=NoSQL)");

        if (credentialsName != null) {
            loginProps.put(
                KVSecurityConstants.AUTH_USERNAME_PROPERTY, username);
            if (WALLET_MANAGER_CLASS.equals(credentialsManagerClass)) {
                loginProps.put(
                   KVSecurityConstants.AUTH_WALLET_PROPERTY, credentialsName);
            } else {
                loginProps.put(
                   KVSecurityConstants.AUTH_PWDFILE_PROPERTY, credentialsName);
            }
        }
        ConfigUtils.storeProperties(loginProps, LOGIN_COMMENT, loginFd);
        System.out.println("created login properties file [" +
                           loginFd.getPath() + "]");
    }
}
