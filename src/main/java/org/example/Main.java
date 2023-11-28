package org.example;

import org.example.database.DbHandler;
import org.example.database.SchemeDB;
import org.example.model.Employee;
import org.example.model.Order;
import org.example.model.Product;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static Connection connection = DbHandler.getConnection();

    public static void main(String[] args) {

        try {
            JSONArray products = getProductsFromURL();
            Product product = new Product();

            for (int i = 0; i < products.length(); i++) {
                JSONObject productJSON = products.getJSONObject(i);
                product.setId(productJSON.getInt("id"));
                product.setTitle(productJSON.getString("title"));
                product.setDescription(productJSON.getString("description"));
                product.setStock(productJSON.getInt("stock"));
                product.setPrice(productJSON.getDouble("price"));

                insertIntoProductos(product);
            }

        } catch (IOException e) {
            System.out.println("Error en la coneccion I/O");
        }

        Employee empleado1 = new Employee("Juan", "Taveira", "juan@juan.com");
        Employee empleado2 = new Employee("Leo", "Messi", "messi@messi.com");

        insertIntoEmpleados(empleado1);
        insertIntoEmpleados(empleado2);

        Order pedido1 = new Order(3);
        Order pedido2 = new Order(5);

        setDescriptionAndTotalAmount(pedido1);
        setDescriptionAndTotalAmount(pedido2);

        insertIntoPedidos(pedido1);
        insertIntoPedidos(pedido2);

        System.out.println("\n---------------------------- Empleados ----------------------------\n");
        showAll(Employee.class);
        System.out.println("\n---------------------------- Productos ----------------------------\n");
        showAll(Product.class);
        System.out.println("\n---------------------------- Pedidos ----------------------------\n");
        showAll(Order.class);

        System.out.println("\n---------------------------- Pedidos con precio < $600 ----------------------------\n");
        showProductsUnder600();

        insertIntoProductsFavIfPriceMoreThan1000();
    }

    private static void insertIntoProductsFavIfPriceMoreThan1000() {
        List<Product> products = getProductsUnder1000();

        products.forEach(Main::insertIntoProductsFav);
    }

    private static void insertIntoProductsFav(Product product) {
        try {
            String query = String.format("INSERT INTO %s (%s) VALUES (?)", SchemeDB.FAV_PRODUCTS, SchemeDB.FAV_PRODUCTS_ID_PRODUCT);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, product.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Product> getProductsUnder1000() {
        List<Product> resultList = new ArrayList<>();

        try {
            String query = String.format("SELECT * FROM %s WHERE %s < 1000.0", SchemeDB.PRODUCTS, SchemeDB.PRODUCT_PRICE);
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while (resultSet.next()) {
                Product actualProduct = new Product();
                actualProduct.setId(resultSet.getInt("id"));
                actualProduct.setTitle(resultSet.getString("nombre"));
                actualProduct.setDescription(resultSet.getString("descripcion"));
                actualProduct.setStock(resultSet.getInt("cantidad"));
                actualProduct.setPrice(resultSet.getDouble("precio"));

                resultList.add(actualProduct);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return resultList;
    }

    private static void showProductsUnder600() {
        try {
            String query = String.format("SELECT * FROM %s WHERE %s < 600.0", SchemeDB.PRODUCTS, SchemeDB.PRODUCT_PRICE);
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();

            printAllColumns(resultSet, metaData);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printAllColumns(ResultSet resultSet, ResultSetMetaData metaData) throws SQLException {
        while (resultSet.next()) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                System.out.print(resultSet.getString(i) + "\t");
            }
            System.out.println();
        }
    }

    private static <T> void showAll(T clazz) {

        String table;

        if (clazz == Employee.class)
            table = SchemeDB.EMPLOYEES;
        else if (clazz == Product.class)
            table = SchemeDB.PRODUCTS;
        else
            table = SchemeDB.ORDERS;

        try {
            String query = String.format("SELECT * FROM " + table);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            ResultSetMetaData metaData = resultSet.getMetaData();

            printAllColumns(resultSet, metaData);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setDescriptionAndTotalAmount(Order order) {
        try {
            String query = String.format("SELECT * FROM %s WHERE %s = ?", SchemeDB.PRODUCTS, SchemeDB.ID);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, order.getProductId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                order.setDescription(resultSet.getString(SchemeDB.PRODUCT_DESCRIPTION));
                order.setTotalAmount(resultSet.getDouble(SchemeDB.PRODUCT_PRICE));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static JSONArray getProductsFromURL() throws IOException {
        URL url = new URL("https://dummyjson.com/products");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuffer stringBuffer = new StringBuffer();
        String line;

        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
        }

        JSONObject response = new JSONObject(stringBuffer.toString());
        JSONArray products = response.getJSONArray("products");
        return products;
    }

    private static void insertIntoProductos(Product product) {
        try {
            String query = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)", SchemeDB.PRODUCTS, SchemeDB.ID, SchemeDB.PRODUCT_NAME, SchemeDB.PRODUCT_DESCRIPTION, SchemeDB.PRODUCT_QUANTITY, SchemeDB.PRODUCT_PRICE);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, product.getId());
            statement.setString(2, product.getTitle());
            statement.setString(3, product.getDescription());
            statement.setInt(4, product.getStock());
            statement.setDouble(5, product.getPrice());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertIntoEmpleados(Employee employee) {
        try {
            String query = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)", SchemeDB.EMPLOYEES, SchemeDB.EMPLOYEES_NAME, SchemeDB.EMPLOYEES_SURNAME, SchemeDB.EMPLOYEES_EMAIL);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getSurname());
            statement.setString(3, employee.getEmail());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertIntoPedidos(Order order) {
        try {
            String query = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)", SchemeDB.ORDERS, SchemeDB.ORDERS_PRODUCT_ID, SchemeDB.ORDERS_DESCRIPTION, SchemeDB.ORDERS_TOTAL_AMOUNT);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, order.getProductId());
            statement.setString(2, order.getDescription());
            statement.setDouble(3, order.getTotalAmount());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}