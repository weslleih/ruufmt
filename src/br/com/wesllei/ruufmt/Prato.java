package br.com.wesllei.ruufmt;

import com.google.gson.annotations.SerializedName;

public class Prato {
	@SerializedName("salada")
	private String salada;
	@SerializedName("pp")
	private String pp;
	@SerializedName("guarnicao")
	private String guarnicao;
	@SerializedName("acompanhamento")
	private String acompanhamento;
	@SerializedName("sobremesa")
	private String sobremesa;

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
