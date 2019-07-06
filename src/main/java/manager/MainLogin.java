/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author toannd4
 */
public class MainLogin {

    private static String PATH = null;

    private static boolean storeUser(String username, String encryptedPassword) {
        try (FileWriter fw = new FileWriter(PATH, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {

            out.println(username + "\t" + encryptedPassword);
            System.out.println("User registration is successful!");
            return true;
        } catch (IOException ex) {
            System.out.println("Error in processing! Please try again!");
            return false;
        }
    }

    private static boolean checkIfUserExist(String username) {
        try (FileReader reader = new FileReader(PATH);
                BufferedReader br = new BufferedReader(reader)) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] elem = line.split("\t");
                if (elem.length == 2) {
                    if (elem[0].equals(username)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (IOException ex) {
            return false;
        }
    }

    private static String getEncryptedPassword(String username) {
        try (FileReader reader = new FileReader(PATH);
                BufferedReader br = new BufferedReader(reader)) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] elem = line.split("\t");
                if (elem.length == 2) {
                    if (elem[0].equals(username)) {
                        return elem[1];
                    }
                }
            }
            System.out.println("Not found user!");
            return null;
        } catch (IOException ex) {
            System.out.println("Error in processing! Please try again!");
            return null;
        }
    }

    private static boolean changePassword(String username, String newEncryptedPassword) {
        boolean isSuccess = false;
        List<String> lines = new ArrayList<String>();
        try (FileReader reader = new FileReader(PATH);
                BufferedReader br = new BufferedReader(reader)) {

            String line;
            while ((line = br.readLine()) != null) {
                String newLine;
                String[] elem = line.split("\t");
                if (elem.length == 2) {
                    if (elem[0].equals(username)) {
                        newLine = elem[0] + "\t" + newEncryptedPassword;
                        lines.add(newLine);
                        isSuccess = true;
                        continue;
                    }
                }
                newLine = line;
                lines.add(line);
            }
            if (isSuccess) {
                try (FileWriter fw = new FileWriter(PATH);
                        BufferedWriter bw = new BufferedWriter(fw);
                        PrintWriter out = new PrintWriter(bw)) {
                    for (String s : lines) {
                        out.println(s);
                    }
                }
                System.out.println("Change password is successful!");
            } else {
                System.out.println("User is not exist in system. Please sign up!");
            }
            return isSuccess;
        } catch (IOException ex) {
            System.out.println("Error in processing! Please try again!");
            return false;
        }
    }

    public static void main(String[] args) {
        File resourcesDirectory;
        try {
            resourcesDirectory = new File(new File(MainLogin.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getParent() + "/storages.txt");
        } catch (URISyntaxException ex1) {
            try {
                resourcesDirectory = new File("src/main/resources/storages.txt");
            } catch (Exception ex2) {
                resourcesDirectory = new File(System.getProperty("user.dir") + "/storages.txt");
            }
        }
        System.out.println("Storage file is located in " + resourcesDirectory.getAbsolutePath());
        PATH = resourcesDirectory.getAbsolutePath();
        boolean isActive = true;
        Scanner scanner = new Scanner(System.in);
        do {
            try {
                System.out.println("");
                System.out.println("==============Menu=============");
                System.out.println("Please choose number option 1,2,3,4,5,6: ");
                System.out.println("1. New user");
                System.out.println("2. Validate password");
                System.out.println("3. Login");
                System.out.println("4. Change password");
                System.out.println("5. Logout");
                System.out.println("6. Exit");
                System.out.println("");
                int option = scanner.nextInt();
                switch (option) {
                    case 1:
                        System.out.println("Create new account");
                        PasswordManager.getInstance().clear();
                        System.out.println("Please enter username: ");
                        String username = scanner.next();
                        System.out.println("Please enter password: ");
                        String password = scanner.next();
                        System.out.println("Please reenter password: ");
                        String confirmPassword = scanner.next();
                        if (password.equals(confirmPassword)) {
                            if (!checkIfUserExist(username)) {
                                while (!PasswordManager.getInstance().validatePassword(password)) {
                                    System.out.println("Password is invalid. Must meet the criteria: ");
                                    System.out.println("- The password must not contain any whitespace");
                                    System.out.println("- The password must be at least 6 characters long");
                                    System.out.println("- The password must contain at least one uppercase and at least one lowercase letter.");
                                    System.out.println("- The password must have at least one digit and symbol");
                                    System.out.println("=====================================================");
                                    System.out.println("Please enter password: ");
                                    password = scanner.next();
                                    System.out.println("Please reenter password: ");
                                    confirmPassword = scanner.next();
                                    while (!password.equals(confirmPassword)) {
                                        System.out.println("Password and confirmed password are not match!");
                                        System.out.println("=====================================================");
                                        System.out.println("Please enter password: ");
                                        password = scanner.next();
                                        System.out.println("Please reenter password: ");
                                        confirmPassword = scanner.next();
                                    }
                                }
                                PasswordManager.getInstance().setUsername(username);
                                PasswordManager.getInstance().setNewPassword(password);
                                storeUser(PasswordManager.getInstance().getUsername(), PasswordManager.getInstance().getPassword());
                            } else {
                                System.out.println("Username has already signup. Please pickup another username!");
                                System.out.println("=====================================================");
                            }
                        } else {
                            System.out.println("Password and confirmed password are not match!");
                            System.out.println("=====================================================");
                            continue;
                        }
                        break;
                    case 2:
                        System.out.println("Validate password");
                        PasswordManager.getInstance().clear();
                        System.out.println("Please enter password to test if it is validate: ");
                        password = scanner.next();
                        if (PasswordManager.getInstance().validatePassword(password)) {
                            System.out.println("Your password is ok! You can use this password for registration");
                        } else {
                            System.out.println("Password is invalid. Must meet the criteria: ");
                            System.out.println("- The password must not contain any whitespace");
                            System.out.println("- The password must be at least 6 characters long");
                            System.out.println("- The password must contain at least one uppercase and at least one lowercase letter.");
                            System.out.println("- The password must have at least one digit and symbol");
                            System.out.println("=====================================================");
                        }
                        break;
                    case 3:
                        System.out.println("Login to the system");
                        PasswordManager.getInstance().clear();
                        System.out.println("Please enter username: ");
                        username = scanner.next();
                        System.out.println("Please enter password: ");
                        password = scanner.next();

                        String encryptedPassword = getEncryptedPassword(username);
                        if (encryptedPassword != null && !encryptedPassword.isEmpty()) {
                            PasswordManager.getInstance().setUsername(username);
                            PasswordManager.getInstance().setPassword(encryptedPassword);
                            if (PasswordManager.getInstance().verifyPassword(password)) {
                                System.out.println("Login successfully");
                                System.out.println("=====================================================");
                            } else {
                                System.out.println("Wrong password! PLease try again");
                                System.out.println("=====================================================");
                            }
                        } else {
                            System.out.println("User is not exists!");
                            System.out.println("=====================================================");
                        }
                        break;
                    case 4:
                        if (PasswordManager.getInstance().getUsername() != null
                                && !PasswordManager.getInstance().getUsername().isEmpty()
                                && PasswordManager.getInstance().getPassword() != null
                                && !PasswordManager.getInstance().getPassword().isEmpty()) {
                            System.out.println("Please enter new password: ");
                            String newPassword = scanner.next();
                            System.out.println("Please reenter new password: ");
                            String confirmNewPassword = scanner.next();
                            if (newPassword.equals(confirmNewPassword)) {
                                PasswordManager.getInstance().setNewPassword(newPassword);
                                changePassword(PasswordManager.getInstance().getUsername(), PasswordManager.getInstance().getPassword());
                            } else {
                                System.out.println("New password and confirm new password are not match");
                                System.out.println("=====================================================");
                            }
                        } else {
                            System.out.println("Please login before change password");
                            System.out.println("=====================================================");
                        }
                        break;
                    case 5:
                        PasswordManager.getInstance().clear();
                        System.out.println("Logout succesfully!");
                        System.out.println("=====================================================");
                        break;
                    case 6:
                        System.out.println("Exit the search option");
                        System.out.println("=====================================================");
                        isActive = false;
                        break;
                    default:
                        System.out.println("Your selection was wrong. Try one more time!");
                        System.out.println("=====================================================");
                        break;

                }
            } catch (Exception ex) {
                scanner = new Scanner(System.in);
                System.out.println("Wrong in select! Try again!");
                System.out.println("=====================================================");
            }
        } while (isActive);
    }

}
