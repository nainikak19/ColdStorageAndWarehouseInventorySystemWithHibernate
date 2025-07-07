package com.hibernate.warehouse.system;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

public class ProductService {

    public void viewInventory() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Product> products = session.createQuery("from Product", Product.class).list();
            for (Product p : products) {
                System.out.println("ID: " + p.getItem_id() + ", Name: " + p.getItem_name() +
                        ", Qty: " + p.getQuantity() + ", Expiry: " + p.getExpiry_date());
            }
        }
    }

    public void addProduct(Scanner sc) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Product p = new Product();

            System.out.print("Item Name: ");
            p.setItem_name(sc.nextLine());

            System.out.print("Quantity: ");
            p.setQuantity(Integer.parseInt(sc.nextLine()));

            System.out.print("Expiry Date (YYYY-MM-DD): ");
            String expDateStr = sc.nextLine().trim();
            Date expiry = Date.valueOf(expDateStr);
            p.setExpiry_date(expiry);

            session.save(p);
            tx.commit();
            System.out.println("Product added.");
        }
    }

    public void batchUpload(Scanner sc) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            System.out.print("How many products to upload? ");
            int count = Integer.parseInt(sc.nextLine());

            for (int i = 0; i < count; i++) {
                Product p = new Product();
                System.out.println("Product " + (i + 1));

                System.out.print("Item Name: ");
                p.setItem_name(sc.nextLine());

                System.out.print("Quantity: ");
                p.setQuantity(Integer.parseInt(sc.nextLine()));

                System.out.print("Expiry Date (YYYY-MM-DD): ");
                String expDateStr = sc.nextLine().trim();
                Date expiry = Date.valueOf(expDateStr);
                p.setExpiry_date(expiry);

                session.save(p);
            }

            tx.commit();
            System.out.println("Batch upload completed.");
        }
    }

    public void removeExpired() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Fetch expired products before deletion
            List<Product> expiredProducts = session.createQuery(
                    "from Product where expiry_date < :today", Product.class)
                    .setParameter("today", Date.valueOf(LocalDate.now()))
                    .list();

            for (Product p : expiredProducts) {
                System.out.println("Removed → ID: " + p.getItem_id() + ", Name: " + p.getItem_name()
                        + ", Expiry: " + p.getExpiry_date());
            }

            // Delete expired products
            Query<?> q = session.createQuery("delete from Product where expiry_date < :today");
            q.setParameter("today", Date.valueOf(LocalDate.now()));
            int removed = q.executeUpdate();

            tx.commit();
            System.out.println("Total removed: " + removed + " expired item(s).");
        }
    }

    public void showExpiringSoon() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Product> list = session.createQuery(
                    "from Product where expiry_date <= :next7", Product.class)
                    .setParameter("next7", Date.valueOf(LocalDate.now().plusDays(7)))
                    .list();

            if (list.isEmpty()) {
                System.out.println("No items expiring or expired within 7 days.");
            } else {
                for (Product p : list) {
                    long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), p.getExpiry_date().toLocalDate());
                    String status = daysRemaining >= 0 ? daysRemaining + " day(s) remaining" : (-daysRemaining) + " day(s) expired";
                    System.out.println("ID: " + p.getItem_id() + ", Name: " + p.getItem_name()
                            + ", Expiry: " + p.getExpiry_date() + " → " + status);
                }
            }
        }
    }
}
