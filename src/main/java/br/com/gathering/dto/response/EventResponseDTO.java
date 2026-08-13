package br.com.gathering.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventResponseDTO {

	private Long id;
	private LocalDateTime createdAt;
	private FormatResponseDTO format;

}
