package br.com.wesllei.ruufmt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Cardapio {
	@SerializedName("almoco")
	private Prato almoco;
	@SerializedName("janta")
	private Prato janta;
	@SerializedName("date")
	private Long date;

	public ArrayList<ItemPrato> getAlmoco() {
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

	public ArrayList<ItemPrato> getJanta() {
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
}
