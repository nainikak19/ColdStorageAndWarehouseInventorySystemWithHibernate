package com.hibernate.warehouse.system;

import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ProductService service = new ProductService();

        System.out.print("Enter username: ");
        //String user = sc.nextLine(); // Optional if you want to use it later

        System.out.print("Enter role (vendor/staff): ");
        String role = sc.nextLine().toLowerCase();
        boolean isStaff = role.equals("staff");

        while (true) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. View Inventory");
            System.out.println("2. Add Product");
            System.out.println("3. Batch Upload");
            System.out.println("4. Remove Expired");
            System.out.println("5. Expiring Soon");
            System.out.println("6. Exit");
            System.out.print("Choose: ");

            int ch = 0;
            try {
                ch = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (ch) {
                case 1:
                    service.viewInventory();
                    break;
                case 2:
                    if (isStaff) service.addProduct(sc);
                    else System.out.println("Access denied for vendor.");
                    break;
                case 3:
                    if (isStaff) service.batchUpload(sc);
                    else System.out.println("Access denied for vendor.");
                    break;
                case 4:
                    if (isStaff) service.removeExpired();
                    else System.out.println("Access denied for vendor.");
                    break;
                case 5:
                    service.showExpiringSoon();
                    break;
                case 6:
                    HibernateUtil.shutdown();
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please choose 1 to 6.");
            }
        }
    }
}