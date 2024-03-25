package org.example;
import java.sql.*;
import java.util.Scanner;

public class Games {
    private static java.sql.Connection con;
    private static int userId;
    private static String userName;
    private static int currentscreen = 0;
    public static void main(String[] args) throws SQLException {
        String host = "jdbc:sqlite:src/main/resources/Games";
        con = java.sql.DriverManager.getConnection(host);
        int option;
        printTitulo();
        while (true){
            printMenu();
            option = getOption();
            if (option == 0) break;
            if (currentscreen == 0){
                switch (option){
                    case 1: login();
                        break;
                    case 2: register();
                        break;
                }
            }else {
                switch (option){
                    case 1: misJuegos();
                        break;
                    case 2: comprarJuegos();
                        break;
                    case 3: otrosJuegos();
                        break;
                    case 4: logout();
                        break;
                }
            }
        }
    }
    public static void printTitulo(){
        System.out.println("\n" +
                "\n" +
                "  ▄████  ▄▄▄       ███▄ ▄███▓▓█████   ██████ \n" +
                " ██▒ ▀█▒▒████▄    ▓██▒▀█▀ ██▒▓█   ▀ ▒██    ▒ \n" +
                "▒██░▄▄▄░▒██  ▀█▄  ▓██    ▓██░▒███   ░ ▓██▄   \n" +
                "░▓█  ██▓░██▄▄▄▄██ ▒██    ▒██ ▒▓█  ▄   ▒   ██▒\n" +
                "░▒▓███▀▒ ▓█   ▓██▒▒██▒   ░██▒░▒████▒▒██████▒▒\n" +
                " ░▒   ▒  ▒▒   ▓▒█░░ ▒░   ░  ░░░ ▒░ ░▒ ▒▓▒ ▒ ░\n" +
                "  ░   ░   ▒   ▒▒ ░░  ░      ░ ░ ░  ░░ ░▒  ░ ░\n" +
                "░ ░   ░   ░   ▒   ░      ░      ░   ░  ░  ░  \n" +
                "      ░       ░  ░       ░      ░  ░      ░  \n" +
                "\n");
    }
    public static void printMenu(){

        System.out.println("--------------------------------------------------------------------------------------------");
        if (currentscreen == 0){
            System.out.println("0 Salir | 1 Login | 2 Registrarse");
        }else {
            System.out.println("0 Salir | 1 Mis Juegos | 2 Comprar Juegos | 3 Otros Juegos | 4 Logout " + " - " + userName);
        }
        System.out.println("--------------------------------------------------------------------------------------------");
    }
    private static int getOption(){
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        try{
            option = Integer.parseInt(scanner.nextLine());
            if ((currentscreen == 0 && option > 2) || (currentscreen == 1 && option > 4)){
                System.out.println("Incorrect Option");
            }
        }catch (IllegalArgumentException iae){
            System.out.println("Incorrect Option");
        }
        return  option;
    }
    private static void login() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Usuario: ");
        String user = scanner.nextLine();
        PreparedStatement st = null;
        String query = "SELECT * FROM persona WHERE usuario = ?";
        st = con.prepareStatement(query);
        st.setString(1,user);
        ResultSet rs = st.executeQuery();
        if (rs.next()){
            userId = rs.getInt("id_persona");
            userName = rs.getString("usuario");
            currentscreen = 1;
        }else {
            System.out.println("USER NOT FOUND");
        }
    }
    private static void logout(){
        currentscreen = 0;
    }
    private static void register() throws SQLException{
        Scanner scanner = new Scanner(System.in);
        PreparedStatement st = null;
        System.out.println("Usuario: ");
        String user = scanner.nextLine();
        System.out.println("Nombre: ");
        String name = scanner.nextLine();
        System.out.println("Apellidos: ");
        String lastname = scanner.nextLine();
        String query = "INSERT INTO persona (usuario,nombre, apellidos) VALUES(?,?,?)";
        st = con.prepareStatement(query);
        st.setString(1,user);
        st.setString(2,name);
        st.setString(3,lastname);
        st.executeUpdate();
        System.out.println("Registro completado correctamente!");
    }
    private static void misJuegos() throws SQLException{
        PreparedStatement st = null;
        String query = "SELECT juego.id_juego,juego.nombre,usuario FROM persona INNER JOIN juego on juego.id_persona = persona.id_persona WHERE persona.id_persona = ?";
        st = con.prepareStatement(query);
        st.setString(1, String.valueOf(userId));
        ResultSet rs = st.executeQuery();
        while (rs.next()){
            System.out.println(rs.getInt("id_juego")  + " - " + rs.getString("nombre") + " - " + rs.getString("usuario"));
        }
    }
    private static void otrosJuegos() throws SQLException{
        PreparedStatement st = null;
        String query = "SELECT juego.id_juego,juego.nombre,juego.precio FROM juego INNER JOIN persona on juego.id_persona = persona.id_persona WHERE persona.id_persona != ?";
        st = con.prepareStatement(query);
        st.setString(1, String.valueOf(userId));
        ResultSet rs = st.executeQuery();
        while (rs.next()){
            System.out.println(rs.getInt("id_juego") + " - " + rs.getString("nombre") + " - " + rs.getString("precio") + "€");
        }
    }
    private static void comprarJuegos()throws SQLException{
        Scanner scanner = new Scanner(System.in);
        int juegoId;
        otrosJuegos();
        PreparedStatement st = null;
        System.out.println("Introduce el ID del juego: ");
        juegoId = Integer.parseInt(scanner.nextLine());
        System.out.println("Introduce el nombre del juego: ");
        String nombre = scanner.nextLine();
        System.out.println("Introduce el precio del juego: ");
        int precio = scanner.nextInt();
        String query = "INSERT INTO juego (nombre,precio,id_persona) VALUES(?,?,?)";
        st = con.prepareStatement(query);
        st.setString(1, nombre);
        st.setInt(2,precio);
        st.setInt(3,userId);
        st.executeUpdate();
    }
}