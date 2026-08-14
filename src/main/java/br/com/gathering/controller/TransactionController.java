package br.com.gathering.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.gathering.dto.request.TransactionDTO;
import br.com.gathering.dto.response.TransactionResponseDTO;
import br.com.gathering.entity.Transaction;
import br.com.gathering.mapper.TransactionResponseMapper;
import br.com.gathering.service.TransactionService;
import br.com.gathering.util.LogHelper;
import br.com.gathering.util.RouteHelper;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

	private static final Logger log = LogHelper.getLogger();
	private static final String PATH = "/transaction";

	@Autowired
	private TransactionService service;

	@GetMapping
	public List<TransactionResponseDTO> getList(Transaction model) {
		LogHelper.info(log, RouteHelper.GET(PATH), "model", model);
		return service.getList(model);
	}

	@GetMapping("/page")
	public Page<TransactionResponseDTO> getPage(Transaction model,
			@SortDefault.SortDefaults({ @SortDefault(sort = "idPlayer"), @SortDefault(sort = "idGathering"), @SortDefault(sort = "idTransactionType"), @SortDefault(sort = "createdAt") }) Sort sort,
			@RequestParam int page,
			@RequestParam int size) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/page"), "page", page, "size", size);
		return service.getPage(model, sort, page, size).map(TransactionResponseMapper::from);	
	}

	@GetMapping("/{id}")
	public TransactionResponseDTO getById(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.GET(PATH, "/{id}"), "id", id);
		return service.getResponseById(id);
	}

	@PostMapping
	public Transaction create(@RequestBody TransactionDTO dto) {
		LogHelper.info(log, RouteHelper.POST(PATH), "dto", dto);
	    return service.create(dto);
	}

	@PutMapping("/{id}")
	public Transaction update(@PathVariable Long id, @RequestBody TransactionDTO dto) {
		LogHelper.info(log, RouteHelper.PUT(PATH, "/{id}"), "id", id, "dto", dto);
	    return service.update(id, dto);
	}


	@DeleteMapping("/{id}")
	public void remove(@PathVariable Long id) {
		LogHelper.info(log, RouteHelper.DELETE(PATH), "id", id);
		service.delete(id);
	}

}
