package objetos;


        import java.util.ArrayList;

/**
 * Created by Alvaro on 12/02/2015.
 */
public class ItemMenu
{
    String Titulo, ID;
    ArrayList<ItemSubMenu> ListaSubMenu = new ArrayList<ItemSubMenu>();
    //Constructor Vacio
   public  ItemMenu(){}
    // Constructor con variables
   public ItemMenu(String Titulo, String ID, ArrayList<ItemSubMenu> ListaSubMenu)
    {
        this.Titulo = Titulo;
        this.ID = ID;
        this.ListaSubMenu = ListaSubMenu;
    }


    public String getTitulo() {
        return Titulo;
    }

    public String getID() {
        return ID;
    }

    public ArrayList<ItemSubMenu> getListaSubMenu() {
        return ListaSubMenu;
    }
}
