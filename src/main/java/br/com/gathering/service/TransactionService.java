package br.com.gathering.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.gathering.constant.TransactionType;
import br.com.gathering.dto.response.EventResponseDTO;
import br.com.gathering.dto.response.FormatResponseDTO;
import br.com.gathering.dto.response.PlayerResponseDTO;
import br.com.gathering.dto.response.TransactionResponseDTO;
import br.com.gathering.dto.response.TransactionTypeResponseDTO;
import br.com.gathering.entity.Transaction;
import br.com.gathering.projection.gathering.PlayerWalletProjection;
import br.com.gathering.repository.GatheringRepository;
import br.com.gathering.repository.PlayerRepository;
import br.com.gathering.repository.TransactionRepository;
import br.com.gathering.util.LogHelper;

@Service
public class TransactionService extends AbstractService<Transaction> {

	private static final Logger log = LogHelper.getLogger();

	@Autowired
	private TransactionRepository repository;

	@Autowired
	private GatheringRepository gatheringRepository;

	@Autowired
	private PlayerRepository playerRepository;

	public static Sort getSort() {
		return Sort.by(Order.asc("idGathering"), Order.asc("idPlayer"), Order.asc("createdAt"));
	}

	public List<Transaction> getList(Transaction model) {
		List<Transaction> result = repository.findAll(getExample(model), getSort());
		LogHelper.info(log, "Fetched list", "count", result.size());
		return result;
	}

	public Page<TransactionResponseDTO> getPage(Transaction model, Sort sort, int page, int size) {
		Page<Transaction> list = repository.findAll(getExample(model), PageRequest.of(page, size, sort));
		return list.map(this::buildTransactionResponse);
	}

    private TransactionResponseDTO buildTransactionResponse(Transaction item) {
    	return TransactionResponseDTO.builder()
    			.id(item.getId())
    			.amount(item.getAmount())
    			.createdAt(item.getCreatedAt())
    			.description(item.getTransactionType().getDescription())
    			.type(
					TransactionTypeResponseDTO.builder()
					.id(item.getTransactionType().getId())
					.name(item.getTransactionType().getName())
					.build())
    			.player(
					PlayerResponseDTO.builder()
					.id(item.getIdPlayer())
					.name(item.getPlayerName())
    					.build())
    			.event(
					item.getEvent() == null ? null :
    				EventResponseDTO.builder()
    				.id(item.getEvent().getId())
    				.createdAt(item.getEvent().getCreatedAt())
    				.format(FormatResponseDTO.builder()
						.id(item.getEvent().getFormat().getId())
						.name(item.getEvent().getFormat().getName())
						.build())
    				.build())    				
    			.build();
    }

	public Transaction getById(Long id) {
		LogHelper.info(log, "Fetching by ID", "id", id);
		Optional<Transaction> optional = repository.findById(id);
		if (optional.isEmpty()) {
			LogHelper.warn(log, "Not found", "id", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		Transaction event = optional.get();
		LogHelper.info(log, "Found", "id", event.getId());
		return event;
	}

	public Transaction save(Transaction model) {
		model.init();
		validate(model);
		LogHelper.info(log, "Saving", "payload", model);
		Transaction saved = repository.save(model);
		LogHelper.info(log, "Saved", "id", saved.getId());
		return saved;
}	

	@SuppressWarnings("incomplete-switch")
	private void validate(Transaction model) {

	if (model.getIdGathering() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID da gathering é obrigatório");

	if (model.getIdPlayer() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do player é obrigatório");

	if (model.getIdTransactionType() == null)
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID do tipo de transação é obrigatório");

	if (!gatheringRepository.existsById(model.getIdGathering())) {
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Gathering não encontrada");
	}

	if (!playerRepository.existsById(model.getIdPlayer())) {
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado");
	}

	TransactionType type = Arrays.stream(TransactionType.values())
		.filter(t -> t.getId() == model.getIdTransactionType())
		.findFirst()
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de transação não encontrado"));

		/**
		 * Future rule: player must belong to the gathering of the transaction.
		 * This validation is currently disabled because some players may not have 
		 * participated in any event but can still pay the confra fee manually.
		 */
		// if (!playerRepository.playerBelongsToGathering(model.getIdPlayer(), model.getIdGathering())) {
		// 	throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
		// 		"O jogador não pertence à gathering da transação");
		// }

		// 🔒 Garante consistência de relacionamento entre tipo e evento
		switch (type) {
			case INSCRICAO, RESULTADO -> {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"Transações de inscrição e resultado são geradas automaticamente pelo sistema");
			}
			case DEPOSITO, SAQUE -> {
				// 🔸 Evita vínculo indevido com evento
				if (model.getIdEvent() != null) {
					LogHelper.warn(log, "Removendo id_event de transação manual", "idEvent", model.getIdEvent(), "type", type);
					model.setIdEvent(null);
				}
			}
		}

		// 💰 Regras específicas de valor
		switch (type) {
			case DEPOSITO -> {
				if (model.getAmount() == null || model.getAmount() <= 0)
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor do depósito deve ser positivo");
			}
			case SAQUE -> {
				if (model.getAmount() == null || model.getAmount() >= 0)
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor do saque deve ser negativo");

				Double wallet = getWalletBalance(model.getIdGathering(), model.getIdPlayer());

				if (wallet + model.getAmount() < 0)
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para saque");
			}
		}
	}

	public double getWalletBalance(Long idGathering, Long idPlayer) {
		PlayerWalletProjection walletProjection = repository.getWalletBalance(idGathering, idPlayer);
		return (walletProjection != null && walletProjection.getWallet() != null)
			? walletProjection.getWallet()
			: 0.0; // In case of first event, so without transactions
	}

}
