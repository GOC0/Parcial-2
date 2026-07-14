package logic;

import jakarta.persistence.*;

@Entity
@Table(name="usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    String usuario;
    String password;
    Rol rol;

    public Usuario() {}

    public Usuario(String nombre, String password, Rol rol) {
        this.usuario = nombre;
        this.password = password;
        this.rol = rol;
    }

    public Usuario(String nombre, String password) {
        this.usuario = nombre;
        this.password = password;
        this.rol = Rol.Participante;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
