package br.com.gathering.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gathering", schema = "gathering")
public class Gathering {

	public static final int NAME_LENGTH = 20;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gathering.sequence_gathering")
	@SequenceGenerator(name = "gathering.sequence_gathering", sequenceName = "gathering.sequence_gathering", allocationSize = 1)
	private Long id;

	@Column(nullable = false)
	private Integer year;

	@Column(nullable = false, length = NAME_LENGTH)
    private String name;

	public void init() {
		this.year = (this.year == null) ? LocalDateTime.now().getYear() : this.year;
	}

    @Override
    public String toString() {
		return "Gathering: {\n"
				+ "\tid: " + this.id + ",\n"
				+ "\tyear: " + this.year + ",\n"
				+ "\tname: " + this.name + ",\n"
				+ "}";
    }

}
