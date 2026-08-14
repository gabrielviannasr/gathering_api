package br.com.gathering.projection;

public interface SummaryProjection {
	Long getId();
	Integer getYear();
	String getName();
	Integer getPlayers();
	Integer getRounds();
	Double getLoserPot();
	Double getConfraPot();
	Double getPrize();
}
