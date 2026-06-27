package com.tupaquete.app.models;

public class MenuItem {
    private int id;
    private String titulo;
    private String descripcion;
    private int iconoRes;
    private String colorHex;
    private String nombreActivity; // 🆕 Nombre completo de la clase Activity
    
    public MenuItem(int id, String titulo, String descripcion, int iconoRes, String colorHex, String nombreActivity) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.iconoRes = iconoRes;
        this.colorHex = colorHex;
        this.nombreActivity = nombreActivity;
    }
    
    // Getters
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public int getIconoRes() { return iconoRes; }
    public String getColorHex() { return colorHex; }
    public String getNombreActivity() { return nombreActivity; }
}