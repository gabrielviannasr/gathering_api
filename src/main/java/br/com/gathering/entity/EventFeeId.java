package br.com.gathering.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class EventFeeId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "id_event")
    private Long idEvent;

    @Column(name = "players")
    private Integer players;

}