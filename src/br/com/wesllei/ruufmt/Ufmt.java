package br.com.wesllei.ruufmt;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class Ufmt extends AsyncTask<Void, Void, Boolean>{
	private String url = null;
	private Document doc = null;
	private MainActivity mainActivity;

	public Ufmt(MainActivity activity) {
		url = "http://www.ufmt.br/ufmt/unidade/index.php/secao/visualizar/3793/RU";
		mainActivity = activity;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		Connection.Response response = null;
		try {
			response = Jsoup
					.connect(this.url)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.0) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.46 Safari/536.5")
					.timeout(100000).header("Accept-Language", "pt")
					.ignoreHttpErrors(true).execute();
		} catch (IOException e) {
			System.out.println("io - " + e);
			return false;
		}

		try {
			this.doc = response.parse();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}	
		return true;
	}

	public String getData(){
		Element secao = doc.getElementById("secao");
		
		Elements p = secao.getElementsByTag("p");
		Pattern pattern = Pattern.compile(".*([0-9]{1,2}[/][0-9]{1,2}[/][0-9]{2,4}).*");
		for (Element element : p) {
			Matcher m = pattern.matcher(element.text());
			if(m.matches()){
				return m.group(0);
			}
		}
		return null;
		
	}

	public ArrayList<Prato> getAmoco() {
		Element secao = doc.getElementById("secao");
		ArrayList<Prato> list = new ArrayList<Prato>();
		String pratoString, tipo;
		Prato prato;
		
		Elements tables = secao.getElementsByTag("table");
		
		Element table = tables.first();
		
		Elements trs = table.getElementsByTag("tr");
		for (Element tr : trs) {
			Elements tds = tr.getElementsByTag("td");
			if(tds.size() == 2){
				tipo = tds.get(0).text().trim().toLowerCase().replaceAll("\u00a0", "").trim().replaceAll("/", ", ");
				pratoString = tds.get(1).text().trim().toLowerCase().replaceAll("\u00a0", "").trim().replaceAll("/", ", ");
				prato = new Prato(tipo,pratoString);
				list.add(prato);
			}
		}
		return list;
	}

	public ArrayList<Prato> getJanta() {
		Element secao = doc.getElementById("secao");
		ArrayList<Prato> list = new ArrayList<Prato>();
		String pratoString, tipo;
		Prato prato;
		
		Elements tables = secao.getElementsByTag("table");
		
		Element table = tables.get(1);
		
		Elements trs = table.getElementsByTag("tr");
		for (Element tr : trs) {
			Elements tds = tr.getElementsByTag("td");
			if(tds.size() == 2){
				tipo = tds.get(0).text().trim().toLowerCase().replaceAll("\u00a0", "").trim().replaceAll("/", ", ");
				pratoString = tds.get(1).text().trim().toLowerCase().replaceAll("\u00a0", "").trim().replaceAll("/", ", ");
				prato = new Prato(tipo,pratoString);
				list.add(prato);
			}
		}
		return list;

	}
	
	protected void onPostExecute(Boolean result) {
       if(result){
    	   mainActivity.setCardapio();
       }else{
    	   mainActivity.alertError();
       }
    }
	
}
