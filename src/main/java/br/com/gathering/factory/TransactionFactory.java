package br.com.gathering.factory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.gathering.constant.TransactionType;
import br.com.gathering.entity.Event;
import br.com.gathering.entity.Result;
import br.com.gathering.entity.Transaction;

public class TransactionFactory {

    /**
     * Cria as transações associadas aos resultados de um evento.
     * Cada jogador gera duas transações: Inscrição e Resultado.
     */
    public static List<Transaction> fromResults(Event event, List<Result> results) {
        LocalDateTime createdAt = event.getCreatedAt(); // usa a data do evento

        return results.stream()
            .flatMap(result -> Stream.of(
                createTransaction(event, result, TransactionType.INSCRICAO, createdAt, -event.getConfraFee()),
                createTransaction(event, result, TransactionType.RESULTADO, createdAt, result.getFinalBalance())
            ))
            .collect(Collectors.toList());
    }

    private static Transaction createTransaction(
            Event event,
            Result result,
            TransactionType type,
            LocalDateTime createdAt,
            Double amount) {

        return Transaction.builder()
            .idGathering(event.getIdGathering())
            .idEvent(event.getId())
            .idPlayer(result.getIdPlayer())
            .idTransactionType(type.getId())
            .createdAt(createdAt)
            .amount(amount)
//            .description(type.getDescription())
            .build();
    }
}
