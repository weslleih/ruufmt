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
	private List<Prato> cardapio;
	
	public CardapioListAdapter(Context context,List<Prato> cardapio) {
		super();
		//this.cardapio = cardapio;
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
		Prato prato = cardapio.get(position);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.activity_item, null);

		TextView textTipo = (TextView) view.findViewById(R.id.tipo);
		StringBuilder tipo = new StringBuilder();
		tipo.append(Character.toUpperCase(prato.getTipo().charAt(0))).append(prato.getTipo().substring(1));
		textTipo.setText(tipo);

		TextView textCapital = (TextView) view.findViewById(R.id.prato);
		textCapital.setText(prato.getTipo());
		return view;
	}

}
