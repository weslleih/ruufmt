package br.com.wesllei.ruufmt;

import java.util.ArrayList;

import br.com.wesllei.ruufmt.CardapioFragment.SectionsPagerAdapter;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class CardapioListFragment extends Fragment {

	private CardapioListAdapter adapter;
	public static final String ARG_REFEICAO_NUMBER = "refeicao";

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(false);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey("List")) {
			this.adapter = (CardapioListAdapter) savedInstanceState
					.getSerializable("List");
		} else {
			
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// View rootView = inflater.inflate(R.layout.activity_list_cardapio,
		// container, false);

		ListView rootView = (ListView) inflater.inflate(
				R.layout.activity_list_cardapio, container, false);
		Cardapio cardapio = new Cardapio();
		cardapio.setCardapio(this.getActivity());
		if (savedInstanceState != null
				&& savedInstanceState.containsKey("List")) {
			this.adapter = (CardapioListAdapter) savedInstanceState
					.getSerializable("List");
		} else {
			if (getArguments().getInt(ARG_REFEICAO_NUMBER) == 0) {
				adapter = new CardapioListAdapter(this.getActivity(),
						cardapio.getAlmocoList());
			} else {
				adapter = new CardapioListAdapter(this.getActivity(),
						cardapio.getJantaList());
			}
		}
		rootView.setAdapter(adapter);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}
	
	public void onSaveInstanceState(Bundle savedState) {
		savedState.putSerializable("list", adapter);
		super.onSaveInstanceState(savedState);
	}

}
