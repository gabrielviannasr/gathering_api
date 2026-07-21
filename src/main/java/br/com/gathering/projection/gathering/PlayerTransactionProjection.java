package br.com.gathering.projection.gathering;

import java.time.LocalDateTime;

public interface PlayerTransactionProjection {
//	Long getIdGathering();
//	String getGatheringName();
	Long getIdEvent();
	Long getIdPlayer();
	String getPlayerName();
	Long getIdTransaction();
	LocalDateTime getCreatedAt();
	Long getIdTransactionType();
	String getTransactionTypeName();
	Double getAmount();
	String getTransactionDescription();
}
