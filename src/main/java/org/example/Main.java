package org.example;

import org.example.database.DbHandler;
import org.example.database.SchemeDB;
import org.example.model.Product;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    static Connection connection = DbHandler.getConnection();

    public static void main(String[] args) {

        try {
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
}