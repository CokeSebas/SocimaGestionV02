package objetos;

/**
 * Created by Ghost on 29-04-15.
 */
public class ItemSubMenu {

    String Nombre, ID, MenuPadre;

   public ItemSubMenu() {
    }

   public  ItemSubMenu(String Nombre, String ID, String MenuPadre) {
        this.Nombre = Nombre;
        this.ID = ID;
        this.MenuPadre = MenuPadre;
    }

    public String getID() {
        return ID;
    }

    public String getNombre() {


        return Nombre;
    }

    public String getMenuPadre() {
        return MenuPadre;
    }

    public String getNombreLimpio() {
        String N = "";

        for (int i = 3; i < Nombre.length(); i++) {
            N += Nombre.charAt(i);
        }
        return N;
    }
}
