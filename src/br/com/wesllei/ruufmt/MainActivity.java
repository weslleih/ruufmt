package br.com.wesllei.ruufmt;

import java.util.ArrayList;

import com.google.analytics.tracking.android.EasyTracker;

import br.com.wesllei.ruufmt.R;

import android.os.Bundle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Ufmt ufmt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progess);
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
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// 2. Chain together various setter methods to set the dialog
		// characteristics
		builder.setMessage(
				"Não foi possível baixar o cardápio. Culpa da sua 3g ou do site da UFMT.")
				.setTitle("Erro!");

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				setContentView(R.layout.activity_main);

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
		});

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();

		dialog.show();
	}

	protected void setCardapio() {
		setContentView(R.layout.activity_main);

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

		String title = ufmt.getData();
		if (title != null) {
			setTitle(title);
		}

		ArrayList<String> almoco = ufmt.getAmoco();
		for (int i = 0; i < almoco.size() - 1; i++) {
			String node = almoco.get(i);
			if (node.equalsIgnoreCase("salada")) {
				TextView salada = (TextView) findViewById(R.id.almocoSalada);
				salada.setText(almoco.get(i + 1));
			}
			if (node.equalsIgnoreCase("prato proteico")) {
				TextView salada = (TextView) findViewById(R.id.almocoPp);
				salada.setText(almoco.get(i + 1));
			}
			if (node.equalsIgnoreCase("guarnição")) {
				TextView salada = (TextView) findViewById(R.id.almocoGuarnicao);
				salada.setText(almoco.get(i + 1));
			}
			if (node.equalsIgnoreCase("acompanhamento")) {
				TextView salada = (TextView) findViewById(R.id.almocoAcompanhamento);
				salada.setText(almoco.get(i + 1));
			}
			if (node.equalsIgnoreCase("sobremesa")) {
				TextView salada = (TextView) findViewById(R.id.almocoSobremesa);
				salada.setText(almoco.get(i + 1));
			}
		}
		ArrayList<String> janta = ufmt.getJanta();
		for (int j = 0; j < janta.size() - 1; j++) {
			String node = almoco.get(j);
			if (node.equalsIgnoreCase("salada")) {
				TextView salada = (TextView) findViewById(R.id.jantaSalada);
				salada.setText(janta.get(j + 1));
			}
			if (node.equalsIgnoreCase("prato proteico")) {
				TextView salada = (TextView) findViewById(R.id.jantaPp);
				salada.setText(janta.get(j + 1));
			}
			if (node.equalsIgnoreCase("guarnição")) {
				TextView salada = (TextView) findViewById(R.id.jantaGuarnicao);
				salada.setText(janta.get(j + 1));
			}
			if (node.equalsIgnoreCase("acompanhamento")) {
				TextView salada = (TextView) findViewById(R.id.jantaAcompanhamento);
				salada.setText(janta.get(j + 1));
			}
			if (node.equalsIgnoreCase("sobremesa")) {
				TextView salada = (TextView) findViewById(R.id.jantaSobremesa);
				salada.setText(janta.get(j + 1));
			}
		}
	}

}
