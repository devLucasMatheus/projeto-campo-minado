package br.com.cod3r.cm.modelo;

import java.util.ArrayList;
import java.util.List;

public class Campo {
	
	private final int linha;
	private final int coluna;
	
	private boolean aberto = false;
	private boolean minado = false;
	private boolean marcado = false;
	
	private List<Campo> vizinhos = new ArrayList<>();
	private List<CampoObservador> observadores = new ArrayList<>();

	Campo(int linha, int coluna) {
		this.linha = linha;
		this.coluna = coluna;
	}
	
	public void registrarObservador(CampoObservador observador) {
		observadores.add(observador);
	}
	
	private void notificarObservadores(CampoEvento evento) {
		observadores.stream()
		.forEach(o -> o.eventoOcorreu(this, evento));
		
	}
	
	boolean adicionarVizinho(Campo vizinho) {
		boolean linhaDiferente = linha != vizinho.linha; // linha é diferente do campo atual ? (campo será passado)
		boolean colunaDiferente = coluna != vizinho.coluna; // coluna é diferente do campo atual ?
		boolean diagonal = linhaDiferente && colunaDiferente; //será diagonal se ambas acima retornarem true
		
		int deltaLinha = Math.abs(linha - vizinho.linha); //calculando numero absoluto na linha (ex: 2 - 3 = 1)
		int deltaColuna = Math.abs(coluna - vizinho.coluna);//calculando numero absoluto na linha (ex: 4 - 4 = 0)
		int deltaGeral = deltaLinha + deltaColuna; // soma os numeros absolutos encontrados acima
		
		if(deltaGeral == 1 && !diagonal) {
			vizinhos.add(vizinho); //adicionar na lista
			return true;
		} else if(deltaGeral == 2 && diagonal) {
			vizinhos.add(vizinho); // segunda hipotese para adicionar na lista
			return true;
		} else {
			return false; // se nenhuma acima for atendida, não será adiconado na lista 
		}
	}
	
	public void alternarMarcacao() {
		if(!aberto) {
			marcado = !marcado;
			
			if(marcado) {
				notificarObservadores(CampoEvento.MARCAR);
			} else {
				notificarObservadores(CampoEvento.DESMARCAR);
			}
		}
	}
	
	public boolean abrir() {
		if(!aberto && !marcado) {
			if(minado) {
				notificarObservadores(CampoEvento.EXPLODIR);
				return true;
		}
				setAberto(true);
			
			if(vizinhancaSegura()) {
				vizinhos.forEach(v -> v.abrir());
			}
			return true;
	} else {
		return false;
	}
	}	
	
	public boolean vizinhancaSegura() {
		return vizinhos.stream().noneMatch(v -> v.minado);
		}
	
	void minar() {
		minado = true;
	}
	
	public boolean isMinado() {
		return minado;
	}
	
	public boolean isMarcado() {
		return marcado;
	}
	
	 void setAberto(boolean aberto) {
		this.aberto = aberto;
		
		if(aberto) {
			notificarObservadores(CampoEvento.ABRIR);
		}
	}

	public boolean isAberto() {
		return aberto;
	}
	
	public boolean isFechdo() {
		return !isAberto();
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}
	
	boolean objetivoAlcancado() {
		boolean desvendado = !minado && aberto;
		boolean protegido = minado && marcado;
		return desvendado || protegido;
	}
	
	public int minasNaVizinhanca() {
		return (int) vizinhos.stream().filter(v -> v.minado).count();
	}
	
	void reiniciar() {
		aberto = false;
		minado = false;
		marcado = false;
		notificarObservadores(CampoEvento.REINICIAR);
	}
	
	
}
