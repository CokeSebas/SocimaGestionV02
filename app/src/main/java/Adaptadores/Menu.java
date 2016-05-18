package Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.odril.socimagestionv02.R;

import java.util.ArrayList;

import objetos.ItemMenu;
import objetos.ItemSubMenu;

/**
 * Created by Ghost on 29-04-15.
 */
public class Menu extends BaseExpandableListAdapter {


    Context context;
    ArrayList<ItemMenu> ListaItems, ListaOriginal;


   public  Menu(Context context, ArrayList<ItemMenu> ListaItems) {
        this.context = context;
        this.ListaItems = new ArrayList<>();
        this.ListaItems.addAll(ListaItems);
        this.ListaOriginal = new ArrayList<>();
        this.ListaOriginal.addAll(ListaItems);
    }


    @Override
    public int getGroupCount() {
        return ListaItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ItemSubMenu> ListaSubItems = ListaItems.get(groupPosition).getListaSubMenu();

        return ListaSubItems.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return ListaItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ItemSubMenu> ListaSubItems = ListaItems.get(groupPosition).getListaSubMenu();
        return ListaSubItems.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ItemMenu items = (ItemMenu) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_list_item, null);
        }
        TextView TxTituloGrupo = (TextView) convertView.findViewById(R.id.Titulo);
        TxTituloGrupo.setText(items.getTitulo());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        final ItemSubMenu itemsChilds = (ItemSubMenu) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_list_item_child, null);
        }
        TextView TxNombre = (TextView) convertView.findViewById(R.id.Titulo);
        TxNombre.setText(itemsChilds.getNombre());


        return convertView;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
