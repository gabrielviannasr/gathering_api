package br.com.gathering.service;

import java.util.Arrays;
import java.util.List;

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
import br.com.gathering.dto.request.TransactionDTO;
import br.com.gathering.dto.response.TransactionResponseDTO;
import br.com.gathering.entity.Transaction;
import br.com.gathering.mapper.TransactionResponseMapper;
import br.com.gathering.projection.gathering.PlayerWalletProjection;
import br.com.gathering.repository.GatheringRepository;
import br.com.gathering.repository.PlayerRepository;
import br.com.gathering.repository.TransactionRepository;
import br.com.gathering.util.LogHelper;
import jakarta.transaction.Transactional;

@Service
public class TransactionService extends AbstractService<Transaction> {

	private static final Logger log = LogHelper.getLogger();
	private static final String ENTITY = "Transaction";
	
	@Autowired
	private TransactionRepository repository;

	@Autowired
	private GatheringRepository gatheringRepository;

	@Autowired
	private PlayerRepository playerRepository;

	public static Sort getSort() {
		return Sort.by(Order.asc("idGathering"), Order.asc("idPlayer"), Order.asc("createdAt"));
	}

	public List<TransactionResponseDTO> getList(Transaction model) {

		LogHelper.info(log, "Fetching list", "model", model);

		List<Transaction> result = repository.findAll(getExample(model), getSort());

		LogHelper.info(log, "Fetched list", "count", result.size());

		return result.stream().map(TransactionResponseMapper::from).toList();
	}

	public Page<Transaction> getPage(Transaction model, Sort sort, int page, int size) {

		LogHelper.info(log, "Fetching paged list", "page", page, "size", size);

		Page<Transaction> result = repository.findAll(getExample(model), PageRequest.of(page, size, sort));

		LogHelper.info(log, "Fetched paged list", "totalElements", result.getTotalElements());

		return result;
	}

	public Transaction getById(Long id) {

		LogHelper.info(log, "Fetching by id", "id", id);

		Transaction found = repository.findById(id)
	            .orElseThrow(() -> {
	                LogHelper.warn(log, ENTITY + " not found", "id", id);
	                return new ResponseStatusException(
	                        HttpStatus.NOT_FOUND, ENTITY + " not found");
	            });

	    LogHelper.info(log, "Found", "id", found.getId());

		return found;
	}

	public TransactionResponseDTO getResponseById(Long id) {
	    return TransactionResponseMapper.from(getById(id));
	}

	@Transactional
	public Transaction create(TransactionDTO dto) {

		Transaction model = dto.toModel();

	    model.init();

	    validate(model);

	    LogHelper.info(log, "Saving", "model", model);

	    Transaction saved = repository.save(model);

	    LogHelper.info(log, "Saved", "id", saved.getId());

	    return saved;
	}

	public Transaction update(Long id, TransactionDTO dto) {

		Transaction current = getById(id);
	    
	    Transaction model = dto.toModel();

		 // Apenas campos editáveis.
		 // Gathering, Player e Event definem o contexto da transação e não podem ser alterados.
		 // Caso seja necessário mudar algum desses vínculos, a transação deve ser excluída e recriada.
		 current.setAmount(model.getAmount());
		 current.setDescription(model.getDescription());
		 current.setIdTransactionType(model.getIdTransactionType());

	    validate(current);

	    LogHelper.info(log, "Updating", "model", current);

	    Transaction saved = repository.save(current);

	    LogHelper.info(log, "Updated", "id", saved.getId());

	    return saved;
	}

	public void delete(Long id) {

		Transaction current = getById(id);

	    LogHelper.info(log, "Removing", "id", id);

	    repository.delete(current);
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
