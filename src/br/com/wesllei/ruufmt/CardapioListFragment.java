package br.com.wesllei.ruufmt;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class CardapioListFragment extends Fragment {

	private CardapioListAdapter adapter;
	private AdView adView;
	public static final String ARG_REFEICAO_NUMBER = "refeicao";
	private final static String MY_AD_UNIT_ID = "a152078f05557b0";

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ListView rootView = (ListView) inflater.inflate(
				R.layout.activity_list_cardapio, container, false);

		Cardapio cardapio = new Cardapio();
		cardapio.setCardapio(this.getActivity());

		if (getArguments().getInt(ARG_REFEICAO_NUMBER) == 0) {
			adapter = new CardapioListAdapter(this.getActivity(),
					cardapio.getAlmocoList());
		} else {
			adapter = new CardapioListAdapter(this.getActivity(),
					cardapio.getJantaList());
		}

		

		adView = new AdView(this.getActivity());
		adView.setAdUnitId(MY_AD_UNIT_ID);
		adView.setAdSize(AdSize.BANNER);

		rootView.addFooterView(adView);

		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				"2A8194AC3148DDFB651F5AE619F07D3D").build();
		adView.loadAd(adRequest);
		
		rootView.setAdapter(adapter);
		
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}
}
