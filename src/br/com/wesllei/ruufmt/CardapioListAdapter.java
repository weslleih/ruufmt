package br.com.wesllei.ruufmt;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CardapioListAdapter extends BaseAdapter {
	private Context context;
	private List<ItemPrato> cardapio;

	public CardapioListAdapter(Context context, List<ItemPrato> cardapio) {
		super();
		this.cardapio = cardapio;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cardapio.size();
	}

	@Override
	public Object getItem(int location) {
		// TODO Auto-generated method stub
		return cardapio.get(location);
	}

	@Override
	public long getItemId(int location) {
		// TODO Auto-generated method stub
		return location;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemPrato itemPrato = cardapio.get(position);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.activity_item, null);

		TextView textTipo = (TextView) view.findViewById(R.id.tipo);
		StringBuilder tipo = new StringBuilder();
		tipo.append(Character.toUpperCase(itemPrato.getTipo().charAt(0)))
				.append(itemPrato.getTipo().substring(1));
		textTipo.setText(tipo);

		TextView textCapital = (TextView) view.findViewById(R.id.prato);
		textCapital.setText(itemPrato.getPrato());
		return view;
	}

}