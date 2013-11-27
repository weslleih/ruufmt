package br.com.wesllei.ruufmt;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Prato implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2656896334983643324L;
	@SerializedName("salada")
	private String salada = new String();
	@SerializedName("pp")
	private String pp = new String();
	@SerializedName("guarnicao")
	private String guarnicao = new String();
	@SerializedName("acompanhamento")
	private String acompanhamento = new String();
	@SerializedName("sobremesa")
	private String sobremesa = new String();

	public String getSalada() {
		return salada;
	}

	public void setSalada(String salada) {
		this.salada = salada;
	}

	public String getPp() {
		return pp;
	}

	public void setPp(String pp) {
		this.pp = pp;
	}

	public String getGuarnicao() {
		return guarnicao;
	}

	public void setGuarnicao(String guarnicao) {
		this.guarnicao = guarnicao;
	}

	public String getAcompanhamento() {
		return acompanhamento;
	}

	public void setAcompanhamento(String acompanhamento) {
		this.acompanhamento = acompanhamento;
	}

	public String getSobremesa() {
		return sobremesa;
	}

	public void setSobremesa(String sobremesa) {
		this.sobremesa = sobremesa;
	}
}
