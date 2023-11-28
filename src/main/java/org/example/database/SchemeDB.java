package org.example.database;

public interface SchemeDB {

    String HOST = "127.0.0.1:3306";
    String DB_NAME = "almacen";
    String ID = "id";

    //Products table
    String PRODUCTS = "productos";
    String PRODUCT_NAME = "nombre";
    String PRODUCT_DESCRIPTION = "descripcion";
    String PRODUCT_PRICE = "precio";
    String PRODUCT_QUANTITY = "cantidad";

    //Employees table
    String EMPLOYEES = "empleados";
    String EMPLOYEES_NAME = "nombre";
    String EMPLOYEES_SURNAME = "apellidos";
    String EMPLOYEES_EMAIL = "correo";

}
