package br.com.wesllei.ruufmt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class Cardapio implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3502921314965074769L;
	@SerializedName("almoco")
	private Prato almoco = new Prato();
	@SerializedName("janta")
	private Prato janta = new Prato();
	@SerializedName("date")
	private Long date;

	public Prato getAlmoco() {
		return almoco;
	}

	public ArrayList<ItemPrato> getAlmocoList() {
		ArrayList<ItemPrato> list = new ArrayList<ItemPrato>();
		list.add(new ItemPrato("SALADA", almoco.getSalada()));
		list.add(new ItemPrato("PRATO PROTEICO", almoco.getPp()));
		list.add(new ItemPrato("GUARNIÇÃO", almoco.getGuarnicao()));
		list.add(new ItemPrato("ACOMPANHAMENTO", almoco.getAcompanhamento()));
		list.add(new ItemPrato("SOBREMESA SUCO", almoco.getSobremesa()));
		return list;

	}

	public void setAlmoco(Prato almoco) {
		this.almoco = almoco;
	}

	public Prato getJanta() {
		return janta;
	}

	public ArrayList<ItemPrato> getJantaList() {
		ArrayList<ItemPrato> list = new ArrayList<ItemPrato>();
		list.add(new ItemPrato("SALADA", janta.getSalada()));
		list.add(new ItemPrato("PRATO PROTEICO", janta.getPp()));
		list.add(new ItemPrato("GUARNIÇÃO", janta.getGuarnicao()));
		list.add(new ItemPrato("ACOMPANHAMENTO", janta.getAcompanhamento()));
		list.add(new ItemPrato("SOBREMESA SUCO", janta.getSobremesa()));
		return list;
	}

	public void setJanta(Prato janta) {
		this.janta = janta;
	}

	public Date getDate() {
		return new Date(date);
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public void setCardapio(Context context) {
		Cardapio cardapioTemp;
		File file = new File(context.getFilesDir(), "cardapio");
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			cardapioTemp = (Cardapio) ois.readObject();
			this.setAlmoco(cardapioTemp.getAlmoco());
			this.setJanta(cardapioTemp.getJanta());
			this.setDate(cardapioTemp.getDate().getTime());
		} catch (IOException e) {
			Log.i("File", e.getMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (ois != null)
					ois.close();
				if (fis != null)
					fis.close();
			} catch (Exception e) { /* do nothing */
			}
		}
	}
}
