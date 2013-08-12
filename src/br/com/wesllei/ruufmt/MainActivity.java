package br.com.wesllei.ruufmt;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;

import br.com.wesllei.ruufmt.R;

import android.os.Bundle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;

public class MainActivity extends Activity {
	private Ufmt ufmt;
	private ListView listViewAlmoco;
	private ListView listViewJanta;
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progess);
		this.adView = new AdView(this, AdSize.BANNER, "a152078f05557b0");
		ufmt = new Ufmt(this);
		ufmt.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this); // Add this method.
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_update:
			setContentView(R.layout.activity_progess);
			ufmt = null;
			ufmt = new Ufmt(this);
			ufmt.execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void alertError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Não foi possível baixar o cardápio. Culpa da sua 3g ou do site da UFMT.")
				.setTitle("Erro!");

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				setContentView(R.layout.activity_main);
				fixTab();
			}
		});

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();

		dialog.show();
	}

	protected void setCardapio() {
		setContentView(R.layout.activity_main);

		fixTab();

		String title = ufmt.getData();
		if (title != null) {
			setTitle(title);
		}

		listViewAlmoco = (ListView) findViewById(R.id.listAlmoco);
		CardapioListAdapter almocoAdapter = new CardapioListAdapter(this,
				ufmt.getAmoco());

		listViewAlmoco.addFooterView((View) adView);
		listViewAlmoco.setAdapter(almocoAdapter);

		listViewJanta = (ListView) findViewById(R.id.listJanta);
		CardapioListAdapter jantaAdapter = new CardapioListAdapter(this,
				ufmt.getJanta());

		listViewJanta.addFooterView((View) adView);
		listViewJanta.setAdapter(jantaAdapter);

		AdRequest request = new AdRequest();
		request.addTestDevice(AdRequest.TEST_EMULATOR);
		request.addTestDevice("0149BD310E016012");

		adView.loadAd(request);
	}

	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

	private void fixTab() {
		TabHost tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();
		TabHost.TabSpec spec = tabs.newTabSpec("tag1");
		spec.setContent(R.id.almoco);
		spec.setIndicator("Almoço");
		tabs.addTab(spec);
		spec = tabs.newTabSpec("tag2");
		spec.setContent(R.id.janta);
		spec.setIndicator("Janta");
		tabs.addTab(spec);
		tabs.setCurrentTab(0);
	}
}
